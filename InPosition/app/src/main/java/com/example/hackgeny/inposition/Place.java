package com.example.hackgeny.inposition;
import android.location.Location;
public class Place implements Comparable<Place> {
    private Location location;
    private String title;
    private String info;
    private double distanceFromUser;
    public Place(String title, Location location, String info) {
        this.title = title;
        this.location = location;
        this.info = info;
    }
    public String getInfo() {
        return info;
    }
    public String getTitle() {
        return title;
    }
    public Location getLocation() {
        return location;
    }
    public void setAltitude(double altitude) {
        location.setAltitude(altitude);
    }
    public void calcDistanceToUser(Location location) {
        double x = this.location.getAltitude() - location.getAltitude();
        double y = this.location.getLongitude() - location.getLongitude();
        double z = this.location.getLatitude() - location.getLatitude();
        distanceFromUser = Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
    }
    public double getDistanceFromUser() {
        return distanceFromUser;
    }
    @Override
    public int compareTo(Place place) {
        return (int) place.distanceFromUser - (int) distanceFromUser;
    }
    public String toString() {
        return title;
    }
}