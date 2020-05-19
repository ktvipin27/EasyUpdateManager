package com.ktvipin27.inappupdate

import android.content.ContextWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference

/**
 * Created by Vipin KT on 18/05/20
 */
class InAppSnackbar(activityRef: WeakReference<AppCompatActivity>, onClickAction: () -> Unit) :
    ContextWrapper(activityRef.get()) {

    private var _enabled = true
    private var _snackbarText = getString(R.string.message_update_downloaded)
    private var _snackbarAction = getString(R.string.action_restart)
    private var _snackbarTextColor = ContextCompat.getColor(this, R.color.color_snackbar_text)
    private var _snackbarActionTextColor =
        ContextCompat.getColor(this, R.color.color_snackbar_action)

    private val snackbar: Snackbar? by lazy {
        val rootView = activityRef.get()?.window?.decorView?.findViewById<View>(android.R.id.content)
        rootView?.let {
            Snackbar
                .make(it, _snackbarText, Snackbar.LENGTH_INDEFINITE)
                .setAction(_snackbarAction) { onClickAction() }
                .setActionTextColor(_snackbarActionTextColor)
                .setTextColor(_snackbarTextColor)
        }
    }

    var text
        get() = _snackbarText
        set(value) {
            _snackbarText = value
        }

    var textColor
        get() = _snackbarTextColor
        set(value) {
            _snackbarTextColor = value
        }

    var actionText
        get() = _snackbarAction
        set(value) {
            _snackbarAction = value
        }

    var actionTextColor
        get() = _snackbarActionTextColor
        set(value) {
            _snackbarActionTextColor = value
        }

    var enabled
        get() = _enabled
        set(value) {
            _enabled = value
        }

    internal fun show() {
        if (_enabled)
            snackbar?.show()
    }

}