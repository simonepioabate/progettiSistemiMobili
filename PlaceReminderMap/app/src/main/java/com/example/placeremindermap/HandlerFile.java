package com.example.placeremindermap;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HandlerFile {

    public static boolean createFile(Context context, String fileName) {

        File file = new File(context.getFilesDir(), fileName);
        boolean created = false;
        if (!file.exists()) {
            try {
                created = file.createNewFile();
            }
            catch (IOException e) { e.printStackTrace(); }
        }
        return created;
    }

    public static void clearFile(Context context, String fileName) {
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
    public static boolean deleteFile(Context context, String fileName) {

        File file = new File(context.getFilesDir(), fileName);
        return file.delete();
    }

    public static void writeFile(Context context, String fileName, String string){

        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_APPEND);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (IOException e) { e.printStackTrace(); }
    }


}
