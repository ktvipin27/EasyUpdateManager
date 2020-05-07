package com.ktvipin27.inappupdate

import android.content.ContextWrapper
import android.graphics.Color
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

    private fun getAppUpdateInfo() {
        // Checks that the platform will allow the specified type of update.
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(_updateType)
            ) {
                // Start an update.
                requestUpdate(it)
            }
        }
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            _updateType,
            activity,
            REQ_CODE_APP_UPDATE
        )
    }

    private fun resumeUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            when {
                _updateType == AppUpdateType.IMMEDIATE && it.updateAvailability() in listOf(
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS,
                    UpdateAvailability.UPDATE_AVAILABLE
                ) -> requestUpdate(it)

                _updateType == AppUpdateType.FLEXIBLE &&
                        it.installStatus() == InstallStatus.DOWNLOADED && _shouldShowSnackbar ->
                    snackbar.show()
            }
        }
    }

    private fun onStateUpdate(state: InstallState) {
        _listener?.invoke(InAppInstallStatus(state))

        if (_updateType == AppUpdateType.FLEXIBLE &&
            state.installStatus() == InstallStatus.DOWNLOADED
        ) {
            // After the update is downloaded, show a snackbar
            // and request user confirmation to restart the app.
            if (_shouldShowSnackbar) snackbar.show()
        }
    }

    companion object {
        const val REQ_CODE_APP_UPDATE = 54321

        fun with(activity: AppCompatActivity) = InAppUpdateManager(activity)
    }
}