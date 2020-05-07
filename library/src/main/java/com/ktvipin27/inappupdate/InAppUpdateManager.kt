package com.ktvipin27.inappupdate

import android.content.ContextWrapper
import android.graphics.Color
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
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * A simple implementation of the Android In-App Update API.
 *
 */
class InAppUpdateManager private constructor(private val activity: AppCompatActivity) :
    ContextWrapper(activity), LifecycleObserver {

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val stateUpdatedListener = InstallStateUpdatedListener { onStateUpdate(it) }

    private var _updateType = AppUpdateType.FLEXIBLE
    private var _shouldResumeUpdate = true
    private var _listener: ((status: InAppInstallStatus) -> Unit)? = null

    private var _shouldShowSnackbar = true
    private var _snackbarText = "An update has just been downloaded."
    private var _snackbarAction = "RESTART"
    private var _snackbarTextColor = Color.WHITE
    private var _snackbarActionTextColor = Color.RED
    private val snackbar: Snackbar by lazy {
        val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        Snackbar
            .make(rootView, _snackbarText, Snackbar.LENGTH_INDEFINITE)
            .setAction(_snackbarAction) { completeUpdate() }
            .setActionTextColor(_snackbarActionTextColor)
            .setTextColor(_snackbarTextColor)
    }

    var updateType
        get() =
            if (_updateType == AppUpdateType.FLEXIBLE)
                InAppUpdateType.FLEXIBLE
            else
                InAppUpdateType.IMMEDIATE
        set(value) {
            _updateType =
                if (value == InAppUpdateType.FLEXIBLE)
                    AppUpdateType.FLEXIBLE
                else
                    AppUpdateType.IMMEDIATE
        }

    var shouldResumeUpdate
        get() = _shouldResumeUpdate
        set(value) {
            _shouldResumeUpdate = value
        }

    var shouldShowSnackbar
        get() = _shouldShowSnackbar
        set(value) {
            _shouldShowSnackbar = value
        }

    var snackbarText
        get() = _snackbarText
        set(value) {
            _snackbarText = value
        }

    var snackbarAction
        get() = _snackbarAction
        set(value) {
            _snackbarAction = value
        }

    var listener
        get() = _listener
        set(value) {
            _listener = value
        }


    init {
        activity.lifecycle.addObserver(this)
        appUpdateManager.registerListener(stateUpdatedListener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        if (_shouldResumeUpdate)
            resumeUpdate()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        appUpdateManager.unregisterListener(stateUpdatedListener)
    }

    fun startUpdate() = getAppUpdateInfo()

    fun completeUpdate() = appUpdateManager.completeUpdate()

    private fun onStateUpdate(state: InstallState) {
        Log.d(TAG, "onStateUpdate(): installStatus: %s ${state.installStatus()}")
        _listener?.invoke(InAppInstallStatus(state))

        if (state.installStatus() == InstallStatus.FAILED)
            Log.d(TAG, "onStateUpdate(): failed: %s ${state.installErrorCode()}")


        if (_updateType == AppUpdateType.FLEXIBLE && state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            if (_shouldShowSnackbar) snackbar.show()
        }
    }

    private fun getAppUpdateInfo() {

        // Checks that the platform will allow the specified type of update.
        appUpdateManager.appUpdateInfo.addOnCompleteListener { task ->

            if (task.isSuccessful) {

                val appUpdateInfo = task.result

                when {
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE -> {
                        // Request the update.
                        if (appUpdateInfo.isUpdateTypeAllowed(_updateType)) {
                            // Start an update.
                            startUpdate(appUpdateInfo)
                        }

                        Log.d(
                            TAG,
                            "getAppUpdateInfo(): Update available. Version Code: %s ${appUpdateInfo.availableVersionCode()}"
                        )
                    }
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                        Log.d(
                            TAG,
                            "getAppUpdateInfo(): No Update available. Code: %s ${appUpdateInfo.availableVersionCode()}"
                        )
                    }
                }

            } else {
                Log.d(TAG, "getAppUpdateInfo(): Update Failed : %s ${task.exception.message}")
            }
        }

    }

    private fun resumeUpdate() {
        // Checks that the platform will allow the specified type of update.
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            Log.d(
                TAG,
                "resumeUpdate(): resuming update. Code: %s ${appUpdateInfo.updateAvailability()}"
            )
            if (_updateType == AppUpdateType.IMMEDIATE &&
                appUpdateInfo.updateAvailability() in listOf(
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS,
                    UpdateAvailability.UPDATE_AVAILABLE
                )
            ) {
                // If an in-app update is already running, resume the update.
                startUpdate(appUpdateInfo)
                Log.d(TAG, "resumeUpdate(): resuming immediate update.")
            }
            if (_updateType == AppUpdateType.FLEXIBLE &&
                appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED
            ) {
                if (_shouldShowSnackbar) snackbar.show()
                Log.d(TAG, "resumeUpdate(): resuming flexible update")
            }
        }

    }

    private fun startUpdate(appUpdateInfo: AppUpdateInfo?) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            _updateType,
            activity,
            REQ_CODE_APP_UPDATE
        )
    }

    companion object {
        const val REQ_CODE_APP_UPDATE = 54321

        fun with(activity: AppCompatActivity) = InAppUpdateManager(activity)
        private const val TAG = "InAppUpdateManager"
    }
}