package com.example.hackgeny.inposition;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class DataBase {
    private String[][] dataBase = {
            {"Alan Turing", "37.484722", "122.203056", "0.0", "Here is a exhibit about Alan Turing. He is credited for creating a machine to win World War II by breaking the Nazi's communication system known as Enigma. All computers were once called Turing machines!"},
            {"Dinosaur", "37.484722", "122.203056", "0.0", "dinos are scary"},
            {"Workshop", "37.484722", "122.203056", "0.0", "its a workshop look at coders"},
            {"Stairway", "37.484722", "122.203056", "10000.0", "hey loook at the stairs"},
            {"trashcans", "37.484722", "122.203056", "10000.0", "trashcans are overflowing with garbage"}};

//    private Location currLocation = new Location("");

    private final int MAX_NUM_CARDS = 3;
    private final double MAX_DISTANCE = 1000.0; //to qualify as exhibits in the vicinity
    private final double MAX_RADIUS = 0.00005; //to pop up automatically a place card

    private ArrayList<Place> places;
    private Context context;

    public DataBase(Context context) {
        places = new ArrayList<Place>();
        this.context = context;
//        currLocation.setAltitude();
//        currLocation.setLatitude();
//        currLocation.setLongitude();
        for(int x = 0; x < dataBase.length; x++) {
            addPlace(x);
        }
    }

    private void addPlace(int index) {

        try {
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(Double.parseDouble(dataBase[index][1]));
            location.setLatitude(Double.parseDouble(dataBase[index][2]));
            location.setAltitude(Double.parseDouble(dataBase[index][3]));
            places.add(new Place(dataBase[index][0], location, dataBase[index][4]));
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

    public void toggle() {
        for(int x = 0; x < 3; x++) {
            places.get(x).setAltitude(10000.0);
        }
        for(int x = 3; x < 5; x++) {
            places.get(x).setAltitude(0.0);
        }
    }

    private void sort(Location location) {

        for(Place place : places) {
            place.calcDistanceToUser(location);
        }

        Collections.sort(places);

    }

}
