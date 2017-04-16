package com.example.administrator.animationdemo.example.flytext;

import com.example.animlib.entity.AnimParameter;

/**
 * Date: 2016/7/21
 * Author: xianghui
 */
public class FlyTextSceneParameter extends AnimParameter {
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
