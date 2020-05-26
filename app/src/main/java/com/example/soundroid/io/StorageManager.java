package com.example.soundroid.io;
import android.util.Log;

import com.example.soundroid.db.PortableDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;

public class StorageManager {

    public static File getSoundroidDirectory() {
        File externalStorageRoot = android.os.Environment.getExternalStorageDirectory();
        return new File(externalStorageRoot.getAbsolutePath() + "/Soundroid");
    }

    public static void createSoundroidDirectory() {
        getSoundroidDirectory().mkdir();
    }

    public static boolean createJsonFile(String name, String json) {
        try (FileWriter writer = new FileWriter(getSoundroidDirectory() + "/" + name + ".json")) {
            writer.write(json);
        } catch (IOException exception) {
            return false;
        }
        return true;
    }

    public static String readJsonFile(String name) {
        File file = new File(getSoundroidDirectory() + "/" + name + ".json");
        try (FileInputStream stream = new FileInputStream(file)) {
            Reader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
                builder.append(buffer, 0, read);
            }
            return builder.toString();
        } catch (IOException exception) {
            return null;
        }
    }



}
