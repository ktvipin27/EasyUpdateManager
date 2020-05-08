package com.ktvipin27.inappupdate

import androidx.appcompat.app.AppCompatActivity

class InAppUpdateManager {

    companion object {
        const val REQ_CODE_APP_UPDATE = 54321

        fun with(activity: AppCompatActivity) = InAppUpdateManagerImpl(activity)
    }
}