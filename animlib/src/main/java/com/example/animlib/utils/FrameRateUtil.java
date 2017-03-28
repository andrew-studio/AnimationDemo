package com.example.animlib.utils;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by zhenliang on 2017/3/23.
 */
public class FrameRateUtil {
    private static final String TAG = FrameRateUtil.class.getSimpleName();
    private static final long ONE_SECOND = 1000;
    private static float fps;
    private static long startTime;

    private FrameRateUtil() {
    }

    private static float getFps() {
        if (startTime != 0) {
            long useTime = System.currentTimeMillis() - startTime;
            startTime = System.currentTimeMillis();
            return fps = ONE_SECOND / useTime;
        }
        startTime = SystemClock.uptimeMillis();
        return 0;
    }

    public static void printFps() {
        Log.e(TAG, "Fps(" + String.valueOf(getFps()) + ")");
    }
}
