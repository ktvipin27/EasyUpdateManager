package com.ktvipin27.inappupdate

import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Created by Vipin KT on 19/05/20
 */
data class InAppUpdateOptions(
    var resumeUpdate: Boolean = true,
    var updateType: InAppUpdateType = InAppUpdateType.FLEXIBLE,
    var updatePriority: InAppUpdatePriority = InAppUpdatePriority.ONE,
    var daysForFlexibleUpdate: Int = 0
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
}