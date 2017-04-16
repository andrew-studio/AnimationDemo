package com.example.animlib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;

import com.example.animlib.annotations.Ascription;
import com.example.animlib.entity.AnimFrame;
import com.example.animlib.entity.AnimScene;
import com.example.animlib.runnables.LoadResRunnable;
import com.example.animlib.utils.FrameRateUtil;
import com.example.animlib.view.BaseSurfaceView;

/**
 * Created by zhenliang on 2017/3/22.
 */

public class AnimRenderManager implements SurfaceHolder.Callback, Runnable {
    public static final String TAG = AnimRenderManager.class.getSimpleName();
    public static final byte FLAG_ANIM_STOP = 2;
    public static final byte FLAG_ANIM_START = 1;
    public static final byte FLAG_SCREEN_CHANGED = 3;

    /**
     * SurfaceView 的有效区域
     */
    protected Rect mLocalRect = new Rect();
    protected BaseSurfaceView mSurfaceView;
    protected SurfaceHolder mSurfaceHolder;
    protected Handler mDispatchHandler;
    protected int mRenderDelay;
    protected long mStartTime;
    protected boolean mIsRender;
    protected Object mSurfaceChangeinglock = new Object();
    protected AnimThreadManager mThreadManager;
    protected SparseArray<AnimFrame> mFrameCacheArray = new SparseArray<>();

    public void setRenderState(boolean mIsRender) {
        this.mIsRender = mIsRender;
    }

    public AnimRenderManager(BaseSurfaceView surfaceView, int renderDelay, AnimThreadManager threadManager) {
        mThreadManager = threadManager;
        mSurfaceView = surfaceView;
        mRenderDelay = renderDelay;
        init();
    }

    private void init() {
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");
        initDispatchHandler();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged");
        mDispatchHandler.sendEmptyMessage(FLAG_SCREEN_CHANGED);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");
        if (mDispatchHandler != null) {
            mDispatchHandler.removeCallbacksAndMessages(null);
        }
        mThreadManager.stopDispatchThead();
    }

    private void initDispatchHandler() {
        mThreadManager.createDispatchThead();
        mDispatchHandler = new Handler(mThreadManager.getDispatchRunnableThread().getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_SCREEN_CHANGED:
                        synchronized (mSurfaceChangeinglock) {
                            setRenderState(false);
                            surfaceSizeChanged();
                            setRenderState(true);
                        }
                        break;
                    case FLAG_ANIM_STOP:
                        setRenderState(false);
                        synchronized (mSurfaceChangeinglock) {
                            cleanAnimQueue();
                        }
                        break;
                    case FLAG_ANIM_START:
                        mThreadManager.getRenderThread().execute(AnimRenderManager.this);
                        break;
                }
            }
        };
    }

    private void surfaceSizeChanged() {
        if (mSurfaceView != null) {
            mLocalRect.set(
                    mSurfaceView.getLeft()
                    , mSurfaceView.getTop()
                    , mSurfaceView.getRight()
                    , mSurfaceView.getBottom());
        }
        if (mFrameCacheArray.size() > 0) {
            int frameCacheSize = mFrameCacheArray.size();
            for (int index = 0; index < frameCacheSize; index++) {
                AnimFrame animFrame = mFrameCacheArray.valueAt(index);
                animFrame.surfaceSizeChanged(mLocalRect);
            }
        }
    }

    @Override
    public void run() {
        if (!mIsRender) {
            return;
        }
        try {
            if (mSurfaceHolder != null
                    && mSurfaceHolder.getSurface().isValid()) {
                Canvas canvas = mSurfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        FrameRateUtil.printFps();
                        if (mStartTime != 0) {
                            while (System.currentTimeMillis() - mStartTime < mRenderDelay) {
                                Thread.yield();
                            }
                        }
                        mStartTime = System.currentTimeMillis();
                        cleanSurfaceBackground(canvas);
                        if (onRender(canvas)) {
                            skip();
                        } else {
                            onComplete();
                            cleanSurfaceBackground(canvas);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, e.toString());
                    } finally {
                        if (mSurfaceHolder != null && canvas != null) {

                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanSurfaceBackground(Canvas canvas) {
        // 还有在draw方法中绘制背景颜色的时候以下面的方式进行绘制就可以实现SurfaceView的背景透明化
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    public boolean onRender(Canvas canvas) {
        boolean continueRender = false;
        int frameCacheSize = mFrameCacheArray.size();
        for (int index = 0; index < frameCacheSize; index++) {
            AnimFrame animFrame = mFrameCacheArray.valueAt(index);
            boolean isContinue = animFrame.onRender(canvas);
            if (!isContinue) {
                animFrame.cleanCacheScenes();
            }
            continueRender = isContinue || continueRender;
        }
        return continueRender;
    }

    private void cleanAnimQueue() {
        int frameCacheSize = mFrameCacheArray.size();
        for (int index = 0; index < frameCacheSize; index++) {
            mFrameCacheArray.valueAt(index).cleanAnimScenes();
        }
    }

    private void onComplete() {
        mStartTime = 0;
        mDispatchHandler.sendEmptyMessage(FLAG_ANIM_STOP);
    }

    private void skip() {
        mDispatchHandler.sendEmptyMessage(FLAG_ANIM_START);
    }

    public void addScenes(AnimScene[] scenes) {
        AnimFrame animFrame = null;
        Ascription ascription = scenes[0].getClass().getAnnotation(Ascription.class);
        if (ascription == null) {
            Log.e(TAG, "AnimScene Unregistered AnimFrame !!!");
            return;
        }
        mThreadManager.getLoadResThreads().execute(new LoadResRunnable(scenes));
        String value = ascription.value();
        synchronized (mSurfaceChangeinglock) {
            try {
                Class<?> clazz = Class.forName(value);
                int hashCode = clazz.hashCode();
                animFrame = mFrameCacheArray.get(hashCode);
                if (animFrame == null) {
                    animFrame = (AnimFrame) clazz.newInstance();
                    animFrame.initLocalRect(mLocalRect);
                    mFrameCacheArray.append(hashCode, animFrame);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (animFrame == null) {
                Log.e("TAG", "AnimScene Unregistered AnimFrame !!!");
                return;
            }
            animFrame.addAnimScenes(scenes);
            setRenderState(true);
            mDispatchHandler.sendEmptyMessage(FLAG_ANIM_START);
        }
    }

    public void setRenderDelay(int renderDelay) {
        mRenderDelay = renderDelay;
    }
}
