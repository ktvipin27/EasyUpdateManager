package com.ktvipin27.inappupdate

import com.google.android.play.core.install.model.AppUpdateType

/**
 * Identifiers for the different types of developer triggered updates.
 */
enum class InAppUpdateType(val value: Int) {
    FLEXIBLE(AppUpdateType.FLEXIBLE),
    IMMEDIATE(AppUpdateType.IMMEDIATE)
}