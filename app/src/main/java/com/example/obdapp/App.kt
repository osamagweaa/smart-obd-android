package com.example.obdapp

import android.app.Application
import com.example.obdapp.BuildConfig
import dagger.hilt.android.HiltAndroidApp

// TODO: Add google-services.json from Firebase Console before release
@HiltAndroidApp
class App: Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (!BuildConfig.DEBUG) {
            com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance()
                .setCrashlyticsCollectionEnabled(true)
        }
    }
}
