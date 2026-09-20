package com.gzpu.livesdk.demo

import android.app.Application
import com.gzpu.livesdk.LiveSdk
import com.gzpu.livesdk.LiveSdkConfig

class DemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        LiveSdk.initialize(this, LiveSdkConfig())
    }
}
