package com.ktvipin27.inappupdate

import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.ktvipin27.inappupdate.InAppUpdateManager.REQ_CODE_APP_UPDATE
import java.lang.ref.WeakReference

/**
 * A simple implementation of the Android In-App Update API.
 *
 */
class InAppUpdateManagerImpl internal constructor(private val activityRef: WeakReference<AppCompatActivity>) :
    ContextWrapper(activityRef.get()), LifecycleObserver {

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val stateUpdatedListener = InstallStateUpdatedListener { onStateUpdate(it) }

    private val inAppSnackbar: InAppSnackbar = InAppSnackbar(activityRef) { completeUpdate() }
    private var listener: ((state: InAppInstallState) -> Unit) = {}
    private val options = InAppUpdateOptions()

    fun options(block: InAppUpdateOptions.() -> Unit): InAppUpdateManagerImpl {
        block(options)
        return this
    }

    fun snackbar(block: InAppSnackbar.() -> Unit): InAppUpdateManagerImpl {
        block(inAppSnackbar)
        return this
    }

    fun listener(block: (state: InAppInstallState) -> Unit): InAppUpdateManagerImpl {
        listener = block
        return this
    }

    fun startUpdate() = getAppUpdateInfo()

    fun completeUpdate() = appUpdateManager.completeUpdate()

    init {
        activityRef.get()?.lifecycle?.addObserver(this)
        appUpdateManager.registerListener(stateUpdatedListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        when {
            options.isFlexibleUpdate && options.resumeUpdate ->
                appUpdateManager.appUpdateInfo.addOnSuccessListener {
                    if (it.installStatus() == InstallStatus.DOWNLOADED && !options.customNotification) inAppSnackbar.show()
                }
            options.isImmediateUpdate && (options.resumeUpdate || !options.forceUpdateCancellable) ->
                appUpdateManager.appUpdateInfo.addOnSuccessListener {
                    if (it.updateAvailability() in options.immediateUpdateResumeStates) requestUpdate(
                        it
                    )
                }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        appUpdateManager.unregisterListener(stateUpdatedListener)
    }

    private fun getAppUpdateInfo() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            val updateDatesSatisfied =
                if (options.updateType == InAppUpdateType.FLEXIBLE) it.clientVersionStalenessDays() != null
                        && it.clientVersionStalenessDays() >= options.daysForFlexibleUpdate else true
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && it.isUpdateTypeAllowed(options.updateType.value)
                && it.updatePriority() >= options.updatePriority.value
                && updateDatesSatisfied
            ) {
                // Start an update.
                requestUpdate(it)
            }
        }
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            options.updateType.value,
            activityRef.get(),
            REQ_CODE_APP_UPDATE
        )
    }

    private fun onStateUpdate(state: InstallState) {
        listener.invoke(InAppInstallState(state))

        if (options.isFlexibleUpdate && state.installStatus() == InstallStatus.DOWNLOADED
            && !options.customNotification
        ) inAppSnackbar.show()
    }
}