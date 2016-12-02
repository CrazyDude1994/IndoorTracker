package com.crazydude.indoortracker.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by CrazyDude on 02.12.16.
 */

public interface ApiService {

    @POST("api/client")
    @FormUrlEncoded
    Call<String> updatePosition(@Field("name") String name,
                                @Field("x") float x,
                                @Field("y") float y,
                                @Field("room_id") int roomId);
}
