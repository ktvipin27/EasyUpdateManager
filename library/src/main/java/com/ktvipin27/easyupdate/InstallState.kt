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

package com.ktvipin27.easyupdate

import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.InstallStatus

/**
 * A wrapper class for [InstallState]
 *
 * Created by Vipin KT on 7/05/20
 */
class InstallState(private val installState: InstallState) {

    val installStatus: Int
        get() = installState.installStatus()

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