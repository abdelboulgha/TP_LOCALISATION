package com.example.tp_localisation.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tp_localisation.classes.Position;
import com.example.tp_localisation.repositories.PositionRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PositionViewModel extends ViewModel {

    private MutableLiveData<String> responseLiveData = new MutableLiveData<>();
    private PositionRepository repository;

    public PositionViewModel() {
        // No need to initialize repository here.
    }

    public void init(Context context) {
        if (repository == null) {
            repository = new PositionRepository(context);
        }
    }

    public LiveData<String> getResponseLiveData() {
        return responseLiveData;
    }

    public void sendPosition(Position position) {
        if (repository != null) {
            repository.sendPosition(position, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        responseLiveData.setValue("Succès : " + response.body());
                    } else {
                        responseLiveData.setValue("Erreur : " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    responseLiveData.setValue("Erreur : " + t.getMessage());
                }
            });
        } else {
            responseLiveData.setValue("Repository non initialisé !");
        }
    }
}
