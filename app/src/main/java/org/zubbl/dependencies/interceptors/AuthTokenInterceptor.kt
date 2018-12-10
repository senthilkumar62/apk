package org.zubbl.dependencies.interceptors

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.zubbl.configs.Constant
import org.zubbl.configs.SessionManager
import java.io.IOException

class AuthTokenInterceptor(private val sessionManager: SessionManager) : Interceptor {
    private lateinit var requestBuilder: Request.Builder

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val original = chain.request()

            requestBuilder = if (sessionManager.getToken() != "") {
                // Request customization: add request headers
                original.newBuilder()
                        .header("Authorization", "" + sessionManager.getToken())
                        .method(original.method(), original.body())
            } else {
                // Request customization: add request headers
                original.newBuilder()
                        .method(original.method(), original.body())
            }
        } catch (e: Exception) {
            Log.e(Constant.LOGGER_TAG, "Received an exception", e)
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}