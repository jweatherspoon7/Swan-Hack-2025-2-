package com.robin.swanhack25;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private TextView totalDonatedText;
    private LinearLayout chartContainer;
    private RadioGroup rangeGroup;

    private final List<Transaction> allTransactions = new ArrayList<>();
    private TimeRange currentRange = TimeRange.MONTH;

    private enum TimeRange {
        WEEK,
        MONTH,
        YEAR
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        totalDonatedText = findViewById(R.id.stats_total_donated_text);
        chartContainer = findViewById(R.id.stats_chart_container);
        rangeGroup = findViewById(R.id.stats_range_group);

        ImageButton profileButton = findViewById(R.id.stats_profile_button);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> showProfileOptions());
        }

        setupBottomNav();
        setupRangeSelector();

        fetchTransactions();
    }

    private void showProfileOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] options = {"Edit profile", "Log out"};
        builder.setItems(options, (DialogInterface dialog, int which) -> {
            if (which == 0) {
                Intent intent = new Intent(StatsActivity.this, EditProfileActivity.class);
                startActivity(intent);
            } else if (which == 1) {
                SessionManager.clear();
                Intent intent = new Intent(StatsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.stats_bottom_nav);
        if (bottomNavigationView == null) {
            return;
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_stats);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(StatsActivity.this, Home.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_stats) {
                return true;
            } else if (id == R.id.nav_give) {
                Intent intent = new Intent(StatsActivity.this, GiveActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_charity) {
                Toast.makeText(StatsActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void setupRangeSelector() {
        if (rangeGroup == null) {
            return;
        }

        rangeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.stats_range_week) {
                currentRange = TimeRange.WEEK;
            } else if (checkedId == R.id.stats_range_year) {
                currentRange = TimeRange.YEAR;
            } else {
                currentRange = TimeRange.MONTH;
            }
            updateChart();
        });

        // Default selection: month
        rangeGroup.check(R.id.stats_range_month);
    }

    private void fetchTransactions() {
        int userId = SessionManager.getKeyUserId();
        String url = Utils.BASE_URL + "/users/id/" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray transactionArray = response.getJSONArray("transactionSet");
                        allTransactions.clear();

                        double totalDonated = 0.0;

                        for (int i = 0; i < transactionArray.length(); i++) {
                            JSONObject obj = transactionArray.getJSONObject(i);
                            Transaction t = new Transaction(
                                    obj.getString("transactionName"),
                                    obj.getDouble("total"),
                                    obj.getDouble("amountDonated"),
                                    obj.getInt("month"),
                                    obj.getInt("day"),
                                    obj.getInt("year")
                            );
                            allTransactions.add(t);
                            totalDonated += t.amountDonated;
                        }

                        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.getDefault());
                        if (totalDonatedText != null) {
                            totalDonatedText.setText("Total donated: " + currency.format(totalDonated));
                        }

                        updateChart();
                    } catch (JSONException e) {
                        Log.e("StatsActivity", "Parsing transactions failed", e);
                        Toast.makeText(StatsActivity.this, "Failed to load stats", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String message = "Failed to load stats";
                    Log.e("StatsActivity", message, error);
                    Toast.makeText(StatsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void updateChart() {
        if (chartContainer == null) {
            return;
        }

        chartContainer.removeAllViews();

        if (allTransactions.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No donations in this period yet.");
            chartContainer.addView(emptyText);
            return;
        }

        Calendar now = Calendar.getInstance();
        Calendar cutoff = (Calendar) now.clone();

        switch (currentRange) {
            case WEEK:
                cutoff.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case YEAR:
                cutoff.add(Calendar.YEAR, -1);
                break;
            case MONTH:
            default:
                cutoff.add(Calendar.MONTH, -1);
                break;
        }

        Map<String, DayTotal> totalsByDay = new HashMap<>();

        for (Transaction t : allTransactions) {
            Calendar txDate = Calendar.getInstance();
            txDate.set(t.year, t.month - 1, t.day, 0, 0, 0);
            txDate.set(Calendar.MILLISECOND, 0);

            if (txDate.before(cutoff) || txDate.after(now)) {
                continue;
            }

            String key = t.year + "-" + t.month + "-" + t.day;
            DayTotal dayTotal = totalsByDay.get(key);
            if (dayTotal == null) {
                dayTotal = new DayTotal(txDate, 0.0);
                totalsByDay.put(key, dayTotal);
            }
            dayTotal.amount += t.amountDonated;
        }

        if (totalsByDay.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No donations in this period yet.");
            chartContainer.addView(emptyText);
            return;
        }

        List<DayTotal> dayTotals = new ArrayList<>(totalsByDay.values());
        Collections.sort(dayTotals, Comparator.comparing(o -> o.date));

        int maxBars;
        if (currentRange == TimeRange.WEEK) {
            maxBars = 7;
        } else if (currentRange == TimeRange.YEAR) {
            maxBars = 12;
        } else {
            maxBars = 30;
        }

        if (dayTotals.size() > maxBars) {
            dayTotals = dayTotals.subList(dayTotals.size() - maxBars, dayTotals.size());
        }

        double maxAmount = 0.0;
        for (DayTotal dt : dayTotals) {
            if (dt.amount > maxAmount) {
                maxAmount = dt.amount;
            }
        }

        if (maxAmount <= 0.0) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No donations in this period yet.");
            chartContainer.addView(emptyText);
            return;
        }

        float density = getResources().getDisplayMetrics().density;
        int maxHeightPx = (int) (180 * density);
        int barWidthMarginPx = (int) (4 * density);

        chartContainer.setOrientation(LinearLayout.HORIZONTAL);

        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] shortMonths = dfs.getShortMonths();

        for (DayTotal dt : dayTotals) {
            double amount = dt.amount;
            int barHeight = (int) (maxHeightPx * (amount / maxAmount));
            if (barHeight < (int) (8 * density)) {
                barHeight = (int) (8 * density);
            }

            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams columnParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1
            );
            columnParams.setMargins(barWidthMarginPx, 0, barWidthMarginPx, 0);
            column.setLayoutParams(columnParams);
            column.setGravity(android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL);

            View bar = new View(this);
            LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                    (int) (16 * density),
                    barHeight
            );
            barParams.bottomMargin = (int) (4 * density);
            bar.setLayoutParams(barParams);
            bar.setBackgroundColor(getColor(R.color.purple_500));

            TextView label = new TextView(this);
            String monthName = shortMonths[dt.date.get(Calendar.MONTH)];
            String labelText = monthName + " " + dt.date.get(Calendar.DAY_OF_MONTH);
            label.setText(labelText);
            label.setTextSize(10);
            label.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

            column.addView(bar);
            column.addView(label);

            chartContainer.addView(column);
        }
    }

    private static class Transaction {
        final String name;
        final double total;
        final double amountDonated;
        final int month;
        final int day;
        final int year;

        Transaction(String name, double total, double amountDonated, int month, int day, int year) {
            this.name = name;
            this.total = total;
            this.amountDonated = amountDonated;
            this.month = month;
            this.day = day;
            this.year = year;
        }
    }

    private static class DayTotal {
        final Calendar date;
        double amount;

        DayTotal(Calendar date, double amount) {
            this.date = date;
            this.amount = amount;
        }
    }
}
