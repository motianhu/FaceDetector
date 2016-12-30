/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smona.app.detect.facedetector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

/**
 * View which displays a bitmap containing a face along with overlay graphics that identify the
 * locations of detected facial landmarks.
 */
public class FaceView extends View {
    private Bitmap mSourceBitmap;
    private SparseArray<Face> mFaces;

    private Bitmap mHeaderBitmap;
    private Bitmap mLeftBitmap;
    private Bitmap mRightBitmap;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the bitmap background and the associated face detections.
     */
    void setContent(Bitmap bitmap, SparseArray<Face> faces) {
        mSourceBitmap = bitmap;
        mFaces = faces;
        invalidate();
    }

    void setHeaderBitmap(Bitmap bitmap) {
        mHeaderBitmap = bitmap;
    }

    void setLeftBitmap(Bitmap bitmap) {
        mLeftBitmap = bitmap;
    }

    void setRightBitmap(Bitmap bitmap) {
        mRightBitmap = bitmap;
    }

    /**
     * Draws the bitmap background and the associated face landmarks.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((mSourceBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
            drawFaceAnnotations(canvas, scale);
        }
    }

    /**
     * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
     * positioning the facial landmark graphics.
     */
    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mSourceBitmap.getWidth();
        double imageHeight = mSourceBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int) (imageWidth * scale), (int) (imageHeight * scale));
        canvas.drawBitmap(mSourceBitmap, null, destBounds, null);
        return scale;
    }

    /**
     * Draws a small circle for each detected landmark, centered at the detected landmark position.
     * <p>
     * <p>
     * Note that eye landmarks are defined to be the midpoint between the detected eye corner
     * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
     * pupil position.
     */
    private void drawFaceAnnotations(Canvas canvas, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        for (int i = 0; i < mFaces.size(); ++i) {
            Face face = mFaces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                drawDecorate(canvas, scale, landmark);

                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, 10, paint);
            }
        }
    }

    private void drawDecorate(Canvas canvas, double scale, Landmark landmark) {
        Log.d("moth", "landmark.getType()=" + landmark.getType());
        if (landmark.getType() == Landmark.LEFT_CHEEK) {
            int x = (int) (landmark.getPosition().x * scale);
            int y = (int) (landmark.getPosition().y * scale);
            int w = 100;
            int h = 100;
            Rect destBounds = new Rect(x - w / 2, y - h / 2, x + w / 2, y + h / 2);
            Log.d("moth", "drawDecorate left=" + destBounds);
            canvas.drawBitmap(mLeftBitmap, null, destBounds, null);
        } else if (landmark.getType() == Landmark.RIGHT_CHEEK) {
            int x = (int) (landmark.getPosition().x * scale);
            int y = (int) (landmark.getPosition().y * scale);
            int w = 100;
            int h = 100;
            Rect destBounds = new Rect(x - w / 2, y - h / 2, x + w / 2, y + h / 2);
            Log.d("moth", "drawDecorate right=" + destBounds);
            canvas.drawBitmap(mRightBitmap, null, destBounds, null);
        }
    }
}
