package com.example.animlib;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.animlib.interfaces.IRender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Android on 2017/3/23.
 */
public abstract class AnimFrame<T extends AnimScene> implements IRender {
    private int mMaxScene;
    private Rect mLocalRect;
    private List<T> mSceneCacheList;
    private List<T> mRenderingCacheList;
    private List<T> mCleanCacheList;
    private AnimScene mPrepareScene;

    public AnimFrame() {
        initLogic();
    }

    private void initLogic() {
        int tempMaxScene = initMaxScene();
        mMaxScene = tempMaxScene > 0 ? tempMaxScene : 1;
        mSceneCacheList = new ArrayList<>();
        mCleanCacheList = new ArrayList<>();
        mRenderingCacheList = new ArrayList<>(mMaxScene);
    }

    protected abstract int initMaxScene();

    @Override
    public boolean onRender(Canvas canvas) {
        if (mSceneCacheList.size() == 0
                && mRenderingCacheList.size() == 0) {
            return false;
        }
        boolean isContinue = false;
        for (T renderingScene : mRenderingCacheList) {
            boolean isContinueTemp = renderingScene.onRender(canvas);
            if (!isContinueTemp) {
                mCleanCacheList.add(renderingScene);
            }
            isContinue = isContinueTemp || isContinue;
        }
        mRenderingCacheList.removeAll(mCleanCacheList);
        return isContinue;
    }

    @Override
    public void surfaceSizeChanged(Rect localRect) {
        mLocalRect = localRect;
    }

    protected synchronized void addAnimScenes(T[] animScenes) {
        mSceneCacheList.addAll(Arrays.asList(animScenes));
    }

    protected synchronized void removeAnimScenes(List<T> animScenes) {
        mSceneCacheList.removeAll(animScenes);
    }

    protected synchronized void cleanAnimScenes() {
        mSceneCacheList.clear();
    }
    protected void cleanCacheScenes() {
        mCleanCacheList.clear();
    }

}
