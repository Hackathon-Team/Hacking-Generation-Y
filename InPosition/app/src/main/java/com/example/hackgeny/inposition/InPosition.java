package com.example.hackgeny.inposition;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

/**
 * A {@link Service} that publishes a {@link LiveCard} in the timeline.
 */
public class InPosition extends Service {

    private static final String LIVE_CARD_TAG = "InPosition";

    private DataBase db = new DataBase(this);
    private OrientationManager mOrientationManager;
    private LiveCard mLiveCard;
    private LiveCardRenderer mRenderer;
    private DataBase data;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mOrientationManager = new OrientationManager(locationManager);
        data=new DataBase(getApplicationContext());
        //mLandmarks = new Landmarks(this)
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);
            mRenderer = new LiveCardRenderer(this,data,mOrientationManager);
            mLiveCard.setDirectRenderingEnabled(true).getSurfaceHolder().addCallback(mRenderer);
            mLiveCard.setVoiceActionEnabled(true);

            LiveCardRenderer renderer = new LiveCardRenderer(this, db, new OrientationManager((LocationManager)getSystemService(Context.LOCATION_SERVICE)));
            mLiveCard.setDirectRenderingEnabled(true).getSurfaceHolder().addCallback(renderer);

            // Display the options menu when the live card is tapped.
            LiveCardMenuActivity.userLocation = ((LocationManager) getSystemService(
                    Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            mLiveCard.attach(this);

            mLiveCard.publish((intent == null) ? PublishMode.SILENT : PublishMode.REVEAL);
          } else {
            mLiveCard.navigate();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }
}
