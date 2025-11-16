package com.robin.swanhack25;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.plaid.link.FastOpenPlaidLink;
import com.plaid.link.Plaid;
import com.plaid.link.PlaidHandler;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkExit;
import com.plaid.link.result.LinkResult;
import com.plaid.link.result.LinkSuccess;

import org.json.JSONException;
import org.json.JSONObject;

public class BankConnection extends AppCompatActivity {

    private Button autoConnect, manualConnect;
    private PlaidHandler plaidHandler;

    private final ActivityResultLauncher<PlaidHandler> linkAccountToPlaid =
            registerForActivityResult(new FastOpenPlaidLink(), result -> {
                if (result instanceof LinkSuccess) {
                    LinkSuccess success = (LinkSuccess) result;
                    String publicToken = success.getPublicToken();
                    sendPublicTokenToBackend(publicToken);
                } else if (result instanceof LinkExit) {
                    Toast.makeText(this, "Bank connection canceled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        autoConnect = findViewById(R.id.autoConnect);
        manualConnect = findViewById(R.id.manualConnect);

        autoConnect.setOnClickListener(v -> startPlaidLinkFlow());

        manualConnect.setOnClickListener(v -> {
            Intent intent = new Intent(BankConnection.this, ManualConnection.class);
            startActivity(intent);
        });
    }

    private void startPlaidLinkFlow() {
        int userId = SessionManager.getKeyUserId();
        String url = Utils.BASE_URL + "/plaid/create_link_token";

        JSONObject body = new JSONObject();
        try {
            body.put("userId", userId);
        } catch (JSONException e) {
            Toast.makeText(this, "Failed to start bank connection", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    String linkToken = response.optString("link_token", null);
                    if (linkToken != null) {
                        openPlaidLink(linkToken);
                    } else {
                        Toast.makeText(this, "Failed to get link token", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to start bank connection", Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void openPlaidLink(String linkToken) {
        LinkTokenConfiguration linkTokenConfiguration = new LinkTokenConfiguration.Builder()
                .token(linkToken)
                .build();

        plaidHandler = Plaid.create(getApplication(), linkTokenConfiguration);
        linkAccountToPlaid.launch(plaidHandler);
    }

    private void sendPublicTokenToBackend(String publicToken) {
        int userId = SessionManager.getKeyUserId();
        String url = Utils.BASE_URL + "/plaid/exchange_public_token";

        JSONObject body = new JSONObject();
        try {
            body.put("userId", userId);
            body.put("public_token", publicToken);
        } catch (JSONException e) {
            Toast.makeText(this, "Failed to link bank", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    Toast.makeText(this, "Bank linked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BankConnection.this, SelectCharitiesActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(this, "Failed to link bank", Toast.LENGTH_SHORT).show()
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}



