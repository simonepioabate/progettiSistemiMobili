package com.example.placeremindermap;

import androidx.annotation.NonNull;

public class SettingsData {

    private double latCenter;
    private double lngCenter;
    private String typeMap;
    private String colorPlaceholder;

    public SettingsData(double latCenter, double lngCenter, String typeMap, String colorPlaceholder){

        this.latCenter = latCenter;
        this.lngCenter = lngCenter;
        this.typeMap = typeMap;
        this.colorPlaceholder = colorPlaceholder;
    }

    public double getLatCenter() {
        return latCenter;
    }

    public void setLatCenter(double latCenter) {
        this.latCenter = latCenter;
    }
    public double getLngCenter() {
        return lngCenter;
    }

    public void setLngCenter(double lngCenter) {
        this.lngCenter = lngCenter;
    }

    public String getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(String typeMap) {
        this.typeMap = typeMap;
    }

    public String getColorPlaceholder() {
        return colorPlaceholder;
    }

    public void setColorPlaceholder(String colorPlaceholder) {
        this.colorPlaceholder = colorPlaceholder;
    }


    @NonNull
    @Override
    public String toString() {
        return latCenter + "\n" +
               lngCenter + "\n" +
               typeMap + "\n" +
               colorPlaceholder;
    }

}
