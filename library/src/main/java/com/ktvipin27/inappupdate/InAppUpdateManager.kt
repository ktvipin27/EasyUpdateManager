package com.ktvipin27.inappupdate

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
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * A simple implementation of the Android In-App Update API.
 *
 */
class InAppUpdateManager private constructor(private val activity: AppCompatActivity) :
    ContextWrapper(activity), LifecycleObserver, InstallStateUpdatedListener {

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private var updateType = AppUpdateType.IMMEDIATE

    private var installStateUpdatedListener: ((state: InstallState?) -> Unit)? = null

    private val snackbar: Snackbar by lazy {
        val rootView = activity.window.decorView.findViewById<View>(android.R.id.content)

        Snackbar.make(
            rootView,
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("RESTART") {
            // Triggers the completion of the update of the app for the flexible flow.
            appUpdateManager.completeUpdate()
        }
    }

    init {
        activity.lifecycle.addObserver(this)
        appUpdateManager.registerListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        resumeUpdate()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        appUpdateManager.unregisterListener(this)
    }

    override fun onStateUpdate(state: InstallState) {
        Log.d(TAG, "onStateUpdate(): installStatus: %s ${state.installStatus()}")
        if (state.installStatus() == InstallStatus.FAILED)
            Log.d(TAG, "onStateUpdate(): failed: %s ${state.installErrorCode()}")

        installStateUpdatedListener?.invoke(state)

        if (updateType == AppUpdateType.FLEXIBLE && state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            snackbar.show()
        }
    }

    fun updateType(type: Int): InAppUpdateManager {
        updateType = type
        return this
    }

    fun listener(listener: (state: InstallState?) -> Unit): InAppUpdateManager {
        installStateUpdatedListener = listener
        return this
    }

    fun checkUpdate() = getAppUpdateInfo()

    private fun getAppUpdateInfo() {

        // Checks that the platform will allow the specified type of update.
        appUpdateManager.appUpdateInfo.addOnCompleteListener { task ->

            if (task.isSuccessful) {

                val appUpdateInfo = task.result

                when {
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE -> {
                        // Request the update.
                        if (appUpdateInfo.isUpdateTypeAllowed(updateType)) {
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
            if (updateType == AppUpdateType.IMMEDIATE &&
                appUpdateInfo.updateAvailability() in listOf(
                    UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS,
                    UpdateAvailability.UPDATE_AVAILABLE
                )
            ) {
                // If an in-app update is already running, resume the update.
                startUpdate(appUpdateInfo)
                Log.d(TAG, "resumeUpdate(): resuming immediate update.")
            }
            if (updateType == AppUpdateType.FLEXIBLE &&
                appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED
            ) {
                snackbar.show()
                Log.d(TAG, "resumeUpdate(): resuming flexible update")
            }
        }

    }

    private fun startUpdate(appUpdateInfo: AppUpdateInfo?) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            updateType,
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