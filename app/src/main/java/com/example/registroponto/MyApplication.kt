package com.example.registroponto

import android.app.Application
import com.example.registroponto.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RegistroPontoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RegistroPontoApp)
            modules(appModule)
        }
    }
}