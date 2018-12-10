package org.zubbl.dependencies.module

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.zubbl.configs.SessionManager
import org.zubbl.dependencies.interceptors.AuthTokenInterceptor
import org.zubbl.service.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.*

@Module
class NetworkModule(baseUrl: String) {
    private var mBaseUrl: String = baseUrl

    @Provides
    @Singleton
    internal fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        return loggingInterceptor
    }

    @Provides
    @Singleton
    internal fun providesGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }
//
//    @Provides
//    @Singleton
//    internal fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor,
//                                      sessionManager: SessionManager): OkHttpClient.Builder {
//        return getUnsafeOkHttpClient(httpLoggingInterceptor, sessionManager)
//    }
    @Provides
    @Singleton
    internal fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor,
                                      sessionManager: SessionManager): OkHttpClient.Builder {
        val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
        client.addInterceptor(httpLoggingInterceptor)
        client.addInterceptor(AuthTokenInterceptor(sessionManager))

        return client
    }

    @Provides
    @Singleton
    fun providesRetrofitService(okHttpClient: OkHttpClient.Builder, gson: Gson): Retrofit {
        return Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient.build())
                .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    public fun getUnsafeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor,
                                     sessionManager: SessionManager): OkHttpClient.Builder {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>,
                                                authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>,
                                                authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls<X509Certificate>(0)
                }
            })
            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.getSocketFactory()



            return OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(AuthTokenInterceptor(sessionManager))
                    .hostnameVerifier(object : HostnameVerifier {
                        override fun verify(hostname: String, session: SSLSession): Boolean {
                            //val hv = HttpsURLConnection.getDefaultHostnameVerifier()
                            //return hv.verify("78.100.117.115", session)
                            return true
                        }
                    })


        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}