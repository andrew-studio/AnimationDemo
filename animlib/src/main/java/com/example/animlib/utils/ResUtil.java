package com.example.animlib.utils;

/**
 * Created by zhenliang on 2017/3/23.
 */
public class ResUtil {
    private volatile static ResUtil instans;

    private ResUtil() {
    }

    public static ResUtil getInstans() {
        if (instans == null) {
            synchronized (ResUtil.class) {
                if (instans == null) {
                    instans = new ResUtil();
                }
            }
        }
        return instans;
    }

}
