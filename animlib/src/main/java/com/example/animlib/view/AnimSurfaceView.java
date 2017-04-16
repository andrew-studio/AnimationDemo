package com.example.animlib.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by zhenliang on 2017/3/22.
 */

public class AnimSurfaceView extends BaseSurfaceView {
    public AnimSurfaceView(Context context) {
        super(context);
    }

    public AnimSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setZOrderMode() {
        setZOrderOnTop(true);
    }

    @Override
    protected void setSurfaceViewBackground() {
        // 设置画布背景不为黑色 继承Sureface时这样处理才能透明
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setBackgroundColor(Color.TRANSPARENT);
    }
}
