package com.ktvipin.easyupdate

import android.util.Log
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Vipin KT on 30/05/20
 */

fun Snackbar.showIfNotShown(){
    if (!isShown) show()
}

/**
 * Display debug log with [EasyUpdateManager.TAG] and [message].
 *
 * @param message message to display in log.
 */
fun Any.logD(message: String) = Log.d(EasyUpdateManager.TAG, message)