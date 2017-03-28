package com.example.animlib;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.animlib.interfaces.IRender;

/**
 * Created by zhenliang on 2017/3/23.
 */
public abstract class AnimScene<C extends AnimParameter> implements IRender {
    protected Rect mLocalRect;
    protected int mCurrentFrameIndex;
    protected int mMaxFrameCount;
    protected C mAnimParameter;

    public AnimScene(C animParameter) {
        mAnimParameter = animParameter;
        init();
    }

    private void init() {
        mMaxFrameCount = initMaxFrameCount();
    }

    protected abstract int initMaxFrameCount();

    @Override
    final public boolean onRender(Canvas canvas) {
        if (mCurrentFrameIndex > mMaxFrameCount) {
            return false;
        }
        if (frameControl(mCurrentFrameIndex)) {
            onDraw(canvas);
            mCurrentFrameIndex++;
            return true;
        }
        return false;
    }

    public void stopRender() {
        mMaxFrameCount = mCurrentFrameIndex;
    }

    /**
     * 帧控制
     *
     * @param currentFrameIndex 当前正准备绘制的帧数
     * @return 是否继续绘制
     */
    protected abstract boolean frameControl(int currentFrameIndex);

    @Override
    public void surfaceSizeChanged(Rect localRect) {
        mLocalRect = localRect;
    }

    protected abstract void onDraw(Canvas canvas);

    public abstract void loadLocalDatas();

    public abstract void loadNetworkDatas();

    protected C getParameter() {
        return mAnimParameter;
    }

    protected Rect getLocalRect() {
        return mLocalRect;
    }
}
