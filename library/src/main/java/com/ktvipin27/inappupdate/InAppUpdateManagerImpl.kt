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
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.ktvipin27.inappupdate.InAppUpdateManager.REQ_CODE_APP_UPDATE

/**
 * A simple implementation of the Android In-App Update API.
 *
 */
class InAppUpdateManagerImpl internal constructor(private val activity: AppCompatActivity) :
    ContextWrapper(activity), LifecycleObserver {

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val stateUpdatedListener = InstallStateUpdatedListener { onStateUpdate(it) }
    val inAppSnackbar: InAppSnackbar = InAppSnackbar(activity) { completeUpdate() }

    private var _resumeUpdate = true
    private var _updateType = InAppUpdateType.FLEXIBLE
    private var listener: ((state: InAppInstallState) -> Unit) = {}

    var updateType
        get() = _updateType
        set(value) {
            _updateType = value
        }

    var resumeUpdate
        get() = _resumeUpdate
        set(value) {
            _resumeUpdate = value
        }

    inline fun snackbar(block: InAppSnackbar.() -> Unit): InAppUpdateManagerImpl {
        block(inAppSnackbar)
        return this
    }

    fun listener(block: (state: InAppInstallState) -> Unit): InAppUpdateManagerImpl {
        listener = block
        return this
    }

    init {
        activity.lifecycle.addObserver(this)
        appUpdateManager.registerListener(stateUpdatedListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        if (_resumeUpdate)
            resumeUpdate()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        appUpdateManager.unregisterListener(stateUpdatedListener)
    }

    fun startUpdate() = getAppUpdateInfo()

    fun completeUpdate() = appUpdateManager.completeUpdate()

    private fun getAppUpdateInfo() {
        // Checks that the platform will allow the specified type of update.
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(_updateType.value)
            ) {
                // Start an update.
                requestUpdate(it)
            }
        }
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            _updateType.value,
            activity,
            REQ_CODE_APP_UPDATE
        )
    }

    private fun resumeUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            when {
                _updateType.value == AppUpdateType.IMMEDIATE && it.updateAvailability() in listOf(
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS,
                    UpdateAvailability.UPDATE_AVAILABLE
                ) -> requestUpdate(it)

                _updateType.value == AppUpdateType.FLEXIBLE &&
                        it.installStatus() == InstallStatus.DOWNLOADED ->
                    inAppSnackbar.show()
            }
        }
    }

    private fun onStateUpdate(state: InstallState) {
        listener.invoke(InAppInstallState(state))

        if (_updateType.value == AppUpdateType.FLEXIBLE &&
            state.installStatus() == InstallStatus.DOWNLOADED
        ) {
            // After the update is downloaded, show a snackbar
            // and request user confirmation to restart the app.
            inAppSnackbar.show()
        }
    }
}