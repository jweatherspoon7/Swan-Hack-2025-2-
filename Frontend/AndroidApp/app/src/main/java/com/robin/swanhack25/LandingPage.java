package com.robin.swanhack25;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class LandingPage extends AppCompatActivity {

    private Button signup, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        signup = (Button) findViewById(R.id.button2);
        login = findViewById(R.id.button3);

        signup.setOnClickListener(v -> openSignUpActivity());
        login.setOnClickListener(v -> openLoginActivity());


    }

    private void openLoginActivity() {
        startActivity(new Intent(LandingPage.this, LoginActivity.class));
    }

    private void openSignUpActivity() {
        startActivity(new Intent(LandingPage.this, SignUpActivity.class));
    }
}
