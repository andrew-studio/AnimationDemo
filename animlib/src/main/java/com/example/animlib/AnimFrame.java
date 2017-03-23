package com.example.animlib;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.animlib.interfaces.IRender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2017/3/23.
 */
public class AnimFrame implements IRender {

    private Rect mLocalRect;
    private List<AnimScene> mSceneCacheList=new ArrayList<>();

    @Override
    public boolean onRender(Canvas canvas) {
        return false;
    }

    @Override
    public void surfaceSizeChanged(Rect localRect) {
        mLocalRect = localRect;
    }
}
