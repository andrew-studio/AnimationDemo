package com.example.animlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by zhenliang on 2017/4/16.
 */

public abstract class BaseSurfaceView extends SurfaceView {
    public BaseSurfaceView(Context context) {
        super(context);
        init();
    }

    public BaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setZOrderMode();
        setSurfaceViewBackground();
    }

    protected abstract void setSurfaceViewBackground();

    protected abstract void setZOrderMode();
}
