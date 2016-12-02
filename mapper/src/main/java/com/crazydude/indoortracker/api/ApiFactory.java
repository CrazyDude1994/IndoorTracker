package com.crazydude.indoortracker.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CrazyDude on 02.12.16.
 */

public class ApiFactory {

    public static ApiService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.4:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}
