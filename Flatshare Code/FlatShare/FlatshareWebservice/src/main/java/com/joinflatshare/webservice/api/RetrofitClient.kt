package com.joinflatshare.webservice.api

import android.app.Application
import com.joinflatshare.webservice.api.interfaces.ApiInterface
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {
    fun getClient(application: Application): ApiInterface? {
        return getClient(null, application)
    }

    fun getClient(apiToken: String?, application: Application): ApiInterface? {
        val retrofit = Retrofit.Builder()
            .baseUrl("")
            .client(getHttpClient(apiToken, application))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(ApiInterface::class.java)

    }

    private fun getHttpClient(apiToken: String?, application: Application): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(
            HttpLoggingInterceptor.Level.BODY
        )
        val file = application.cacheDir
        val builder = OkHttpClient().newBuilder()
            .cache(
                Cache(file!!, 10485760L)
            ) // 10 MB
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(interceptor)
        if (!apiToken.isNullOrEmpty())
            builder.addNetworkInterceptor(AddHeaderInterceptor(apiToken));
        return builder.build()

    }


    private class AddHeaderInterceptor(private val apiToken: String) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            val builder = request.newBuilder()
            builder.addHeader("x-access-token", apiToken)
            builder.addHeader("Accept", "application/json")
            builder.addHeader("Content-Type", "application/json")
            request = builder.build()
            return chain.proceed(request)
        }
    }
}