package com.joinflatshare.chat.api;

import static com.joinflatshare.constants.SendBirdConstants.SENDBIRD_BASEURL;

import androidx.annotation.NonNull;

import com.joinflatshare.FlatShareApplication;
import com.joinflatshare.constants.SendBirdConstants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SendBirdRetrofitClient {
    public static SendBirdApiInterface getClient() {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(SENDBIRD_BASEURL)
                .client(getHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(SendBirdApiInterface.class);
    }

    private static OkHttpClient getHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .cache(new Cache(FlatShareApplication.Companion.getInstance().getCacheDir(),
                        10 * 1024 * 1024)) // 10 MB
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor);
        builder.addNetworkInterceptor(new AddHeaderInterceptor());
        return builder.build();
    }

    private static class AddHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            builder.addHeader("Api-Token", SendBirdConstants.SENDBIRD_API_TOKEN);
            request = builder.build();
            return chain.proceed(request);
        }
    }
}

