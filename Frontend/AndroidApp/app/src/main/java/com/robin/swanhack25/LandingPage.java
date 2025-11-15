package com.robin.swanhack25;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class LandingPage extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        button = (Button) findViewById(R.id.button2);

        button.setOnClickListener(v -> openSignUpActivity());


    }

    private void openSignUpActivity() {
        startActivity(new Intent(LandingPage.this, SignUpActivity.class));
    }
}
