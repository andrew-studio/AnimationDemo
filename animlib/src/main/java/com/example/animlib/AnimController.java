package com.example.animlib;

import android.util.Log;

import com.example.animlib.entity.AnimScene;
import com.example.animlib.interfaces.IAnimSceneCreater;
import com.example.animlib.view.BaseSurfaceView;

/**
 * Created by zhenliang on 2017/3/22.
 */

public class AnimController {
    private String TAG = AnimController.class.getSimpleName();
    private AnimRenderManager mRenderManager;
    private int fps = 30; // 帧率
    private int renderDelay = 1000 / fps;
    private AnimThreadManager mThreadManager;

    public AnimController(BaseSurfaceView surfaceView) {
        mThreadManager=new AnimThreadManager();
        mRenderManager = new AnimRenderManager(surfaceView, renderDelay,mThreadManager);
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
        this.renderDelay = 1000 / fps;
        if (mRenderManager != null) {
            mRenderManager.setRenderDelay(renderDelay);
        }
    }

    public void addScene(Object o, IAnimSceneCreater sceneCreater) {
        mThreadManager.getLoadLocalRunnableThread().submit(new LoadSceneRunnable(o, sceneCreater));
    }

    public class LoadSceneRunnable implements Runnable {
        private Object object;
        private IAnimSceneCreater mSceneCreater;

        public LoadSceneRunnable(Object o, IAnimSceneCreater sceneCreater) {
            object = o;
            mSceneCreater = sceneCreater;
        }

        @Override
        public void run() {
            if (object == null || mSceneCreater == null || mRenderManager == null) {
                Log.e(TAG, "LoadSceneRunnable Missing parameter!!!");
                return;
            }
            AnimScene[] scenes = mSceneCreater.createScenes(object);
            if (scenes == null || scenes.length == 0) {
                Log.e(TAG, "LoadSceneRunnable NoAnimScene!!!");
                return;
            }
            mRenderManager.addScenes(scenes);
        }
    }
}
