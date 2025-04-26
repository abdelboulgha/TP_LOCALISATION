package com.example.tp_localisation;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tp_localisation.apis.PositionApi;
import com.example.tp_localisation.apis.RetrofitClient;
import com.example.tp_localisation.classes.Position;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize the MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("MapActivity", "Map is ready");

        // Fetch locations and show them on the map
        fetchLocations();
    }

    private void fetchLocations() {
        // Retrofit API call to fetch positions
        PositionApi positionApi = RetrofitClient.getRetrofitInstance().create(PositionApi.class);
        Call<List<Position>> call = positionApi.getAllPositions();

        call.enqueue(new Callback<List<Position>>() {
            @Override
            public void onResponse(Call<List<Position>> call, Response<List<Position>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Position> positions = response.body();
                    for (Position position : positions) {
                        LatLng location = new LatLng(position.getLatitude(), position.getLongitude());
                        Log.d("MapActivity", "API Key: " + getString(R.string.google_maps_key));
                        Log.d("MapActivity", "Marker added at: " + location);
                        mMap.addMarker(new MarkerOptions().position(location).title("Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                    }
                } else {
                    Toast.makeText(MapActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Position>> call, Throwable t) {
                Toast.makeText(MapActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}