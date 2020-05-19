package com.ktvipin27.inappupdate

import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

object InAppUpdateManager {

    internal const val REQ_CODE_APP_UPDATE = 54321

    fun with(activity: AppCompatActivity) = InAppUpdateManagerImpl(WeakReference(activity))
}