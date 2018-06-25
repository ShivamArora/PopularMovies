package com.shivora.example.popularmovies.utils;



import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkingUtils {

    public static final String BASE_URL = "https://api.themoviedb.org/";
    public static final String BASE_URL_POSTER = "http://image.tmdb.org/t/p/";

    public static final String QUALITY_500 = "w500";

    private static final String API_KEY = "api_key";
    private static final String PARAM_LANGUAGE = "language";
    private static final String LANGUAGE_ENGLISH = "en-US";
    private static final String PARAM_PAGE_NO = "page";
    private static int pageNumber = 1;

    public static Retrofit getRetrofitInstance(final String apiKey){
        Retrofit retrofit;

        //OkHttpBuilder
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter(API_KEY,apiKey)
                        .addQueryParameter(PARAM_LANGUAGE,LANGUAGE_ENGLISH)
                        .addQueryParameter(PARAM_PAGE_NO,String.valueOf(pageNumber))
                        .build();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);
                System.out.println("URL: "+url);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = retrofitBuilder
                .client(httpClient.build())
                .build();

        return retrofit;
    }
}
