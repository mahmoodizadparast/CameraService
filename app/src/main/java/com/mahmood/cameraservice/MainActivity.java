package com.mahmood.cameraservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TakePicService.mcontext = MainActivity.this;
        Intent intent = new Intent(this, TakePicService.class);
        startService(intent);


    }
}
