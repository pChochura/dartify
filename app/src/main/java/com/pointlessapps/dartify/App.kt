package com.pointlessapps.dartify

import android.app.Application
import com.pointlessapps.dartify.di.applicationModules
import com.pointlessapps.dartify.domain.di.domainModules
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            modules(
                applicationModules +
                        domainModules,
            )
        }
    }
}
