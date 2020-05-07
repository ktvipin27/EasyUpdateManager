package com.ktvipin27.inappupdate

import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.InstallStatus

/**
 * A wrapper class for InstallState
 */
class InAppInstallState(private val installState: InstallState) {

    val isDownloading: Boolean
        get() = installState.installStatus() == InstallStatus.DOWNLOADING

    val isDownloaded: Boolean
        get() = installState.installStatus() == InstallStatus.DOWNLOADED

    val isFailed: Boolean
        get() = installState.installStatus() == InstallStatus.FAILED

    val isInstalled: Boolean
        get() = installState.installStatus() == InstallStatus.INSTALLED

    val isInstalling: Boolean
        get() = installState.installStatus() == InstallStatus.INSTALLING

    val isCanceled: Boolean
        get() = installState.installStatus() == InstallStatus.CANCELED

    val isPending: Boolean
        get() = installState.installStatus() == InstallStatus.PENDING

    val isUnknown: Boolean
        get() = installState.installStatus() == InstallStatus.UNKNOWN

    val bytesDownloaded: Long
        get() = installState.bytesDownloaded()

    val totalBytesToDownload: Long
        get() = installState.totalBytesToDownload()

    val installErrorCode: Int
        get() = installState.installErrorCode()
}