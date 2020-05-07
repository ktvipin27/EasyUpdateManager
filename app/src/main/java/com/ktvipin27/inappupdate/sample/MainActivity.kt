package com.ktvipin27.inappupdate.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin27.inappupdate.InAppUpdateManager
import com.ktvipin27.inappupdate.InAppUpdateType

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        InAppUpdateManager
            .with(this)
            .apply {
                updateType = InAppUpdateType.FLEXIBLE
                shouldResumeUpdate = true
                listener = {
                    
                }
            }
            .checkUpdate()
    }
}
