package com.hossamelsharkawy.req

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Hossam Elsharkawy
0201099197556
on 6/11/2018.  time :17:16

 */
object Server {

    lateinit var mBaseUrl: String


    const val serverErrorMsg = "Server ErrorMsg"
    const val invalid_credentialsMsg = "Invalid Credentials"
    const val noInternetConnectionMsg = "No Internet Connection"
    const val socketTimeoutMsg = "Socket Timeout"
    const val connectionErrorMsg = "Connection Error"
    const val protocolExceptionMsg = "Protocol Exception"
    const val connectionAbortMsg = "Connection Abort !!" //
    const val cancellationExceptionMsg = "Cancellation Exception"


    var ApiJob = Job()

    fun init(mBaseUrl: String) {
        this.mBaseUrl = mBaseUrl
    }


    private val retrofit by lazy {
        setRetrofit()
    }
    private val retrofitGust by lazy {
        Retrofit.Builder()
            .baseUrl(mBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .addCallAdapterFactory(TryCallAdapter())
            .client(clientGust)
            .build()
    }
    val client by lazy {
        provideOkHttpClient()
    }
    val clientGust by lazy {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .connectTimeout(50L, TimeUnit.SECONDS)
            .writeTimeout(50L, TimeUnit.SECONDS)
            .readTimeout(50L, TimeUnit.SECONDS)
            // .addInterceptor(logging)
            .build()
    }


    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }


    fun <T> createGust(service: Class<T>): T {
        return retrofitGust.create(service)
    }


    private fun setRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(mBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .addCallAdapterFactory(TryCallAdapter())
            .client(client)
            .build()
    }

    fun getGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ssZ")
            .setLenient()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }


    private fun provideOkHttpClient(): OkHttpClient {
        val cacheSize = 100 * 1024 * 1024 // 100 MiB

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
//Log.d("BaseApp.app.cacheDir" , BaseApp.app.cacheDir.length().toString())
        val client = OkHttpClient.Builder()
            .connectTimeout(50L, TimeUnit.SECONDS)
            .writeTimeout(50L, TimeUnit.SECONDS)
            .readTimeout(50L, TimeUnit.SECONDS)
            .addInterceptor(logging)
        //   .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
        // .cache(Cache(BaseApp.app.cacheDir, cacheSize.toLong()))
        //.addInterceptor(AuthenticationInterceptor())
        // .authenticator(RefreshTokenAuthenticator())

        // .addInterceptor(LoggingInterceptor2())


        //    client.addInterceptor(logging)

        return client.build()

    }


}