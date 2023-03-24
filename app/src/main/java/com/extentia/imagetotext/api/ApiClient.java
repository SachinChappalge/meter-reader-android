package com.extentia.imagetotext.api;


import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static final String BASE_URL="http://65.1.84.98";

    public static Retrofit getClient() {
        if (retrofit == null) {
            Interceptor responseInterceptor= chain -> {
                Request request = chain.request();
                okhttp3.Response response = chain.proceed(request);
                return response;
            };
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new DefaultHeaderInterceptor())
                    .addInterceptor(logging)
                    .addInterceptor(responseInterceptor)
                    .readTimeout(45, TimeUnit.SECONDS)
                    .connectTimeout(45, TimeUnit.SECONDS)
                    .writeTimeout(45, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
