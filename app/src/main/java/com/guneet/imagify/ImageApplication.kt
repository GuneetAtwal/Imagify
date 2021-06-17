package com.guneet.imagify

import android.app.Application
import com.guneet.imagify.di.repoModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ImageApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ImageApplication)
            modules(repoModule)
        }
    }
}