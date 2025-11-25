package com.example.sentivibe.data;

import com.example.sentivibe.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    // The base URL is now sourced from BuildConfig for better flexibility
    private static final String BASE_URL = BuildConfig.API_BASE_URL;

    private static volatile ApiService apiServiceInstance;

    /**
     * Returns a thread-safe singleton instance of the ApiService.
     *
     * @return The single instance of ApiService.
     */
    public static ApiService getApi() {
        if (apiServiceInstance == null) {
            synchronized (RetrofitClient.class) {
                if (apiServiceInstance == null) {
                    // Create a logging interceptor
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    if (BuildConfig.DEBUG) {
                        // In debug builds, log the entire request/response body
                        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    } else {
                        // In release builds, disable logging
                        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
                    }

                    // Configure the OkHttp client with timeouts and the logging interceptor
                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();

                    // Build the Retrofit instance
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    // Create the ApiService instance
                    apiServiceInstance = retrofit.create(ApiService.class);
                }
            }
        }
        return apiServiceInstance;
    }
}
