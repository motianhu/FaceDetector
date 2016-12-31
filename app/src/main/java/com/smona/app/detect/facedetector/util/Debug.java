package com.smona.app.detect.facedetector.util;

import android.util.Log;

/**
 * Created by motianhu on 12/31/16.
 */

public class Debug {

    private static final boolean DEBUG = false;
    private static final String TAG = "moth";

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }


}
