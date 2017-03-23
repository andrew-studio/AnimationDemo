package com.example.animlib.utils;

import android.os.SystemClock;

/**
 * Created by Android on 2017/3/23.
 */
public class FrameRateUtil {
    private static final long ONE_SECOND = 1000;
    private static float fps;
    private static long startTime;

    private FrameRateUtil() {
    }

    public static float getFps() {
        if (startTime == 0) {
            long useTime = SystemClock.uptimeMillis() - startTime;
            return fps = ONE_SECOND / useTime;
        }
        startTime = SystemClock.uptimeMillis();
        return 0;
    }
}
