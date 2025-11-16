package com.robin.swanhack25;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        emailEditText = findViewById(R.id.edit_profile_email);
        passwordEditText = findViewById(R.id.edit_profile_password);
        saveButton = findViewById(R.id.edit_profile_save_button);

        String currentEmail = SessionManager.getKeyUsername();
        if (!TextUtils.isEmpty(currentEmail)) {
            emailEditText.setText(currentEmail);
        }

        saveButton.setOnClickListener(v -> sendUpdate());
    }

    private void sendUpdate() {
        int userId = SessionManager.getKeyUserId();
        String url = Utils.BASE_URL + "/users/" + userId;

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        JSONObject body = new JSONObject();
        try {
            if (!TextUtils.isEmpty(email)) {
                body.put("email", email);
            }
            if (!TextUtils.isEmpty(password)) {
                body.put("password", password);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Failed to build request", Toast.LENGTH_SHORT).show();
            return;
        }

        if (body.length() == 0) {
            Toast.makeText(this, "Enter email or password to update", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.PUT,
                url,
                response -> {
                    if (!TextUtils.isEmpty(email)) {
                        SessionManager.setKeyUsername(email);
                    }
                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                return body.toString().getBytes();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}

