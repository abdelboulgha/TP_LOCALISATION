package com.example.tp_localisation.apis;

import com.example.tp_localisation.classes.Position;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PositionApi {
    @FormUrlEncoded
    @POST("createPosition.php")
    Call<String> sendPosition(
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("date") String date,
            @Field("imei") String imei
    );

    @GET("getPositions.php")
    Call<List<Position>> getAllPositions();
}