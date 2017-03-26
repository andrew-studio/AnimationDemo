package com.example.administrator.animationdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.example.animlib.AnimScene;
import com.example.animlib.annotations.Ascription;

/**
 * Date: 2016/7/21
 * Author: xianghui
 */
@Ascription("com.example.administrator.animationdemo.FlySceneFrame")
public class FlyScene extends AnimScene<FlySceneParameter> {


    private int x;
    private int y;

    public FlyScene(FlySceneParameter animParameter) {
        super(animParameter);
    }

    @Override
    protected int initMaxFrameCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected boolean frameControl(int currentFrameIndex) {
        if (currentFrameIndex == 1) {
            x = getLocalRect().right;
            y = getLocalRect().bottom / 2;
        }
        return true;
    }

    Paint mPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(25);
        canvas.drawText("aaaa", x, y, mPaint);
        x = x - 3;
        if (x < -getLocalRect().right) {
            stopRender();
        }
    }

    @Override
    public void loadLocalDatas() {

    }

    @Override
    public void loadNetworkDatas() {

    }
}
