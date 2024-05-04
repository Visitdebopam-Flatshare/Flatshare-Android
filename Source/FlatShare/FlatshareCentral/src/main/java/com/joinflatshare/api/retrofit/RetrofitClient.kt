package com.joinflatshare.api.retrofit

import com.joinflatshare.FlatShareApplication
import com.joinflatshare.FlatshareCentral.BuildConfig
import com.joinflatshare.db.daos.UserDao
import com.joinflatshare.utils.helper.CommonMethod
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

class RetrofitClient {
    companion object {
        fun getClient(): ApiInterface? {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.DOMAIN)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return retrofit.create(ApiInterface::class.java)

        }

        private fun getHttpClient(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(if(BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
            val file = FlatShareApplication.instance.cacheDir
            val builder = OkHttpClient().newBuilder()
                .cache(
                    Cache(file!!, 10485760L)
                ) // 10 MB
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor)
            builder.addNetworkInterceptor(AddHeaderInterceptor());
            return builder.build()
        }

        private class AddHeaderInterceptor : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                var request = chain.request()
                val builder = request.newBuilder()
                val token = FlatShareApplication.getDbInstance().userDao().get(UserDao.USER_KEY_API_TOKEN)
                if (!token.isNullOrEmpty()) {
                    builder.addHeader("x-access-token", token)
                    CommonMethod.makeLog("Token", token)
                }
                builder.addHeader("Accept", "application/json")
                builder.addHeader("Content-Type", "application/json")
                request = builder.build()
                return chain.proceed(request)
            }
        }
    }
}