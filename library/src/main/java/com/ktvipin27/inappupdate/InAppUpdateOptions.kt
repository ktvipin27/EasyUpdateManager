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

package com.ktvipin27.inappupdate

import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * A set of customization options for [InAppUpdateManager]
 *
 * Created by Vipin KT on 19/05/20
 */
data class InAppUpdateOptions(
    /**
     * Whether to resume update or not.
     */
    var resumeUpdate: Boolean = true,
    /**
     * Type of update. [InAppUpdateType].
     */
    var updateType: InAppUpdateType = InAppUpdateType.FLEXIBLE,
    /**
     * Update priority , integer value between 0 and 5, must be one of the  [InAppUpdatePriority]
     */
    var updatePriority: InAppUpdatePriority = InAppUpdatePriority.ONE,
    /**
     * Days to wait before notifying the user with a flexible update.
     */
    var daysForFlexibleUpdate: Int = 0,
    /**
     * Pass true, if you want to show custom notification.
     * [InAppSnackbar] will not show if set to true.
     */
    var customNotification: Boolean = false
) {

    /**
     * Stores a list of [UpdateAvailability], Immediate update will be resumed based on this states.
     */
    val immediateUpdateResumeStates = mutableSetOf(
        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS,
        UpdateAvailability.UPDATE_AVAILABLE
    )

    /**
     * Whether to allow cancellation of force update on clicking close button.
     * [immediateUpdateResumeStates] will be updated based on this value.
     */
    var forceUpdateCancellable = false
        set(value) {
            field = value
            if (value)
                immediateUpdateResumeStates.remove(UpdateAvailability.UPDATE_AVAILABLE)
            else
                immediateUpdateResumeStates.add(UpdateAvailability.UPDATE_AVAILABLE)
        }

    /**
     * Returns true if AppUpdateType is FLEXIBLE
     */
    internal val isFlexibleUpdate = updateType.value == AppUpdateType.FLEXIBLE

    /**
     * Returns true if AppUpdateType is IMMEDIATE
     */
    internal val isImmediateUpdate = updateType.value == AppUpdateType.IMMEDIATE
}