package com.example.administrator.animationdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.animationdemo.example.flytext.FlyTextScene;
import com.example.administrator.animationdemo.example.flytext.FlyTextSceneCreater;
import com.example.administrator.animationdemo.example.flytext.FlyTextSceneParameter;
import com.example.animlib.AnimController;
import com.example.animlib.entity.AnimScene;
import com.example.animlib.interfaces.IAnimSceneCreater;
import com.example.animlib.view.AnimSurfaceView;

public class MainActivity extends AppCompatActivity {

    private AnimSurfaceView surfaceView;
    private AnimController animController;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = (AnimSurfaceView) findViewById(R.id.surface_view);
        addButton = (Button) findViewById(R.id.button_add);
        animController = new AnimController(surfaceView);
        animController.setFps(60);
        initListener();
    }

    private void initListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animController.addScene("悲酥清风", new FlyTextSceneCreater());
            }
        });
    }
}
