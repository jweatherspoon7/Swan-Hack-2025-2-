package com.robin.swanhack25;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private Button signup;
    private Button login;

    private EditText username;
    private EditText password;

    private static final String URL_STRING_REQ = "http://coms-3090-011.class.las.iastate.edu:8080/users";

    private static final String POST_MAN = "https://5557881a-7d40-4d24-81a9-a46c5513d903.mock.pstmn.io/signup";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signup = findViewById(R.id.button);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.LoginButton);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                signupRequest();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }



    private void signupRequest() {
        String usernameInput = username.getText().toString();
        String passwordInput = password.getText().toString();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", usernameInput);
            requestBody.put("password", passwordInput);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_STRING_REQ,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley Response", response.toString());
                        Toast.makeText(SignUpActivity.this, response.toString(), Toast.LENGTH_LONG).show();

                        fetchUserIdAndCreateProfile(usernameInput);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(SignUpActivity.this, "Failed: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void createProfileForNewUser(int userId) {
        JSONObject profileJson = new JSONObject();
        try {
            profileJson.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Utils.BASE_URL + "/profiles",
                profileJson,
                response -> Log.d("ProfileCreate", "Profile created successfully"),
                error -> Log.e("ProfileCreate", "Failed to create profile: " + error)
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void fetchUserIdAndCreateProfile(String username) {
        String url = Utils.BASE_URL + "/users/username/" + username;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        int newUserId = response.getInt("id");
                        createProfileForNewUser(newUserId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SignUpActivity.this, "Failed to get new user ID", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Fetch User ID Error", error.toString());
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

}