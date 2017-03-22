package com.example.animlib;

import com.example.animlib.view.AnimSurfaceView;

/**
 * Created by Administrator on 2017/3/22.
 */

public class AnimController {

    private AnimRenderManager mRenderManager;
    private int fps = 31; // 帧率
    private int renderDelay = 1000 / fps;

    public AnimController(AnimSurfaceView surfaceView) {
        mRenderManager = new AnimRenderManager(surfaceView,renderDelay);
    }
}
