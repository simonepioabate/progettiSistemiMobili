package com.example.placeremindermap;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HandlerFileSettings extends HandlerFile{

    private static final String fileName = "settings.txt";
    public static SettingsData readFile(Context context) {
        final List<String> settingsList = new ArrayList<>();

        try  {
            FileInputStream inputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                settingsList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SettingsData(Double.parseDouble(settingsList.get(0)),
                Double.parseDouble(settingsList.get(1)), settingsList.get(2), settingsList.get(3));
    }
}
