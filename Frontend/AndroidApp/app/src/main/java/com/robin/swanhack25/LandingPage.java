package com.robin.swanhack25;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
        Intent intent = new Intent(LandingPage.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void openSignUpActivity() {
        if (signup == null) {
            // Fallback if for some reason the button is not initialized
            Intent intent = new Intent(LandingPage.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return;
        }

        signup.setEnabled(false);
        Animation zoom = AnimationUtils.loadAnimation(this, R.anim.zoom_in_button);
        zoom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(LandingPage.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                signup.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        signup.startAnimation(zoom);
    }
}
