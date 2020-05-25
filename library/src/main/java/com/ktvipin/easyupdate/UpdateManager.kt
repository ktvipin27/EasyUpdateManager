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
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import java.lang.ref.WeakReference

/**
 * A wrapper for Android In-App Update API.
 * (https://developer.android.com/guide/playcore/in-app-updates)
 *
 * Created by Vipin KT on 08/05/20
 */
class UpdateManager internal constructor(private val activityRef: WeakReference<AppCompatActivity>) :
    ContextWrapper(activityRef.get()), LifecycleObserver {

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val stateUpdatedListener = InstallStateUpdatedListener { onStateUpdate(it) }

    private var listener: ((state: com.ktvipin.easyupdate.InstallState) -> Unit) = {}
    private var listenerJava: UpdateListener? = null
    private var updateOptions = UpdateOptions()
    private var snackbarOptions = SnackbarOptions()

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
     * Use this lambda function to customize [UpdateManager].
     *
     * @param block [UpdateOptions]
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
     * Use this lambda function to customize [SnackbarOptions].
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
     * Install updates will be delivered through this function.
     *
     * @param block [InstallState]
     * @return [UpdateManager]
     */
    fun listener(block: (state: com.ktvipin.easyupdate.InstallState) -> Unit): UpdateManager {
        listener = block
        return this
    }

    /**
     * Install updates will be delivered through this function.
     *
     * @param listener [InstallState]
     * @return [UpdateManager]
     */
    fun setListener(listener: UpdateListener): UpdateManager {
        listenerJava = listener
        return this
    }

    /**
     * Call this method to start the update process.
     */
    fun startUpdate() = getAppUpdateInfo()

    /**
     * Call this method to complete the update process.
     * If you are using custom notification, then on user action, call this method to finish the update.
     */
    fun completeUpdate() = appUpdateManager.completeUpdate().also { logD("completeUpdate") }

    /**
     * Called at the time of initialization.
     * Registering [com.google.android.play.core.install.InstallStateUpdatedListener] here.
     */
    init {
        activityRef.get()?.lifecycle?.addObserver(this)
        appUpdateManager.registerListener(stateUpdatedListener)
        logD("Initialised")
    }

    /**
     * Called when activity lifecycle changed to onResume.
     * Resumes the update based on configured [options].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        logD("onResume : ")
        when {
            updateOptions.isFlexibleUpdate && updateOptions.resumeUpdate ->
                appUpdateManager.appUpdateInfo.addOnSuccessListener {
                    val isDownloaded = it.installStatus() == InstallStatus.DOWNLOADED
                    logD("onResume : FlexibleUpdate, downloaded = $isDownloaded")
                    if (isDownloaded && !updateOptions.customNotification) snackbar?.show()
                }
            updateOptions.isImmediateUpdate && (updateOptions.resumeUpdate || !updateOptions.forceUpdate) ->
                appUpdateManager.appUpdateInfo.addOnSuccessListener {
                    logD("onResume : ImmediateUpdate updateAvailability = ${it.updateAvailability()}")
                    if (it.updateAvailability() in updateOptions.immediateUpdateResumeStates) requestUpdate(
                        it
                    )
                }
        }
    }

    /**
     * Called when activity lifecycle changed to onDestroy.
     * Unregistering [stateUpdatedListener] here.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        logD("onDestroyed : unregistering listener")
        appUpdateManager.unregisterListener(stateUpdatedListener)
    }

    /**
     * Checks for update based on configured [options].
     * calls [requestUpdate] if update available.
     */
    private fun getAppUpdateInfo() {
        logD("getAppUpdateInfo : checking update")
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            val updateDatesSatisfied =
                if (updateOptions.updateType == UpdateType.FLEXIBLE) it.clientVersionStalenessDays() != null
                        && it.clientVersionStalenessDays() >= updateOptions.daysForFlexibleUpdate else true

            logD("getAppUpdateInfo : updateDatesSatisfied = $updateDatesSatisfied")

            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && it.isUpdateTypeAllowed(updateOptions.updateType.value)
                && it.updatePriority() >= updateOptions.updatePriority.value
                && updateDatesSatisfied
            ) {
                logD("getAppUpdateInfo : updateConditionsSatisfied = true")
                // Start an update.
                requestUpdate(it)
            } else logD("getAppUpdateInfo : updateConditionsSatisfied = false")
        }
    }

    /**
     * Starts the update.
     *
     * @param appUpdateInfo AppUpdateInfo
     */
    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
        logD("requestUpdate : starting UpdateFlowForResult")
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
        logD("onStateUpdate : install state changed, state = $state")
        listener.invoke(InstallState(state))
        listenerJava?.onStateUpdate(InstallState(state))

        if (updateOptions.isFlexibleUpdate && state.installStatus() == InstallStatus.DOWNLOADED
            && !updateOptions.customNotification
        ) snackbar?.show()
    }

    /**
     * Display debug log with [EasyUpdateManager.TAG] and [message].
     *
     * @param message message to display in log.
     */
    private fun logD(message: String) = Log.d(EasyUpdateManager.TAG, message)
}