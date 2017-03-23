package com.example.animlib.interfaces;

import android.graphics.Canvas;

/**
 * Created by Android on 2017/3/23.
 */
public interface IRender {
    /**
     * @return ture 继续渲染，false 渲染结束
     */
    boolean onRender(Canvas canvas);
}
