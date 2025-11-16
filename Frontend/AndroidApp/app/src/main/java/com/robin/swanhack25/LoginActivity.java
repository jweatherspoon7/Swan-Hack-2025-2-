package com.robin.swanhack25;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import com.robin.swanhack25.SignUpActivity;
import com.robin.swanhack25.VolleySingleton;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    private static final String USERNAME_URL = "http://coms-3090-011.class.las.iastate.edu:8080/users/username/";
    private static final String EMAIL_URL = "http://coms-3090-011.class.las.iastate.edu:8080/users/email/";

    private int loggedInUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        signupButton = findViewById(R.id.login_signup_btn);

        loginButton.setOnClickListener(v -> {
            String usernameOrEmail = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username/email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginRequest(usernameOrEmail, password);
            }
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void loginRequest(String usernameOrEmailInput, String passwordInput) {
        String url = EMAIL_URL + usernameOrEmailInput;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        String backendPassword = response.getString("password");
                        int userId = response.getInt("id");

                        if (backendPassword.equals(passwordInput)) {
                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            loggedInUserId = userId;
                            SessionManager.setKeyUserId(userId);
                            SessionManager.setKeyUsername(response.getString("email"));


                            goToProfile();

                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String message = "Login failed: ";
                    if (error.networkResponse != null) {
                        message += "HTTP " + error.networkResponse.statusCode;
                    } else if (error.getMessage() != null) {
                        message += error.getMessage();
                    } else {
                        message += error.toString();
                    }
                    Log.e("Volley Error", message, error);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }




    private void goToProfile() {
        Intent intent = new Intent(LoginActivity.this, BankConnection.class);
        startActivity(intent);
        finish();
    }
}
