package com.example.animlib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.SurfaceHolder;

import com.example.animlib.view.AnimSurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/3/22.
 */

public class AnimRenderManager implements SurfaceHolder.Callback, Runnable {
    private static final byte FLAG_ANIM_START = 1;
    private static final byte FLAG_ANIM_PAUSE = 2;
    private static final byte FLAG_ANIM_STOP = 3;
    private static final byte FLAG_SCREEN_CHANGED = 4;

    /**
     * SurfaceView 的有效区域
     */
    protected Rect mLocalRect = new Rect();
    protected AnimSurfaceView mSurfaceView;
    protected SurfaceHolder mSurfaceHolder;
    protected HandlerThread mDispatchThread;
    protected Handler mDispatchHandler;
    private ExecutorService mRenderThread;
    protected int mRenderDelay;
    protected long mStartTime;
    protected boolean mIsRender;
    protected List<AnimFrame> mFrameCacheList = new ArrayList<>();
    protected List<AnimFrame> mFrameRenderFinishList = new ArrayList<>();

    public void setRenderState(boolean mIsRender) {
        this.mIsRender = mIsRender;
    }

    public AnimRenderManager(AnimSurfaceView surfaceView, int renderDelay) {
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
        initDispatchThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mDispatchHandler.sendEmptyMessage(FLAG_SCREEN_CHANGED);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mRenderThread != null) {
            mRenderThread.shutdownNow();
        }
        if (mDispatchHandler != null) {
            mDispatchHandler.removeCallbacksAndMessages(null);
        }
        if (mDispatchThread != null) {
            mDispatchThread.quit();
        }
    }

    private void initDispatchThread() {
        mRenderThread = Executors.newSingleThreadExecutor();
        mDispatchThread = new HandlerThread("DispatchThread");
        mDispatchHandler = new Handler(mDispatchThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_SCREEN_CHANGED:
                        setRenderState(false);
                        surfaceSizeChanged();
                        break;
                    case FLAG_ANIM_START:
                        setRenderState(true);
                        mRenderThread.submit(AnimRenderManager.this);
                        break;
                    case FLAG_ANIM_PAUSE:
                        setRenderState(false);
                        break;
                    case FLAG_ANIM_STOP:
                        setRenderState(false);
                        synchronized (mDispatchThread) {
                            cleanAnimQueue();
                        }
                        break;
                }
            }
        };
        mDispatchThread.start();
    }

    private void surfaceSizeChanged() {
        if (mSurfaceView != null) {
            mLocalRect.set(mSurfaceView.getLeft(), mSurfaceView.getTop(), mSurfaceView.getRight(), mSurfaceView.getBottom());
        }
        if (mFrameCacheList.size() > 0) {
            for (AnimFrame animFrame : mFrameCacheList) {
                animFrame.surfaceSizeChanged(mLocalRect);
            }
            setRenderState(true);
        }
    }

    @Override
    public void run() {
        if (!mIsRender) {
            return;
        }
        if (mSurfaceHolder != null) {
            synchronized (mSurfaceHolder) {
                try {
                    if (mSurfaceHolder != null
                            && mSurfaceHolder.getSurface().isValid()) {
                        Canvas canvas = mSurfaceHolder.lockCanvas();
                        if (canvas != null) {
                            try {
                                if (mStartTime != 0) {
                                    while (System.currentTimeMillis() - mStartTime < mRenderDelay) {
                                        Thread.yield();
                                    }
                                }
                                mStartTime = System.currentTimeMillis();
                                // 还有在draw方法中绘制背景颜色的时候以下面的方式进行绘制就可以实现SurfaceView的背景透明化
                                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                                if (onRender(canvas)) {
                                    skip();
                                } else {
                                    onComplete();
                                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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
        }
    }

    public boolean onRender(Canvas canvas) {
        boolean renderFinished = false;
        synchronized (mDispatchThread) {
            int frameCacheSize = mFrameCacheList.size();
            for (int index = 0; index < frameCacheSize; index++) {
                AnimFrame animFrame = mFrameCacheList.get(index);
                boolean isContinue = animFrame.onRender(canvas);
                if (!isContinue) {
                    mFrameRenderFinishList.add(animFrame);
                }
                renderFinished = isContinue || renderFinished;
            }
            if (mFrameRenderFinishList.size() != 0) {
                mFrameCacheList.removeAll(mFrameRenderFinishList);
                mFrameRenderFinishList.clear();
            }
            if (renderFinished) {
                cleanAnimQueue();
            }
        }
        return renderFinished;
    }

    private void cleanAnimQueue() {
        mFrameCacheList.clear();
    }

    private void onComplete() {
        mStartTime = 0;
    }

    private void skip() {
        mDispatchHandler.sendEmptyMessage(FLAG_ANIM_START);
    }
}
