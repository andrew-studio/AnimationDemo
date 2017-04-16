package com.example.administrator.animationdemo.example.flytext;

import com.example.animlib.entity.AnimScene;
import com.example.animlib.interfaces.IAnimSceneCreater;

/**
 * Created by zhenliang on 2017/4/16.
 */

public class FlyTextSceneCreater implements IAnimSceneCreater {
    @Override
    public AnimScene[] createScenes(Object object) {
        if (object instanceof String) {
            FlyTextSceneParameter flyTextSceneParameter = new FlyTextSceneParameter();
            flyTextSceneParameter.setText((String) object);
            return new AnimScene[]{new FlyTextScene(flyTextSceneParameter)};
        }
        return null;
    }
}
