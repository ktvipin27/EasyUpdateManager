package com.ktvipin27.inappupdate.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin27.inappupdate.InAppUpdateManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        InAppUpdateManager
            .with(this)
            .apply {
                shouldResumeUpdate = true
            }
            .checkUpdate()
    }
}
