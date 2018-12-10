package org.zubbl.dependencies.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import org.zubbl.configs.Constant
import org.zubbl.configs.SessionManager
import org.zubbl.customviews.CustomDialog
import org.zubbl.model.common.JsonResp
import org.zubbl.utils.CommonMethods
import org.zubbl.utils.NetworkUtils
import org.zubbl.utils.RunTimePermission
import org.zubbl.utils.Validator
import javax.inject.Singleton

@Module(includes = [(ApplicationModule::class)])
class AppContainerModule {
    @Provides
    @Singleton
    fun providesSharedPreference(application: Application): SharedPreferences {
        return application.getSharedPreferences(Constant.APP_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providesContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesSessionManager(): SessionManager {
        return SessionManager()
    }
    @Provides
    @Singleton
    fun providesNetworkUtils(application: Application): NetworkUtils {
        return NetworkUtils(application)
    }

    @Provides
    @Singleton
    fun providesValidator(): Validator {
        return Validator()
    }

    @Provides
    @Singleton
    fun providesCommonMethods(): CommonMethods {
        return CommonMethods()
    }
    //
    @Provides
    @Singleton
    fun providesStringObjectsHashMap(): HashMap<String, Any> {
        return HashMap()
    }

    @Provides
    @Singleton
    fun providesCustomDialog(): CustomDialog {
        return CustomDialog()
    }


    @Provides
    @Singleton
    internal fun providesJsonResp(): JsonResp {
        return JsonResp(url = "", method = "", strRequest = "", strResponse = "", responseCode = 0, requestCode = 0, errorMsg = "", requestData = "", isOnline = false, headers = null)
    }


    @Provides
    @Singleton
    fun providesRunTimePermission(application: Application): RunTimePermission {
        return RunTimePermission(application.applicationContext)
    }


}