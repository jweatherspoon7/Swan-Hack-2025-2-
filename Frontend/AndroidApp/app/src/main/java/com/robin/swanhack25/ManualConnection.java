package com.robin.swanhack25;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ManualConnection extends AppCompatActivity {

    private Button connect;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_manualbank);

        connect = findViewById(R.id.connect);

        connect.setOnClickListener(v -> {
            Intent intent = new Intent(ManualConnection.this, SelectCharitiesActivity.class);
            startActivity(intent);
        });
    }



}
