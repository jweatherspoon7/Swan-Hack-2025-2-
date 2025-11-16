package com.robin.swanhack25;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharityActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private CharityAdapter adapter;
    private final List<Charity> charities = new ArrayList<>();
    private String lastQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity);

        searchView = findViewById(R.id.charity_search_view);
        recyclerView = findViewById(R.id.charity_recycler_view);

        ImageButton profileButton = findViewById(R.id.charity_profile_button);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> showProfileOptions());
        }

        adapter = new CharityAdapter(charities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupBottomNav();
        setupSearch();
    }

    private void showProfileOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] options = {"Edit profile", "Log out"};
        builder.setItems(options, (DialogInterface dialog, int which) -> {
            if (which == 0) {
                Intent intent = new Intent(CharityActivity.this, EditProfileActivity.class);
                startActivity(intent);
            } else if (which == 1) {
                SessionManager.clear();
                Intent intent = new Intent(CharityActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.charity_bottom_nav);
        if (bottomNavigationView == null) {
            return;
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_charity);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(CharityActivity.this, Home.class));
                return true;
            } else if (id == R.id.nav_stats) {
                startActivity(new Intent(CharityActivity.this, StatsActivity.class));
                return true;
            } else if (id == R.id.nav_give) {
                startActivity(new Intent(CharityActivity.this, GiveActivity.class));
                return true;
            } else if (id == R.id.nav_charity) {
                return true;
            }
            return false;
        });
    }

    private void setupSearch() {
        if (searchView == null) {
            return;
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String trimmed = newText != null ? newText.trim() : "";
                if (trimmed.equals(lastQuery)) {
                    return true;
                }
                lastQuery = trimmed;
                performSearch(trimmed);
                return true;
            }
        });
    }

    private void performSearch(String query) {
        if (TextUtils.isEmpty(query)) {
            charities.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        String encoded;
        try {
            encoded = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encoded = query;
        }

        String url = Utils.BASE_URL + "/everyorg/" + encoded;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    charities.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Charity c = new Charity(
                                    obj.optString("ein"),
                                    obj.optString("name"),
                                    obj.optString("description"),
                                    obj.optString("website"),
                                    obj.optString("logoUrl")
                            );
                            charities.add(c);
                        }
                    } catch (JSONException e) {
                        Log.e("CharityActivity", "Parsing charities failed", e);
                        Toast.makeText(CharityActivity.this, "Failed to load charities", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    String message = "Failed to load charities";
                    Log.e("CharityActivity", message, error);
                    Toast.makeText(CharityActivity.this, message, Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private static class Charity {
        final String ein;
        final String name;
        final String description;
        final String website;
        final String logoUrl;
        boolean added;

        Charity(String ein, String name, String description, String website, String logoUrl) {
            this.ein = ein;
            this.name = name;
            this.description = description;
            this.website = website;
            this.logoUrl = logoUrl;
            this.added = false;
        }
    }

    private class CharityAdapter extends RecyclerView.Adapter<CharityAdapter.CharityViewHolder> {

        private final List<Charity> localCharities;

        CharityAdapter(List<Charity> charities) {
            this.localCharities = charities;
        }

        @NonNull
        @Override
        public CharityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_search_charity, parent, false);
            return new CharityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CharityViewHolder holder, int position) {
            Charity charity = localCharities.get(position);

            holder.nameText.setText(charity.name);
            holder.websiteText.setText(TextUtils.isEmpty(charity.website) ? "No website listed" : charity.website);
            holder.descriptionText.setText(charity.description);

            if (!TextUtils.isEmpty(charity.logoUrl)) {
                Picasso.get()
                        .load(charity.logoUrl)
                        .placeholder(R.drawable.ic_charity)
                        .error(R.drawable.ic_charity)
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_charity);
            }

            holder.addButton.setEnabled(!charity.added);
            holder.addButton.setText(charity.added ? "Added" : "Add");

            holder.addButton.setOnClickListener(v -> {
                if (!charity.added) {
                    sendBookmark(charity, holder.getBindingAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return localCharities.size();
        }

        class CharityViewHolder extends RecyclerView.ViewHolder {
            final ImageView imageView;
            final TextView nameText;
            final TextView websiteText;
            final TextView descriptionText;
            final Button addButton;

            CharityViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.search_charity_image);
                nameText = itemView.findViewById(R.id.search_charity_name);
                websiteText = itemView.findViewById(R.id.search_charity_website);
                descriptionText = itemView.findViewById(R.id.search_charity_description);
                addButton = itemView.findViewById(R.id.search_charity_add_button);
            }
        }

        private void sendBookmark(Charity charity, int position) {
            int userId = SessionManager.getKeyUserId();
            String url = Utils.BASE_URL + "/bookmark";

            JSONObject body = new JSONObject();
            try {
                body.put("userId", userId);
                body.put("charityName", charity.name);
            } catch (JSONException e) {
                Toast.makeText(CharityActivity.this, "Failed to build request", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    url,
                    response -> {
                        Toast.makeText(CharityActivity.this, response, Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Toast.makeText(CharityActivity.this, "Failed to add charity", Toast.LENGTH_SHORT).show();
                    }
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

            charity.added = true;
            notifyItemChanged(position);

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }
    }
}
