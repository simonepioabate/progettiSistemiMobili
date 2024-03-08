package com.example.placeremindermap;

import androidx.annotation.NonNull;

public class PoiDescriptor {

    private String name = null;
    private String description = null;
    private double lat = 0.0;
    private double lng = 0.0;
    private String date;
    private String lastUpdate;

    public PoiDescriptor() {
    }

    public PoiDescriptor(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public PoiDescriptor(String name, String description, double lat, double lng, String date, String lastUpdate) {
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.date = date;
        this.lastUpdate = lastUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @NonNull
    @Override
    public String toString() {
        return name + ";" + description +
                ";" + lat + ";" + lng + ";"
                + date + ";" + lastUpdate + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoiDescriptor that = (PoiDescriptor) o;
        return Double.compare(that.lat, lat) == 0 &&
                Double.compare(that.lng, lng) == 0;
    }

    public String getDate() {
        return date;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String modifyDate) {
        this.lastUpdate = modifyDate;
    }
}
