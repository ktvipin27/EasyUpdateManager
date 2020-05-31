/*
 * Copyright 2020 Vipin KT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ktvipin.easyupdate

import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.ktvipin.easyupdate.EasyUpdateManager.REQ_CODE_APP_UPDATE
import java.lang.Exception
import java.lang.ref.WeakReference

/**
 * A wrapper for Android In-App Update API.
 * (https://developer.android.com/guide/playcore/in-app-updates)
 *
 * Created by Vipin KT on 08/05/20
 */
class UpdateManager internal constructor(private val activityRef: WeakReference<FragmentActivity>) :
    ContextWrapper(activityRef.get()), LifecycleObserver {

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val stateUpdatedListener = InstallStateUpdatedListener { onStateUpdate(it) }

    private var listener: UpdateListener? = null
    private var updateOptions = UpdateOptions()
    private var snackbarOptions = SnackbarOptions()
    private var isStarted = false

    /**
     * An instance of [Snackbar] with given options.
     */
    private val snackbar: Snackbar? by lazy {
        val rootView =
            activityRef.get()?.window?.decorView?.findViewById<View>(android.R.id.content)
        rootView?.let {
            Snackbar
                .make(it, snackbarOptions.text, Snackbar.LENGTH_INDEFINITE)
                .setAction(snackbarOptions.actionText) { completeUpdate() }
                .setActionTextColor(snackbarOptions.actionTextColor)
                .setTextColor(snackbarOptions.textColor)
        }
    }

    /**
     * For accessing the current options.
     *
     * @return [UpdateOptions]
     */
    fun getOptions() = updateOptions

    /**
     * Use this function to customize [UpdateManager].
     * Lambda alternative for [setOptions]
     *
     * @param block [UpdateOptions] as lambda.
     * @return [UpdateManager]
     */
    fun options(block: UpdateOptions.() -> Unit): UpdateManager {
        block(updateOptions)
        return this
    }

    /**
     * Use this function to customize [UpdateManager].
     *
     * @param options [UpdateOptions]
     * @return [UpdateManager]
     */
    fun setOptions(options: UpdateOptions): UpdateManager {
        updateOptions = options
        return this
    }

    /**
     * Use this function to customize [Snackbar].
     * Lambda alternative for [setSnackbar]
     *
     * @param block [SnackbarOptions]
     * @return [UpdateManager]
     */
    fun snackbar(block: SnackbarOptions.() -> Unit): UpdateManager {
        block(snackbarOptions)
        return this
    }

    /**
     * Use this function to customize [Snackbar].
     *
     * @param options [SnackbarOptions]
     * @return [UpdateManager]
     */
    fun setSnackbar(options: SnackbarOptions): UpdateManager {
        snackbarOptions = options
        return this
    }

    /**
     * Set install updates listener.
     * Lambda alternative for [setListener]
     *
     * @param block lambda function to trigger on listener trigger, has param [InstallState]
     * @return [UpdateManager]
     */
    fun listener(block: (state: com.ktvipin.easyupdate.InstallState) -> Unit): UpdateManager {
        this.listener = object : UpdateListener {
            override fun onStateUpdate(state: com.ktvipin.easyupdate.InstallState) {
                block(state)
            }
        }
        return this
    }

    /**
     * Set install updates listener.
     *
     * @param listener [UpdateListener]
     * @return [UpdateManager]
     */
    fun setListener(listener: UpdateListener): UpdateManager {
        this.listener = listener
        return this
    }

    /**
     * Call this method to start the update process.
     */
    fun startUpdate() {
        isStarted = true
        getAppUpdateInfo()
    }

    /**
     * Call this method to complete the update process.
     * If you are using custom notification, then on user action, call this method to finish the update.
     */
    fun completeUpdate() {
        logD("Completing update...")
        appUpdateManager
            .completeUpdate()
            .addOnCompleteListener {
                if (it.isSuccessful) logD("Update completed.")
                else logD("Update not completed : ${it.exception.message}")
            }
    }

    /**
     * Called at the time of initialization.
     * Registering [com.google.android.play.core.install.InstallStateUpdatedListener] here.
     */
    init {
        logD("Initialising...")
        activityRef.get()?.lifecycle?.addObserver(this)
        appUpdateManager.registerListener(stateUpdatedListener)
    }

    /**
     * Called when activity lifecycle changed to onResume.
     * Resumes the update based on configured [options].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        if (!isStarted) return
        with(updateOptions) {
            when {
                isFlexibleUpdate && resumeUpdate -> updateInfo({
                    if (it.installStatus() == InstallStatus.DOWNLOADED) {
                        logD("Resuming $updateType")
                        if (!customNotification) snackbar?.showIfNotShown()
                    }
                }, {
                    it.printStackTrace()
                    logD("Resuming $updateType failed : ${it.message}")
                })
                isImmediateUpdate && (resumeUpdate || forceUpdate) -> updateInfo({
                    if (it.updateAvailability() in immediateUpdateResumeStates) {
                        logD("Resuming $updateType")
                        requestUpdate(it)
                    }
                }, {
                    it.printStackTrace()
                    logD("Resuming $updateType failed : ${it.message}")
                })
            }
        }
    }

    /**
     * Called when activity lifecycle changed to onDestroy.
     * Unregistering [stateUpdatedListener] here.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        logD("Destroyed : unregistering listener...")
        isStarted = false
        listener = null
        appUpdateManager.unregisterListener(stateUpdatedListener)
    }

    /**
     * Checks for update based on configured [options].
     * calls [requestUpdate] if update available.
     */
    private fun getAppUpdateInfo() {
        logD("Checking updates...")
        updateInfo({ info ->
            val updateDatesSatisfied =
                if (updateOptions.updateType == UpdateType.FLEXIBLE) info.clientVersionStalenessDays() != null
                        && info.clientVersionStalenessDays() >= updateOptions.daysForFlexibleUpdate else true

            logD("getAppUpdateInfo : updateDatesSatisfied = $updateDatesSatisfied")

            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && info.isUpdateTypeAllowed(updateOptions.updateType.value)
            //&& info.updatePriority() >= updateOptions.updatePriority.value
            //&& updateDatesSatisfied
            ) {
                logD("Updates available.")
                // Start an update.
                requestUpdate(info)
            } else logD("No updates available!")
        }, {
            it.printStackTrace()
            logD("Update checking failed : ${it.message}")
        })
    }

    /**
     * Starts the update.
     *
     * @param appUpdateInfo AppUpdateInfo
     */
    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
        logD("Requesting ${updateOptions.updateType} update...")
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            updateOptions.updateType.value,
            activityRef.get(),
            REQ_CODE_APP_UPDATE
        )
    }

    /**
     * Called when there is a change in [InstallState].
     * Triggers [listener] with [state].
     *
     * @param state InstallState
     */
    private fun onStateUpdate(state: InstallState) {
        val installState = InstallState(state)
        logD("Install state changed to ${installState.currentStatus}")

        listener?.onStateUpdate(installState)

        if (updateOptions.isFlexibleUpdate
            && state.installStatus() == InstallStatus.DOWNLOADED
            && !updateOptions.customNotification
        ) snackbar?.showIfNotShown()
    }

    /**
     * Check the updates.
     *
     * @param onSuccess to called if the check update is success.
     * @param onFailed to called if the check update is failed.
     */
    private fun updateInfo(onSuccess: (AppUpdateInfo) -> Unit, onFailed: (Exception) -> Unit) {
        appUpdateManager.appUpdateInfo.addOnCompleteListener {
            if (it.isSuccessful) onSuccess(it.result) else onFailed(it.exception)
        }
    }

    /**
     * Provides a singleton instance of [UpdateManager]
     */
    companion object :
        SingletonHolder<UpdateManager, WeakReference<FragmentActivity>>(::UpdateManager)
}