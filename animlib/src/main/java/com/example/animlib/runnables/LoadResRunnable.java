package com.example.animlib.runnables;

import com.example.animlib.entity.AnimScene;

/**
 * Created by zhenliang on 2017/3/26.
 */

public class LoadResRunnable implements Runnable {
    private AnimScene[] mScenes;

    public LoadResRunnable(AnimScene[] scenes) {
        mScenes = scenes;
    }

    @Override
    public void run() {
        if (mScenes == null || mScenes.length == 0) {
            return;
        }
        for (AnimScene scene : mScenes) {
            scene.loadLocalDatas();
        }
        for (AnimScene scene : mScenes) {
            scene.loadNetworkDatas();
        }
    }
}
