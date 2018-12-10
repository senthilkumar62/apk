package org.zubbl.application

import android.app.Application

import org.zubbl.configs.Constant
import org.zubbl.dependencies.component.AppComponent
import org.zubbl.dependencies.component.DaggerAppComponent
import org.zubbl.dependencies.module.ApplicationModule
import org.zubbl.dependencies.module.NetworkModule


class AppController : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        //DAGGER COMPONENT
        appComponent = DaggerAppComponent.builder()
                .applicationModule(ApplicationModule(this))
                .networkModule(NetworkModule(Constant.BASE_URL))
                .build()
    }
}