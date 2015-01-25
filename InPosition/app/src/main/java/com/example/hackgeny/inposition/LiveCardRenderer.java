package com.example.hackgeny.inposition;

import com.google.android.glass.timeline.DirectRenderingCallback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;


public class LiveCardRenderer implements DirectRenderingCallback {

    private static final String TAG = LiveCardRenderer.class.getSimpleName();


    /**
     * The refresh rate, in frames per second, of the view
     */

    private static final long REFRESH_RATE_FPS = 45;

    /**
     * The duration, in millisconds, of one frame.
     */

    private static final long FRAME_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1) / REFRESH_RATE_FPS;


    /**
     * "Hello world" text size.
     */

    private static final float TEXT_SIZE = 70f;


    /**
     * Alpha variation per frame.
     */

    private static final int ALPHA_INCREMENT = 5;


    /**
     * Max alpha value.
     */

    private static final int MAX_ALPHA = 256;

    //private final String mText;

    private int mCenterX;
    private int mCenterY;

    private int mSurfaceWidth;
    private int mSurfaceHeight;

    private SurfaceHolder mHolder;
    private boolean mRenderingPaused;
    private DataBase data;
//    private LiveCardMenuActivity act = new LiveCardMenuActivity();

    //private final FrameLayout mLayout;


    private RenderThread mRenderThread;
    private final FrameLayout mLayout;
    private final PositionView mPositionView;
    private final RelativeLayout mTipsContainer;
    private final TextView mTipsView;
    private final OrientationManager mOrientationManager;

    private final OrientationManager.OnChangedListener mPositionListener = new OrientationManager.OnChangedListener() {
        @Override
        public void onLocationChanged(OrientationManager orientationManager) {
            Location location = orientationManager.getLocation();
            List<Place> places = data.closestPlaces(location);
            mPositionView.setNearbyPlaces(places);
        }
    };



    public LiveCardRenderer(Context context, OrientationManager orientationManager, DataBase database) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mLayout = (FrameLayout)inflater.inflate(R.layout.compass,null);
        mLayout.setWillNotDraw(false);
        mPositionView = (PositionView)mLayout.findViewById(R.id.compass);
        mTipsContainer = (RelativeLayout) mLayout.findViewById(R.id.tips_container);
        mTipsView = (TextView)mLayout.findViewById(R.id.tips_view);
        mOrientationManager = orientationManager;
        data = database;
        mPositionView.setOrientationManager(mOrientationManager);

        //mText = context.getResources().getString(R.string.hello_world);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCenterX = width / 2;
        mCenterY = height / 2;
        doLayout();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mRenderingPaused = false;
        mHolder = holder;
        updateRenderingState();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
        updateRenderingState();
    }

    @Override
    public void renderingPaused(SurfaceHolder holder, boolean paused) {
        mRenderingPaused = paused;
        updateRenderingState();
    }


    /**
     * Starts or stops rendering according to the link card's state.
     */

    private void updateRenderingState() {
        boolean shouldRender = (mHolder != null) && !mRenderingPaused;
        boolean isRendering = (mRenderThread != null);

        if (shouldRender != isRendering) {
            if (shouldRender) {
                mOrientationManager.addOnChangedListener(mPositionListener);
                mOrientationManager.start();
                if(mOrientationManager.hasLocation()) {
                    Location location = mOrientationManager.getLocation();
                    List<Place> pList = data.closestPlaces(location);
                    mPositionView.setNearbyPlaces(pList);

                }
                mRenderThread = new RenderThread();
                mRenderThread.start();
            } else {
                mRenderThread.quit();
                mRenderThread = null;
                mOrientationManager.removeOnChangedListener(mPositionListener);
                mOrientationManager.stop();
            }
        }
    }


    private void doLayout() {
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(mSurfaceWidth,View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(mSurfaceHeight,View.MeasureSpec.EXACTLY);
        mLayout.measure(measuredWidth,measuredHeight);
        mLayout.layout(0,0,mLayout.getMeasuredWidth(),mLayout.getMeasuredHeight());
    }

    private synchronized void repaint() {
        Canvas canvas = null;
        try {
            mHolder.unlockCanvasAndPost(canvas);
        } catch (RuntimeException e) {
            Log.d(TAG, "unlockCanvasAndPost failed", e);

        }
    }

//    private void draw() {
//        Canvas canvas;
//        try {
//            canvas = mHolder.lockCanvas();
//        } catch (Exception e) {
//            return;
//        }
//        if (canvas != null) {
//            // Clear the canvas.
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//
//            // Update the text alpha and draw the text on the canvas.
//            mPaint.setAlpha((mPaint.getAlpha() + ALPHA_INCREMENT) % MAX_ALPHA);
//            canvas.drawText("This proves dependency on the run method", mCenterX, mCenterY, mPaint);
//
//            // Unlock the canvas and post the updates.
//            mHolder.unlockCanvasAndPost(canvas);
//        }
//    }


    /**
     * Redraws the {@link View} in the background.
     */

    private class RenderThread extends Thread {
        private boolean mShouldRun;


        /**
         * Initializes the background rendering thread.
         */

        public RenderThread() {
            mShouldRun = true;
        }


        /**
         * Returns true if the rendering thread should continue to run.
         *
         * @return true if the rendering thread should continue to run
         */

        private synchronized boolean shouldRun() {
            return mShouldRun;
        }


        /**
         * Requests that the rendering thread exit at the next opportunity.
         */

        public synchronized void quit() {
            mShouldRun = false;
        }

        @Override
        public void run() {
            while (shouldRun()) {
                long frameStart = SystemClock.elapsedRealtime();
                repaint();
                long frameLength = SystemClock.elapsedRealtime() - frameStart;

                long sleepTime = FRAME_TIME_MILLIS - frameLength;
                if (sleepTime > 0) {
                    SystemClock.sleep(sleepTime);
                }
            }
        }
    }

}