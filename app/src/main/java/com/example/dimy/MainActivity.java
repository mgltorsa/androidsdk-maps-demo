package com.example.dimy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dimy.view.MapsActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.mapButton);
        button.setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.mapButton: {
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);

                break;
            }
        }


    }
}
