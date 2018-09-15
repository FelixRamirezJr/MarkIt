package com.example.felix.locationmapper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class FileMarkerHandler {
    static String folderName = "markit";
    static String fileName = "markers";
    FileOutputStream outputStream;

    static public void addMarker(ArrayList<MarkerLocation> markerLocationList, Context context)
    {
        // Destroy folder and recreate it if it exists
        File folder = new File(context.getFilesDir(), folderName);
        if(folder.exists()){
            folder.delete();
        }
        folder.mkdir();

        // Convert array list into a JSON object to be saved into the file
        Gson gson = new Gson();
        String json = gson.toJson(markerLocationList);

        // Now insert the data
        try{
            File gpxfile = new File(folder, fileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(json);
            writer.flush();
            writer.close();

        } catch (Exception e){
            e.printStackTrace();

        }
        Log.d("addMarker", "Done!");

    }

    static public ArrayList<MarkerLocation> readMarkers(Context context)
    {
//        File file = new File(context.getFilesDir(), folderName + "/" + fileName);
//        if(folder.exists()) {
//            Log.d("readMarkers", "Folder Exists");
//            for(File file : folder.listFiles()){
//                Log.d("readMarkers",file.getName());
//            }
//        }
        ArrayList<MarkerLocation> locations;
        //String filePath = context.getFilesDir() + folderName + "/" + fileName;
        File file = new File(context.getFilesDir(), folderName + "/" + fileName);
        MarkerLocation [] data = new MarkerLocation[0];

        if(file.exists()){
            Log.d("readMarkers", "File exists going to read...");
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.d("readMarker:append_line",line);
                    sb.append(line);
                }
                data = new Gson().fromJson(sb.toString(), MarkerLocation[].class);

            }catch (Exception e){
                e.printStackTrace();
            }

            // Now create locations based on the json data retrieved
            locations = new ArrayList<>(Arrays.asList(data));
        } else {
            Log.d("readMarkers","File Does NOT exist");
            locations = new ArrayList<>();
        }
        return locations;
    }

}
