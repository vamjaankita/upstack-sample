package com.giphytest.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class ServiceUtil {

    private static final int CLIENT_TIME_OUT = 10;


    private static final String DATE_FORMAT = "yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'";

    private ServiceUtil() {
    }


    public static <T> T createService(final Class<T> clazz, final String endPoint) {
        return getRetrofit(endPoint).create(clazz);
    }


    private static Retrofit getRetrofit(final String endPoint) {
        return new Retrofit.Builder()
                .baseUrl(endPoint)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .client(getOkHttpClient())
                .build();
    }

    private static Gson getGson() {
        return new GsonBuilder()
                .setDateFormat(DATE_FORMAT)
                .create();
    }

    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(CLIENT_TIME_OUT, TimeUnit.SECONDS)
                .build();
    }
}
