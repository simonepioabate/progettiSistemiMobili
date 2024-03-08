package com.example.placeremindermap;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HandlerFilePlaceholder extends HandlerFile{

    private static final String fileName = "placeholders.txt";

    public static List<PoiDescriptor> readFile(Context context) {

        final List<PoiDescriptor> poiDescriptorList = new ArrayList<>();

        try  {
            FileInputStream inputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                String[] parti = line.split(";");

                poiDescriptorList.add(
                        new PoiDescriptor(parti[0], parti[1],
                                Double.parseDouble(parti[2]),
                                Double.parseDouble(parti[3]),
                                parti[4], parti[5]));

            }
        } catch (IOException e) { e.printStackTrace(); }

        return poiDescriptorList;
    }


    public static boolean onlyOneName(Context context, String namePlaceholder){
        try  {

            FileInputStream  inputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader  bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                String[] parti = line.split(";");
                if(parti[0].equals(namePlaceholder))
                {
                    return false;
                }
            }

        } catch (IOException e) { e.printStackTrace(); }
        return true;
    }

    public static List<String> readOnlyNameFile(Context context) {

        List<String> nameList = new ArrayList<>();

        try  {
            FileInputStream  inputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader  bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                String[] parti = line.split(";");

                nameList.add(parti[0]);

            }

        } catch (IOException e) { e.printStackTrace(); }

        return nameList;
    }

    public static PoiDescriptor recoveryPlaceholder(Context context, String namePlaceholder){
        try  {

            FileInputStream  inputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader  bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                String[] parti = line.split(";");
                if(parti[0].equals(namePlaceholder))
                {
                    return new PoiDescriptor(parti[0], parti[1],
                            Double.parseDouble(parti[2]),
                            Double.parseDouble(parti[3]),
                            parti[4], parti[5]);
                }
            }

        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    public static void deletePlaceholder(Context context, String namePlaceholder){

        List<PoiDescriptor> poiDescriptorList = readFile(context);

        clearFile(context, fileName);

        for(PoiDescriptor poiDescriptor : poiDescriptorList){
            if(!poiDescriptor.getName().equals(namePlaceholder)){
                writeFile(context,fileName, poiDescriptor.toString());
            }
        }
    }

}
