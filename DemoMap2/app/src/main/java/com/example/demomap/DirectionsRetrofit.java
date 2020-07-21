package com.example.demomap;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionsRetrofit {
    private static Retrofit retrofit = null;

    private static final String BASE_URL_DIRECTIONS = "https://mapapi.cloud.huawei.com/mapApi/v1/";

    static Retrofit getClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_DIRECTIONS)
                .addConverterFactory(GsonConverterFactory.create())
                .client(setInterCeptors())
                .build();
        return retrofit;
    }

    private static OkHttpClient setInterCeptors() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient
                .Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        HttpUrl url = chain.request().url().newBuilder().addQueryParameter("key", "CgB6e3x9UwA/Le7Lfj5pnrNBBIkeVnM41hNLuUXdN7rMshoBNi4L+nUGhzxzyySAp3kKOYrkw1jHAPJy8ZwJKF69").build();
                        Request request = chain.request().newBuilder()
                                .header("Content-Type", "application/json")
                                .url(url)
                                .build();

                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();
    }
}
