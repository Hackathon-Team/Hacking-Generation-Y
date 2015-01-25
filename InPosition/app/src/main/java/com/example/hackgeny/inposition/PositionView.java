package com.example.hackgeny.inposition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amogkamsetty on 1/25/15.
 */
public class PositionView extends View {
    private static final int MAX_OVERLAPPING_PLACE_NAMES = 5;
    private static final float MIN_DISTANCE_TO_ANIMATE = 15.0f;
    private static final float PLACE_TEXT_HEIGHT = 22.0f;
    private static final float PLACE_PIN_WIDTH = 14.0f;
    private static final float PLACE_TEXT_LEADING = 4.0f;
    private static final float PLACE_TEXT_MARGIN = 8.0f;
    private static final double EARTH_RADIUS_KM = 6371.0;
    private float mHeading;
    private OrientationManager mOrientation;
    private List<Place> mNearbyPlaces;
    private final TextPaint mPaint;
    private float mAnimatedHeading;
    private final ValueAnimator mAnimator;
    private final List<Rect> mAllBounds;


    public PositionView(Context context) {
        this(context, null, 0);
    }

    public PositionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PositionView(Context context, AttributeSet attrs, int deftStyle) {
        super(context, attrs, deftStyle);
        mPaint = new TextPaint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(84.0f);
        mPaint.setColor(Color.WHITE);
        mPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        mAllBounds = new ArrayList<Rect>();
        mAnimatedHeading = Float.NaN;
        mAnimator = new ValueAnimator();
        setupAnimator();


    }

    public void setOrientationManager(OrientationManager orientationManager) {
        mOrientation = orientationManager;
    }

    public float getHeading() {
        return mHeading;
    }

    public void setHeading(float degrees) {
        mHeading = mod(degrees,360.0f);
        animateTo(mHeading);
    }

    public void setNearbyPlaces(List<Place> places) {
        mNearbyPlaces = places;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2.0f;
        float centerY = getHeight() / 2.0f;
        canvas.save();
        canvas.translate(-mAnimatedHeading * getWidth() / 90.0f + centerX, centerY);
        for (int i = -1; i <= 1; i++) {
            drawPlaces(canvas, getWidth() / 90.0f, i * getWidth() / 90.0f * 360);
        }
        canvas.restore();
    }

    private void drawPlaces(Canvas canvas, float pixelsPerDegree, float offset) {
        if (mOrientation.hasLocation() && mNearbyPlaces != null) {
            synchronized (mNearbyPlaces) {
                Location userLocation = mOrientation.getLocation();
                double latitude1 = userLocation.getLatitude();
                double longitude1 = userLocation.getLongitude();
                mAllBounds.clear();
                for (Place place : mNearbyPlaces) {
                    double latitude2 = place.getLocation().getLatitude();
                    double longitude2 = place.getLocation().getLongitude();
                    float bearing = getBearing(latitude1, longitude1, latitude2, longitude2);
                    String name = place.getTitle();
                    double distancekm = getDistance(latitude1, longitude1, latitude2, longitude2);
                    String text = place.getInfo();
                    Rect textBounds = new Rect();
                    mPaint.getTextBounds(text, 0, name.length(), textBounds);
                    textBounds.offsetTo((int) (offset + bearing * pixelsPerDegree + PLACE_PIN_WIDTH / 2 + PLACE_TEXT_MARGIN), canvas.getHeight() / 2 - (int) PLACE_TEXT_HEIGHT);
                    textBounds.left -= PLACE_PIN_WIDTH + PLACE_TEXT_MARGIN;
                    textBounds.right += PLACE_TEXT_MARGIN;
                    boolean intersects;
                    int numberOfTries =0;
                    do {
                        intersects = false;
                        numberOfTries++;
                        textBounds.offset(0, (int) -(PLACE_TEXT_HEIGHT + PLACE_TEXT_LEADING));
                        for (Rect existing : mAllBounds) {
                            if (Rect.intersects(existing, textBounds)) {
                                intersects = true;
                                break;
                            }
                        }
                    }
                    while (intersects && numberOfTries <= MAX_OVERLAPPING_PLACE_NAMES) ;
                    if (numberOfTries <= MAX_OVERLAPPING_PLACE_NAMES) {
                        mAllBounds.add(textBounds);
                        canvas.drawText(text, offset + bearing * pixelsPerDegree + PLACE_PIN_WIDTH / 2 + PLACE_TEXT_MARGIN, textBounds.top + PLACE_TEXT_HEIGHT, mPaint);
                    }
                }
            }
        }

    }


    private float getBearing(double latitude1, double longitude1, double latitude2,
                             double longitude2) {
        latitude1 = Math.toRadians(latitude1);
        longitude1 = Math.toRadians(longitude1);
        latitude2 = Math.toRadians(latitude2);
        longitude2 = Math.toRadians(longitude2);

        double dLon = longitude2 - longitude1;

        double y = Math.sin(dLon) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1)
                * Math.cos(latitude2) * Math.cos(dLon);

        double bearing = Math.atan2(y, x);
        return mod((float) Math.toDegrees(bearing), 360.0f);
    }

    private float getDistance(double latitude1, double longitude1, double latitude2,
                              double longitude2) {
        double dLat = Math.toRadians(latitude2 - latitude1);
        double dLon = Math.toRadians(longitude2 - longitude1);
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        double sqrtHaversineLat = Math.sin(dLat / 2);
        double sqrtHaversineLon = Math.sin(dLon / 2);
        double a = sqrtHaversineLat * sqrtHaversineLat + sqrtHaversineLon * sqrtHaversineLon
                * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (EARTH_RADIUS_KM * c);
    }

    private float mod(float a, float b) {
        return (a % b + b) % b;
    }

    private void setupAnimator() {
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(250);

        // Notifies us at each frame of the animation so we can redraw the view.
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mAnimatedHeading = mod((Float) mAnimator.getAnimatedValue(), 360.0f);
                invalidate();
            }
        });

        // Notifies us when the animation is over. During an animation, the user's head may have
        // continued to move to a different orientation than the original destination angle of the
        // animation. Since we can't easily change the animation goal while it is running, we call
        // animateTo() again, which will either redraw at the new orientation (if the difference is
        // small enough), or start another animation to the new heading. This seems to produce
        // fluid results.
        mAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animator) {
                animateTo(mHeading);
            }
        });
    }


    private void animateTo(float end) {
        // Only act if the animator is not currently running. If the user's orientation changes
        // while the animator is running, we wait until the end of the animation to update the
        // display again, to prevent jerkiness.
        if (!mAnimator.isRunning()) {
            float start = mAnimatedHeading;
            float distance = Math.abs(end - start);
            float reverseDistance = 360.0f - distance;
            float shortest = Math.min(distance, reverseDistance);

            if (Float.isNaN(mAnimatedHeading) || shortest < MIN_DISTANCE_TO_ANIMATE) {
                // If the distance to the destination angle is small enough (or if this is the
                // first time the compass is being displayed), it will be more fluid to just redraw
                // immediately instead of doing an animation.
                mAnimatedHeading = end;
                invalidate();
            } else {
                // For larger distances (i.e., if the compass "jumps" because of sensor calibration
                // issues), we animate the effect to provide a more fluid user experience. The
                // calculation below finds the shortest distance between the two angles, which may
                // involve crossing 0/360 degrees.
                float goal;

                if (distance < reverseDistance) {
                    goal = end;
                } else if (end < start) {
                    goal = end + 360.0f;
                } else {
                    goal = end - 360.0f;
                }

                mAnimator.setFloatValues(start, goal);
                mAnimator.start();
            }
        }
    }
}