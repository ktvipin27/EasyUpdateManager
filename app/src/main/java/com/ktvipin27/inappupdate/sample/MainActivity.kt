package com.ktvipin27.inappupdate.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ktvipin27.inappupdate.InAppUpdateManager
import com.ktvipin27.inappupdate.InAppUpdateType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1.setOnClickListener {
            startFlexibleUpdate()
        }

        button2.setOnClickListener {
            startImmediateUpdate()
        }
    }

    private fun startFlexibleUpdate() {
        InAppUpdateManager
            .with(this)
            .startUpdate()
    }

    private fun startImmediateUpdate() {
        InAppUpdateManager
            .with(this)
            .apply {
                updateType = InAppUpdateType.IMMEDIATE
            }
            .startUpdate()
    }
}
