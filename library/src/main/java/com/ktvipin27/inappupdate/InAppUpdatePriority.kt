package com.ktvipin27.inappupdate

import com.google.android.play.core.install.model.AppUpdateType

/**
 * Identifiers for the different types of developer triggered updates.
 */
enum class InAppUpdatePriority(val value: Int) {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
}