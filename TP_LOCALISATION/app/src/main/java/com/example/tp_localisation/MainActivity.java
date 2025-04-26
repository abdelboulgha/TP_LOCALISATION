package com.example.tp_localisation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.tp_localisation.classes.Position;
import com.example.tp_localisation.viewmodels.PositionViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 1;

    private Button btnSend, btnShowMap;
    private TextView tvStatus;
    private  String message;

    private LocationManager locationManager;
    private TelephonyManager telephonyManager;
    private PositionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = findViewById(R.id.btn_send);
        tvStatus = findViewById(R.id.tv_status);

        // Initialize LocationManager and TelephonyManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PositionViewModel.class);
        viewModel.init(this);

        // Observe the result from ViewModel
        viewModel.getResponseLiveData().observe(this, result -> tvStatus.setText(message));
        requestPermissions();
        sendCurrentPosition();
        // Button click listener to check permissions and fetch location
        btnSend.setOnClickListener(v -> {
            if (checkPermissions()) {
                sendCurrentPosition();
            } else {
                requestPermissions();
            }
        });

        btnShowMap = findViewById(R.id.btn_show_map);

        // Set onClickListener to navigate to the MapActivity
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
        }, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (checkPermissions()) {
                sendCurrentPosition();
            } else {
                tvStatus.setText("Permissions refusées");
            }
        }
    }

    private void sendCurrentPosition() {
        try {
            if (checkPermissions()) {
                tvStatus.setText("Searching for location...");

                // Try to use network provider for faster initial position, with specific update intervals
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            6000,
                            5,
                            locationListener);
                }
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            6000,
                            5,
                            locationListener);
                } else {
                    tvStatus.setText("GPS is disabled. Please enable GPS in settings.");
                    return;
                }
                Location lastKnownLocation = null;
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (lastKnownLocation == null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

                if (lastKnownLocation != null) {
                    updatePosition(lastKnownLocation);
                }
                // Set a timeout handler
                new android.os.Handler().postDelayed(() -> {
                    if (tvStatus.getText().toString().equals("Searching for location...")) {
                        tvStatus.setText("Location timeout. Try again or check GPS settings.");
                        try {
                            locationManager.removeUpdates(locationListener);
                        } catch (SecurityException e) {
                            Log.e("MainActivity", "Error removing location updates: " + e.getMessage());
                        }
                    }
                }, 6000);

            } else {
                tvStatus.setText("Permissions non accordées");
            }
        } catch (SecurityException e) {
            tvStatus.setText(e.getMessage());
        }
    }

    private final android.location.LocationListener locationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.d("MainActivity", "Location changed: " + location.getLatitude() + ", " + location.getLongitude());

            // Afficher les informations de localisation avec un Toast
            String msg = "Nouvelle localisation : \nLat: " + location.getLatitude() +
                    ", Long: " + location.getLongitude() +
                    "\nAlt: " + location.getAltitude() +
                    ", Précision: " + location.getAccuracy();
            message=msg;
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

            tvStatus.setText(msg);

            updatePosition(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Handle status change if needed
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            tvStatus.setText("Fournisseur de localisation activé: " + provider);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            tvStatus.setText("Fournisseur de localisation désactivé: " + provider);
        }
    };
    private void updatePosition(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String imei = getImei();  // Use the updated method to get IMEI or an alternative identifier
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Position position = new Position(lat, lon, imei, date);
        viewModel.sendPosition(position);
    }

    // Get IMEI, or fallback to another identifier if not allowed
    // Modifiez votre méthode getImei actuelle dans MainActivity.java
    private String getImei() {
        // Utiliser Android ID comme alternative à l'IMEI
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Si Android ID est disponible, l'utiliser
        if (androidId != null && !androidId.isEmpty()) {
            return  androidId;
        }

        // Si nous arrivons ici, essayer de vérifier si nous avons un identifiant persistant
        SharedPreferences sharedPrefs = getSharedPreferences("DevicePrefs", Context.MODE_PRIVATE);
        String uniqueID = sharedPrefs.getString("UniqueID", null);

        // Si pas d'ID existant, en créer un nouveau
        if (uniqueID == null) {
            uniqueID = "device-" + UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("UniqueID", uniqueID);
            editor.apply();
        }

        return uniqueID;
    }
}