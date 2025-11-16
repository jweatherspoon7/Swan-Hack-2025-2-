package com.robin.swanhack25;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BankConnection extends AppCompatActivity {

    private Button autoConnect, manualConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        autoConnect = findViewById(R.id.autoConnect);
        manualConnect = findViewById(R.id.manualConnect);

        autoConnect.setOnClickListener(v -> {
            Intent intent = new Intent(BankConnection.this, BankSearchActivity.class);
            startActivity(intent);
        });

        manualConnect.setOnClickListener(v-> {


                Intent intent = new Intent(BankConnection.this, ManualConnection.class);
                startActivity(intent);

        });
    }



    }



