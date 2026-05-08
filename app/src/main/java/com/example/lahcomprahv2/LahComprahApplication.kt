package com.example.lahcomprahv2

import android.app.Application
import com.example.lahcomprahv2.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LahComprahApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LahComprahApplication)
            modules(appModule)
        }
    }
}
