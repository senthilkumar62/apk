package org.zubbl.dependencies.module

import android.app.Application
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(app: Application){
    private var application: Application = app

    @Provides
    fun application(): Application {
        return application
    }
}