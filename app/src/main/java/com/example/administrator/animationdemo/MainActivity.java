package com.example.administrator.animationdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.animlib.AnimController;
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
        initListener();
    }

    private void initListener() {
    }
}
