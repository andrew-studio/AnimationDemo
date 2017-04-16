package com.example.animlib.entity;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.animlib.interfaces.IRender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhenliang on 2017/3/23.
 */
public abstract class AnimFrame<T extends AnimScene> implements IRender {
    private int mMaxScene;
    private Rect mLocalRect;
    private List<T> mSceneCacheList;
    private List<T> mRenderingCacheList;
    private List<T> mCleanCacheList;
    private T mPrepareScene;
    private ExecutorService mFillSceneThread;

    public AnimFrame() {
        initLogic();
    }

    private void initLogic() {
        int tempMaxScene = initMaxScene();
        mMaxScene = tempMaxScene > 0 ? tempMaxScene : 1;
        mSceneCacheList = new ArrayList<>();
        mCleanCacheList = new ArrayList<>();
        mRenderingCacheList = new ArrayList<>(mMaxScene);
        mFillSceneThread = Executors.newCachedThreadPool();
    }

    protected abstract int initMaxScene();

    @Override
    public boolean onRender(Canvas canvas) {
        if (mSceneCacheList.size() == 0
                && mRenderingCacheList.size() == 0) {
            return false;
        }
        fillRenderingScene();
        int renderingCacheSize = mRenderingCacheList.size();
        for (int index = 0; index < renderingCacheSize; index++) {
            T renderingScene = mRenderingCacheList.get(index);
            renderingScene.surfaceSizeChanged(mLocalRect);
            boolean isContinueTemp = renderingScene.onRender(canvas);
            if (!isContinueTemp) {
                mCleanCacheList.add(renderingScene);
            }
        }
        mRenderingCacheList.removeAll(mCleanCacheList);
        return !(mSceneCacheList.size() == 0 && mRenderingCacheList.size() == 0);
    }

    private void fillRenderingScene() {
        if (mRenderingCacheList.size() < mMaxScene
                && mSceneCacheList.size() > 0) {
            mFillSceneThread.submit(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        while (mRenderingCacheList.size() < mMaxScene
                                && mSceneCacheList.size() > 0) {
                            mPrepareScene = mSceneCacheList.get(0);
                            mRenderingCacheList.add(mPrepareScene);
                            mSceneCacheList.remove(mPrepareScene);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void surfaceSizeChanged(Rect localRect) {
        mLocalRect = localRect;
    }

    public void initLocalRect(Rect localRect) {
        mLocalRect = localRect;
    }

    public void addAnimScenes(T[] animScenes) {
        mSceneCacheList.addAll(Arrays.asList(animScenes));
    }

    public void cleanAnimScenes() {
        mSceneCacheList.clear();
    }

    public void cleanCacheScenes() {
        mCleanCacheList.clear();
    }

}
