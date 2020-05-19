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
    var resumeUpdate: Boolean = true,
    var updateType: InAppUpdateType = InAppUpdateType.FLEXIBLE,
    var updatePriority: InAppUpdatePriority = InAppUpdatePriority.ONE,
    var daysForFlexibleUpdate: Int = 0,
    var customNotification: Boolean = false
) {

    val immediateUpdateResumeStates = mutableSetOf(
        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS,
        UpdateAvailability.UPDATE_AVAILABLE
    )

    var forceUpdateCancellable = false
        set(value) {
            field = value
            if (value)
                immediateUpdateResumeStates.remove(UpdateAvailability.UPDATE_AVAILABLE)
            else
                immediateUpdateResumeStates.add(UpdateAvailability.UPDATE_AVAILABLE)
        }

    internal val isFlexibleUpdate = updateType.value == AppUpdateType.FLEXIBLE
    internal val isImmediateUpdate = updateType.value == AppUpdateType.IMMEDIATE
}