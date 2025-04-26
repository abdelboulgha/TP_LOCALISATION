package com.example.tp_localisation.repositories;

import android.content.Context;
import android.util.Log;

import com.example.tp_localisation.apis.PositionApi;
import com.example.tp_localisation.classes.Position;
import com.example.tp_localisation.apis.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PositionRepository {
    private final Context context;
    private static final String TAG = "PositionRepository";

    public PositionRepository(Context context) {
        this.context = context;
    }

    public void sendPosition(Position position, Callback<String> callback) {
        Log.d(TAG, "Preparing to send position: " + position.getLatitude() + ", " + position.getLongitude());

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        PositionApi positionApi = retrofit.create(PositionApi.class);

        Log.d(TAG, "API call prepared with data: Lat=" + position.getLatitude() +
                ", Lon=" + position.getLongitude() + ", Date=" + position.getDate() +
                ", IMEI=" + position.getImei());
        Call<String> call = positionApi.sendPosition(
                position.getLatitude(),
                position.getLongitude(),
                position.getDate(),
                position.getImei()
        );

        call.enqueue(callback);
    }
}