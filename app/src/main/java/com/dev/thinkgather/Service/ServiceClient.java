package com.dev.thinkgather.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ServiceClient {
//    public static final String BASE_URL = "http://thinkgather.mamasaja.id/";
//        public static final String BASE_URL = "http://192.168.2.8/thinkgather/";
    public static final String BASE_URL = "http://192.168.43.139/thinkgather/";
//    public static final String BASE_URL = "http://192.168.202.1/thinkgather/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        return retrofit;
    }
}
