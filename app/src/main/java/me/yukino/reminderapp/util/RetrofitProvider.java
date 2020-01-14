package me.yukino.reminderapp.util;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Retrofit单例类
 *
 * 提供Retrofit实例
 *
 * @author Yukino Yukinoshita
 */

public class RetrofitProvider {

    private static final String BASE_URL = "http://134.175.176.173/reminder/";
    private static final String CONTENT_BASE_URL = "http://106.52.70.177/reminders/";
    private static volatile Retrofit retrofitInstance;
    private static volatile Retrofit contentRetrofitInstance;

    public static Retrofit get() {
        if (retrofitInstance == null) {
            synchronized (RetrofitProvider.class) {
                if (retrofitInstance == null) {
                    retrofitInstance = new Retrofit.Builder()
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(JacksonConverterFactory.create())
                            .baseUrl(BASE_URL)
                            .build();
                }
            }
        }
        return retrofitInstance;
    }

    public static Retrofit getContent() {
        if (contentRetrofitInstance == null) {
            synchronized (RetrofitProvider.class) {
                if (contentRetrofitInstance == null) {
                    contentRetrofitInstance = new Retrofit.Builder()
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(JacksonConverterFactory.create())
                            .baseUrl(CONTENT_BASE_URL)
                            .build();
                }
            }
        }
        return contentRetrofitInstance;
    }

}
