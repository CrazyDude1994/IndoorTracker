package com.crazydude.indoortracker.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import static com.crazydude.indoortracker.utils.WifiUtils.calculateSignalLevel;

/**
 * Created by Crazy on 25.10.2016.
 */

public class MapperView extends View {

    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private Matrix mCameraMatrix;
    private Paint mDrawPaint;
    private int mMapWidth, mMapHeight;
    private float mLongestWall;
    private Mode mCurrentMode = Mode.VIEW;
    private List<WifiPoint> mWifiPoints = new ArrayList<>();
    private WifiMapPointListener mWifiMapPointListener;
    private float mPixelsPerMeter;
    private int mFullSize;

    public enum Mode {
        VIEW, MAP
    }

    public MapperView(Context context) {
        super(context);
        init();
    }

    public MapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MapperView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public Mode getCurrentMode() {
        return mCurrentMode;
    }

    public void createMap(int width, int height) {
        mMapWidth = width;
        mMapHeight = height;
        mLongestWall = (mMapHeight >= mMapWidth) ? mMapHeight : mMapWidth;
    }

    public void switchMode(Mode mode) {
        mCurrentMode = mode;
    }

    public void setWifiMapPointListener(WifiMapPointListener wifiMapPointListener) {
        mWifiMapPointListener = wifiMapPointListener;
    }

    public void update() {
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event) | mScaleGestureDetector.onTouchEvent(event)) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMapWidth == 0 || mMapHeight == 0) return;

        mFullSize = (canvas.getHeight() >= canvas.getWidth()) ? canvas.getWidth() : canvas.getHeight();
        mPixelsPerMeter = mFullSize / mLongestWall;

        canvas.setMatrix(mCameraMatrix);
        drawGrid(canvas);
        drawWalls(canvas);
        drawWifiPoints(canvas);
    }

    private void drawWifiPoints(Canvas canvas) {
        mDrawPaint.setTextSize(64);
        for (WifiPoint wifiPoint : mWifiPoints) {
            if (wifiPoint.getScanResults() != null && wifiPoint.getScanResults().size() > 0) {
                int signalLevel = calculateSignalLevel(wifiPoint.getScanResults().get(0).level, 100) + 1;
                double distance = 100 - signalLevel;
                float[] canvasCoordinates = mapToCanvasCoordinates(wifiPoint.getX(), wifiPoint.getY());
                mDrawPaint.setARGB(200, 0, 255, 0);
                canvas.drawCircle(canvasCoordinates[0], canvasCoordinates[1], (float) (Math.sqrt(distance) * mPixelsPerMeter), mDrawPaint);
                mDrawPaint.setARGB(255, 0, 0, 0);
/*                canvas.drawText(String.valueOf(distance),
                        wifiPoint.getX(), wifiPoint.getY(), mDrawPaint)*/
                ;
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        mDrawPaint.setARGB(255, 174, 234, 255);
        mDrawPaint.setStrokeWidth(4);

        for (int i = 1; i < mMapHeight; i++) {
            canvas.drawLine(0, i * mPixelsPerMeter, mMapWidth * mPixelsPerMeter, i * mPixelsPerMeter, mDrawPaint);
        }

        for (int i = 1; i < mMapWidth; i++) {
            canvas.drawLine(i * mPixelsPerMeter, 0, i * mPixelsPerMeter, mMapHeight * mPixelsPerMeter, mDrawPaint);
        }
    }

    private void drawWalls(Canvas canvas) {
        mDrawPaint.setARGB(255, 0, 0, 0);
        mDrawPaint.setStrokeWidth(16);

        float koefWallWidth = mMapWidth / mLongestWall;
        float koefWallHeight = mMapHeight / mLongestWall;

        canvas.drawLine(
                0,
                0,
                koefWallWidth * mFullSize,
                0,
                mDrawPaint); //top wall
        canvas.drawLine(
                koefWallWidth * mFullSize,
                0,
                koefWallWidth * mFullSize,
                koefWallHeight * mFullSize,
                mDrawPaint); //right wall
        canvas.drawLine(
                koefWallWidth * mFullSize,
                koefWallHeight * mFullSize,
                0,
                koefWallHeight * mFullSize,
                mDrawPaint); //bottom wall
        canvas.drawLine(
                0,
                koefWallHeight * mFullSize,
                0,
                0,
                mDrawPaint); //left wall
    }

    private void init() {
        mGestureDetector = new GestureDetectorCompat(getContext(), new GestureDetectorListener());
        mScaleGestureDetector = new android.view.ScaleGestureDetector(getContext(), new ScaleGestureDetectorListener());
        mCameraMatrix = new Matrix();
        mDrawPaint = new Paint();
        mDrawPaint.setARGB(255, 255, 0, 0);
        mDrawPaint.setStrokeWidth(25);
    }

    private void scaleImage(float scaleFactor, float focusX, float focusY) {
        mCameraMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        invalidate();
    }

    private void createMapPoint(float x, float y) {
        WifiPoint wifiPoint = new WifiPoint(x, y);
        mWifiPoints.add(wifiPoint);
        invalidate();
        if (mWifiMapPointListener != null) {
            mWifiMapPointListener.onMapWifi(wifiPoint);
        }
    }

    private float[] mapToWorldCoordinates(float[] points) {
        if (points.length == 2) {
            return new float[]{points[0] / mPixelsPerMeter, points[1] / mPixelsPerMeter};
        } else {
            throw new IllegalArgumentException("Points array must be size of 2");
        }
    }

    private float[] mapToCanvasCoordinates(float x, float y) {
        return new float[] {x * mPixelsPerMeter, y * mPixelsPerMeter};
    }

    public interface WifiMapPointListener {

        void onMapWifi(WifiPoint wifiPoint);
    }

    private class GestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mCurrentMode == Mode.MAP) {
                Matrix inverseMatrix = new Matrix();
                mCameraMatrix.invert(inverseMatrix);

                float[] points = {e.getX(), e.getY()};
                inverseMatrix.mapPoints(points);

                float[] worldCoordinates = mapToWorldCoordinates(points);
                createMapPoint(worldCoordinates[0], worldCoordinates[1]);
                return true;
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mCameraMatrix.postTranslate(-distanceX, -distanceY);
            MapperView.this.invalidate();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private class ScaleGestureDetectorListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(android.view.ScaleGestureDetector detector) {
            float scaleFactor = Math.max(0.1f,
                    Math.min(detector.getScaleFactor(), 5.0f));
            scaleImage(scaleFactor, detector.getFocusX(), detector.getFocusY());
            return true;
        }
    }
}
