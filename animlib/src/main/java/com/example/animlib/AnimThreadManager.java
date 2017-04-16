package com.example.animlib;

import android.os.HandlerThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhenliang on 2017/4/16.
 */

public class AnimThreadManager {
    private ExecutorService mLoadLocalRunnableThread;
    private ExecutorService mRenderThread;
    private ExecutorService mLoadResThreads;
    private HandlerThread mDispatchRunnableThread;

    public AnimThreadManager() {
        init();
    }

    private void init() {
        mLoadLocalRunnableThread = Executors.newSingleThreadExecutor();
        mRenderThread = Executors.newSingleThreadExecutor();
        mLoadResThreads = Executors.newFixedThreadPool(2);
    }

    public ExecutorService getLoadLocalRunnableThread() {
        return mLoadLocalRunnableThread;
    }

    public ExecutorService getRenderThread() {
        return mRenderThread;
    }

    public ExecutorService getLoadResThreads() {
        return mLoadResThreads;
    }

    public HandlerThread getDispatchRunnableThread() {
        return mDispatchRunnableThread;
    }

    public void createDispatchThead() {
        mDispatchRunnableThread = new HandlerThread("DispatchThread");
        mDispatchRunnableThread.start();
    }

    public void stopDispatchThead() {
        if (mDispatchRunnableThread != null) {
            mDispatchRunnableThread.quit();
            mDispatchRunnableThread = null;
        }
    }
}
