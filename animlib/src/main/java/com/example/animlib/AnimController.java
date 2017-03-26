package com.example.animlib;

import android.util.Log;

import com.example.animlib.interfaces.IAnimSceneCreater;
import com.example.animlib.runnables.LoadResRunnable;
import com.example.animlib.view.AnimSurfaceView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/3/22.
 */

public class AnimController {
    private String TAG = AnimController.class.getSimpleName();
    private AnimRenderManager mRenderManager;
    private int fps = 31; // 帧率
    private int renderDelay = 1000 / fps;
    private ExecutorService mLoadSceneThread;

    public AnimController(AnimSurfaceView surfaceView) {
        mRenderManager = new AnimRenderManager(surfaceView, renderDelay);
        mLoadSceneThread = Executors.newSingleThreadExecutor();
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void addScene(Object o, IAnimSceneCreater sceneCreater) {
        mLoadSceneThread.submit(new LoadSceneRunnable(o, sceneCreater));
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
            if (scenes == null || scenes.length > 0) {
                Log.e(TAG, "LoadSceneRunnable NoAnimScene!!!");
                return;
            }
            mRenderManager.addScenes(scenes);
        }
    }
}
