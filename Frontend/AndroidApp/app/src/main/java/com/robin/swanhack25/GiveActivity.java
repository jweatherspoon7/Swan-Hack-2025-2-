package com.robin.swanhack25;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiveActivity extends AppCompatActivity {

    private RecyclerView bookmarksRecyclerView;
    private TextView totalPercentText;
    private Button saveButton;

    private final List<Bookmark> bookmarks = new ArrayList<>();
    private BookmarksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give);

        bookmarksRecyclerView = findViewById(R.id.give_recycler_view);
        totalPercentText = findViewById(R.id.give_total_percent_text);
        saveButton = findViewById(R.id.give_save_button);

        adapter = new BookmarksAdapter(bookmarks);
        bookmarksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookmarksRecyclerView.setAdapter(adapter);

        setupBottomNav();

        saveButton.setOnClickListener(v -> saveChanges());

        loadBookmarks();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.give_bottom_nav);
        if (bottomNavigationView == null) {
            return;
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_give);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new android.content.Intent(GiveActivity.this, Home.class));
                return true;
            } else if (id == R.id.nav_stats) {
                startActivity(new android.content.Intent(GiveActivity.this, StatsActivity.class));
                return true;
            } else if (id == R.id.nav_give) {
                return true;
            } else if (id == R.id.nav_charity) {
                Toast.makeText(GiveActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadBookmarks() {
        int userId = SessionManager.getKeyUserId();
        String url = Utils.BASE_URL + "/bookmark/user/" + userId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    bookmarks.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String charityName = obj.getString("charityName");
                            // backend field is spelled "precentContribution"
                            int percent = 0;
                            if (obj.has("precentContribution") && !obj.isNull("precentContribution")) {
                                percent = obj.getInt("precentContribution");
                            }
                            double totalContribution = obj.optDouble("totalContribution", 0.0);

                            Bookmark bookmark = new Bookmark(id, charityName, percent, totalContribution);
                            bookmarks.add(bookmark);
                        }
                    } catch (JSONException e) {
                        Log.e("GiveActivity", "Parsing bookmarks failed", e);
                        Toast.makeText(GiveActivity.this, "Failed to load bookmarks", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                    updateTotalPercent();
                },
                error -> {
                    String message = "Failed to load bookmarks";
                    Log.e("GiveActivity", message, error);
                    Toast.makeText(GiveActivity.this, message, Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void updateTotalPercent() {
        int total = 0;
        for (Bookmark bookmark : bookmarks) {
            total += bookmark.currentPercent;
        }

        if (totalPercentText != null) {
            totalPercentText.setText("Total allocation: " + total + "%");
        }
    }

    private void saveChanges() {
        if (bookmarks.isEmpty()) {
            Toast.makeText(this, "No charities to update", Toast.LENGTH_SHORT).show();
            return;
        }

        int total = 0;
        for (Bookmark bookmark : bookmarks) {
            total += bookmark.currentPercent;
        }

        if (total != 100) {
            Toast.makeText(this, "Total allocation must be 100%", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Bookmark bookmark : bookmarks) {
            if (bookmark.currentPercent != bookmark.originalPercent) {
                sendUpdate(bookmark);
            }
        }

        Toast.makeText(this, "Saving changes...", Toast.LENGTH_SHORT).show();
    }

    private void sendUpdate(Bookmark bookmark) {
        String url = Utils.BASE_URL + "/bookmark/" + bookmark.id;

        JSONObject body = new JSONObject();
        try {
            body.put("precentContribution", bookmark.currentPercent);
            body.put("totalContribution", bookmark.totalContribution);
            body.put("charityId", bookmark.charityName);
        } catch (JSONException e) {
            Toast.makeText(this, "Failed to build update", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(
                Request.Method.PUT,
                url,
                response -> {
                    if (!TextUtils.isEmpty(response)) {
                        Toast.makeText(GiveActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                    bookmark.originalPercent = bookmark.currentPercent;
                },
                error -> Toast.makeText(GiveActivity.this, "Failed to update " + bookmark.charityName, Toast.LENGTH_SHORT).show()
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

    private static class Bookmark {
        final int id;
        final String charityName;
        final double totalContribution;
        int originalPercent;
        int currentPercent;

        Bookmark(int id, String charityName, int percent, double totalContribution) {
            this.id = id;
            this.charityName = charityName;
            this.originalPercent = percent;
            this.currentPercent = percent;
            this.totalContribution = totalContribution;
        }
    }

    private class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarkViewHolder> {

        private final List<Bookmark> localBookmarks;

        BookmarksAdapter(List<Bookmark> bookmarks) {
            this.localBookmarks = bookmarks;
        }

        @NonNull
        @Override
        public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_bookmark, parent, false);
            return new BookmarkViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
            Bookmark bookmark = localBookmarks.get(position);
            holder.nameText.setText(bookmark.charityName);
            holder.totalContributionText.setText("Total contributed: $" + String.format("%.2f", bookmark.totalContribution));

            holder.percentText.setText(bookmark.currentPercent + "%");
            holder.seekBar.setOnSeekBarChangeListener(null);
            holder.seekBar.setProgress(bookmark.currentPercent);

            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    bookmark.currentPercent = progress;
                    holder.percentText.setText(progress + "%");
                    updateTotalPercent();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }

        @Override
        public int getItemCount() {
            return localBookmarks.size();
        }

        class BookmarkViewHolder extends RecyclerView.ViewHolder {
            final TextView nameText;
            final TextView totalContributionText;
            final TextView percentText;
            final SeekBar seekBar;

            BookmarkViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.bookmark_charity_name);
                totalContributionText = itemView.findViewById(R.id.bookmark_total_contribution);
                percentText = itemView.findViewById(R.id.bookmark_percent_text);
                seekBar = itemView.findViewById(R.id.bookmark_percent_seekbar);
            }
        }
    }
}

