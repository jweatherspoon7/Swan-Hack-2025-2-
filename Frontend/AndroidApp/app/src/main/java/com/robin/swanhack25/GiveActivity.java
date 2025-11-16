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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiveActivity extends AppCompatActivity {

    private PieChart pieChart;
    private android.widget.LinearLayout slicesContainer;
    private TextView totalPercentText;
    private Button saveButton;

    private final List<Bookmark> bookmarks = new ArrayList<>();

    private final List<Float> values = new ArrayList<>();
    private final List<TextView> labels = new ArrayList<>();
    private final List<SeekBar> seekBars = new ArrayList<>();
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give);

        pieChart = findViewById(R.id.give_pie_chart);
        slicesContainer = findViewById(R.id.give_slices_container);
        totalPercentText = findViewById(R.id.give_total_percent_text);
        saveButton = findViewById(R.id.give_save_button);

        ImageButton profileButton = findViewById(R.id.give_profile_button);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> showProfileOptions());
        }

        setupBottomNav();

        saveButton.setOnClickListener(v -> saveChanges());

        loadBookmarks();
    }

    private void showProfileOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] options = {"Edit profile", "Log out"};
        builder.setItems(options, (DialogInterface dialog, int which) -> {
            if (which == 0) {
                Intent intent = new Intent(GiveActivity.this, EditProfileActivity.class);
                startActivity(intent);
            } else if (which == 1) {
                SessionManager.clear();
                Intent intent = new Intent(GiveActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
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
                startActivity(new android.content.Intent(GiveActivity.this, CharityActivity.class));
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
                    initializeSlices();
                    updateChart();
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

    private void initializeSlices() {
        values.clear();
        labels.clear();
        seekBars.clear();
        if (slicesContainer != null) {
            slicesContainer.removeAllViews();
        }

        if (bookmarks.isEmpty() || slicesContainer == null) {
            return;
        }

        float total = 0f;
        for (Bookmark bookmark : bookmarks) {
            total += bookmark.currentPercent;
        }

        if (total <= 0f) {
            float defaultValue = 100f / bookmarks.size();
            for (Bookmark bookmark : bookmarks) {
                bookmark.currentPercent = Math.round(defaultValue);
                values.add(defaultValue);
            }
        } else {
            for (Bookmark bookmark : bookmarks) {
                float v = (bookmark.currentPercent / total) * 100f;
                values.add(v);
            }
        }

        for (int i = 0; i < bookmarks.size(); i++) {
            Bookmark bookmark = bookmarks.get(i);
            float value = values.get(i);

            TextView tv = new TextView(this);
            tv.setText(bookmark.charityName + ": " + String.format("%.1f", value) + "%");
            tv.setTextSize(16);
            slicesContainer.addView(tv);
            labels.add(tv);

            SeekBar sb = new SeekBar(this);
            sb.setMax(100);
            sb.setProgress(Math.round(value));
            slicesContainer.addView(sb);
            seekBars.add(sb);

            final int index = i;
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser || isUpdating) {
                        return;
                    }
                    adjustSlices(index, (float) progress);
                    updateChart();
                    updateTotalPercent();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }
    }

    private void adjustSlices(int changedIndex, float newValue) {
        isUpdating = true;

        values.set(changedIndex, newValue);

        float remainingTotal = 100f - newValue;

        float otherSum = 0f;
        for (int i = 0; i < values.size(); i++) {
            if (i != changedIndex) {
                otherSum += values.get(i);
            }
        }

        for (int i = 0; i < values.size(); i++) {
            if (i == changedIndex) {
                continue;
            }

            if (otherSum == 0f) {
                values.set(i, remainingTotal / (values.size() - 1));
            } else {
                float scaled = (values.get(i) / otherSum) * remainingTotal;
                values.set(i, scaled);
            }
        }

        for (int i = 0; i < seekBars.size(); i++) {
            seekBars.get(i).setProgress(Math.round(values.get(i)));
        }

        for (int i = 0; i < bookmarks.size(); i++) {
            bookmarks.get(i).currentPercent = Math.round(values.get(i));
        }

        isUpdating = false;
    }

    private void updateChart() {
        if (pieChart == null || bookmarks.isEmpty()) {
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            float v = values.get(i);
            Bookmark bookmark = bookmarks.get(i);
            entries.add(new PieEntry(v, bookmark.charityName));
            if (i < labels.size()) {
                labels.get(i).setText(bookmark.charityName + ": " + String.format("%.1f", v) + "%");
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void sendUpdate(Bookmark bookmark) {
        String url = Utils.BASE_URL + "/bookmark/" + bookmark.id;

        JSONObject body = new JSONObject();
        try {
            body.put("precentContribution", bookmark.currentPercent);
            String contribution = String.format("%.1f", bookmark.totalContribution);
            
            body.put("totalContribution", contribution);
            body.put("charityName", bookmark.charityName);
            Log.d("put request", "body: " + body.toString());

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
}
