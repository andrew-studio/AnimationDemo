package com.example.administrator.animationdemo.example.flytext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.animlib.annotations.Ascription;
import com.example.animlib.entity.AnimScene;

/**
 * Date: 2016/7/21
 * Author: xianghui
 */
@Ascription("com.example.administrator.animationdemo.example.flytext.FlyTextSceneFrame")
public class FlyTextScene extends AnimScene<FlyTextSceneParameter> {

    private Paint mPaint;
    private int mSpeed = 3;
    private int targetX;
    private int targetY;
    private int textWidth;

    public FlyTextScene(FlyTextSceneParameter animParameter) {
        super(animParameter);
        init();
    }

    private void init() {

    }

    @Override
    protected int initMaxFrameCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected boolean frameControl(int currentFrameIndex) {
        if (currentFrameIndex == 0) {
            targetX = mLocalRect.right;
            targetY = mLocalRect.centerY();
        }
        if (targetX < -textWidth) {
            stopRender();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText(getParameter().getText(), targetX, targetY, mPaint);
        targetX -= mSpeed;
    }

    @Override
    public void loadLocalDatas() {
        mPaint = new Paint();
        mPaint.setTextSize(50);
        mPaint.setColor(Color.BLACK);
        textWidth = (int) (mPaint.measureText(getParameter().getText()) + 0.5);
    }

    @Override
    public void loadNetworkDatas() {

    }
}
