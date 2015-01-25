package com.example.hackgeny.inposition;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.location.Location;

public class DataBase {

    private final int MAX_NUM_CARDS = 3;
    private final double MAX_DISTANCE = 10.0;
    private final double MAX_RADIUS = 1.0;

    private ArrayList<Place> places;
    private Context context;

    public DataBase(Context context) {

        places = new ArrayList<Place>();
        ArrayList<String> fileNames = new ArrayList<String>();

        fileNames.add("Dinosaurs.txt");
        fileNames.add("Workspace.txt");
        fileNames.add("Alan Turing.txt");

        this.context = context;

        for(String filename : fileNames) {
            addPlace(filename);
        }

    }

    private void addPlace(String filename) {

        try {

            InputStream paragraphInfo =  context.getResources().getAssets().open(filename);
            InputStreamReader inputReader = new InputStreamReader(paragraphInfo);
            BufferedReader buffReader = new BufferedReader(inputReader);

            String title = buffReader.readLine();
            double altitude = Double.parseDouble(buffReader.readLine());
            double latitude = Double.parseDouble(buffReader.readLine());
            double longitude = Double.parseDouble(buffReader.readLine());

            Location location = new Location("");
            location.setAltitude(altitude);
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            String info1, info = "";
            while ((info1 = buffReader.readLine()) != null) {
                    info += info1;
                    info += " ";
            }

            places.add(new Place(title, location, info));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Place findPlace(Location location) {

        sort(location);

        if(places.get(0).getDistanceFromUser() < MAX_RADIUS)
            return places.get(0);

        else
            return null;

    }

    public ArrayList<Place> closestPlaces(Location location) {

        sort(location);

        ArrayList<Place> closestPlaces = new ArrayList<Place>();

        for(Place place: places) {
            if(place.getDistanceFromUser() <= MAX_DISTANCE && closestPlaces.size() < MAX_NUM_CARDS)
                closestPlaces.add(place);
            else
                break;
        }

        return closestPlaces;
    }

    private void sort(Location location) {

        for(Place place : places) {
            place.calcDistanceToUser(location);
        }

        Collections.sort(places);

    }

}
