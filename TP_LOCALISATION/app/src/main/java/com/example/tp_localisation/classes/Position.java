package com.example.tp_localisation.classes;

public class Position {
    private double latitude, longitude;
    private String imei, date;

    public Position(double latitude, double longitude, String imei, String date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imei = imei;
        this.date = date;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImei() {
        return imei;
    }

    public String getDate() {
        return date;
    }
}