package com.relferreira.gitnotify;

import android.content.SharedPreferences;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relferreira.gitnotify.api.GithubService;
import com.relferreira.gitnotify.model.GsonAdaptersModel;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by relferreira on 1/10/17.
 */
@Module
public class NetworkModule {

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapterFactory(new GsonAdaptersModel())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    ApiInterceptor provideApiInterceptor(SharedPreferences sharedPreferences){
        ApiInterceptor apiInterceptor = new ApiInterceptor();
        apiInterceptor.setAuthValue(sharedPreferences.getString(ApiInterceptor.AUTH_KEY, null));
        return apiInterceptor;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(ApiInterceptor apiInterceptor) {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addInterceptor(apiInterceptor);
        return httpClient.build();
    }


    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .baseUrl("https://api.github.com/")
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    GithubService provideGithubService(Retrofit retrofit) {
        return retrofit.create(GithubService.class);
    }

}
