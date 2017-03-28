package com.example.animlib.interfaces;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by zhenliang on 2017/3/23.
 */
public interface IRender {
    /**
     * @return ture 继续渲染，false 渲染结束
     */
    boolean onRender(Canvas canvas);

    void surfaceSizeChanged(Rect localRect);
}
