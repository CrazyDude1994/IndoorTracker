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

        int fullSize = (canvas.getHeight() >= canvas.getWidth()) ? canvas.getWidth() : canvas.getHeight();

        canvas.setMatrix(mCameraMatrix);
        drawGrid(canvas, fullSize);
        drawWalls(canvas, fullSize);
        drawWifiPoints(canvas, fullSize);
    }

    private void drawWifiPoints(Canvas canvas, int fullSize) {
        mDrawPaint.setARGB(200, 0, 255, 0);
        mDrawPaint.setStrokeWidth(30);
        float pixelsPerMeter = fullSize / mLongestWall;
        for (WifiPoint wifiPoint : mWifiPoints) {
            canvas.drawCircle(wifiPoint.getX(), wifiPoint.getY(), pixelsPerMeter, mDrawPaint);
        }
    }

    private void drawGrid(Canvas canvas, int fullSize) {
        mDrawPaint.setARGB(255, 174, 234, 255);
        mDrawPaint.setStrokeWidth(4);
        float pixelsPerMeter = fullSize / mLongestWall;

        for (int i = 1; i < mMapHeight; i++) {
            canvas.drawLine(0, i * pixelsPerMeter, mMapWidth * pixelsPerMeter, i * pixelsPerMeter, mDrawPaint);
        }

        for (int i = 1; i < mMapWidth; i++) {
            canvas.drawLine(i * pixelsPerMeter, 0, i * pixelsPerMeter, mMapHeight * pixelsPerMeter, mDrawPaint);
        }
    }

    private void drawWalls(Canvas canvas, int fullSize) {
        mDrawPaint.setARGB(255, 0, 0, 0);
        mDrawPaint.setStrokeWidth(16);

        float koefWallWidth = mMapWidth / mLongestWall;
        float koefWallHeight = mMapHeight / mLongestWall;

        canvas.drawLine(
                0,
                0,
                koefWallWidth * fullSize,
                0,
                mDrawPaint); //top wall
        canvas.drawLine(
                koefWallWidth * fullSize,
                0,
                koefWallWidth * fullSize,
                koefWallHeight * fullSize,
                mDrawPaint); //right wall
        canvas.drawLine(
                koefWallWidth * fullSize,
                koefWallHeight * fullSize,
                0,
                koefWallHeight * fullSize,
                mDrawPaint); //bottom wall
        canvas.drawLine(
                0,
                koefWallHeight * fullSize,
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

                createMapPoint(points[0], points[1]);
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
