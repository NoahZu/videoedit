package io.github.noahzu.videoedit

import android.app.Application
import android.content.Context

class VideoEditApplication : Application() {

    companion object {
        fun getAppContext(): Context = instance.applicationContext

        lateinit var instance : VideoEditApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}