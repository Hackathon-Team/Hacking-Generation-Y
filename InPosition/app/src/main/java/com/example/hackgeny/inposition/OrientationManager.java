package com.example.hackgeny.inposition;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Abinesh on 1/24/15.
 */
public class OrientationManager {
    private LocationManager mLocationManager;
    private Location mLocation;
    private final Set<OnChangedListener> mListeners;
    private boolean mTracking = false;

    private final long MAX_LOCATION_AGE_MILLIS = TimeUnit.SECONDS.toMillis(3);
    private static final long MILLIS_BETWEEN_LOCATIONS = 3;
    private static final float METERS_BETWEEN_DISTANCES = 2;


    public interface OnChangedListener {
        void onLocationChanged(OrientationManager orientationManager);
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;

            //updatedGeomagneticField();
            //notifyLocationChanged();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public OrientationManager(LocationManager locationManager) {
        mLocationManager = locationManager;
        mListeners = new LinkedHashSet<OnChangedListener>();
    }


    // Start tracking user's location
    public void start() {
        Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (!mTracking) {
            if (lastLocation != null) {
                if (MAX_LOCATION_AGE_MILLIS >= (lastLocation.getTime() - System.currentTimeMillis())) {
                    mLocation = lastLocation;
                    Log.d("Location Info", "LAT: " + mLocation.getLatitude() + "  LONG: " + mLocation.getLongitude());
                }
            }

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);

            List<String> providers = mLocationManager.getProviders(criteria, true);
            for (String provider : providers) {
                mLocationManager.requestLocationUpdates(provider, MILLIS_BETWEEN_LOCATIONS, METERS_BETWEEN_DISTANCES, mLocationListener, Looper.getMainLooper());
            }

            mTracking = true;
        }
    }

    public boolean hasLocation() {
        return mLocation != null;
    }
    public Location getLocation() {
        return mLocation;
    }

    public void addOnChangedListener(OnChangedListener listener)
    {
        mListeners.add(listener);
    }
    public void removeOnChangedListener(OnChangedListener listener)
    {
        mListeners.remove(listener);
    }

    private void notifyLocatedChaned()
    {
        for(OnChangedListener listener : mListeners) {
            listener.onLocationChanged(this);
        }
    }

    public void stop() {
        if (mTracking) {
            mLocationManager.removeUpdates(mLocationListener);
            mTracking = false;
        }
    }
}
