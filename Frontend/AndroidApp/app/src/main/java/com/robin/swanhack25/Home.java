package com.robin.swanhack25;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private RecyclerView transactionsRecyclerView;
    private TransactionsAdapter transactionsAdapter;
    private TextView monthlyDonatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView welcomeText = findViewById(R.id.home_welcome_text);
        monthlyDonatedText = findViewById(R.id.home_monthly_donated_text);
        String username = SessionManager.getKeyUsername();
        if (username != null && !username.isEmpty()) {
            welcomeText.setText("Welcome, " + username);
        }

        ImageButton profileButton = findViewById(R.id.home_profile_button);
        if (profileButton != null) {
            profileButton.setOnClickListener(v -> showProfileOptions());
        }

        transactionsRecyclerView = findViewById(R.id.home_recycler_view);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsAdapter = new TransactionsAdapter();
        transactionsRecyclerView.setAdapter(transactionsAdapter);

        setupBottomNav();

        fetchTransactions();
    }

    private void showProfileOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CharSequence[] options = {"Edit profile", "Log out"};
        builder.setItems(options, (DialogInterface dialog, int which) -> {
            if (which == 0) {
                Intent intent = new Intent(Home.this, EditProfileActivity.class);
                startActivity(intent);
            } else if (which == 1) {
                SessionManager.clear();
                Intent intent = new Intent(Home.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.home_bottom_nav);
        if (bottomNavigationView == null) {
            return;
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_stats) {
                Intent intent = new Intent(Home.this, StatsActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_give) {
                Intent intent = new Intent(Home.this, GiveActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_charity) {
                Intent intent = new Intent(Home.this, CharityActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
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
                        List<Transaction> transactions = new ArrayList<>();

                        Calendar calendar = Calendar.getInstance();
                        int currentMonth = calendar.get(Calendar.MONTH) + 1;
                        int currentYear = calendar.get(Calendar.YEAR);
                        double monthlyDonated = 0.0;

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
                            transactions.add(t);

                             if (t.month == currentMonth && t.year == currentYear) {
                                 monthlyDonated += t.amountDonated;
                             }
                        }

                        transactionsAdapter.setTransactions(transactions);

                        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.getDefault());
                        if (monthlyDonatedText != null) {
                            monthlyDonatedText.setText("Change donated this month: " + currency.format(monthlyDonated));
                        }
                    } catch (JSONException e) {
                        Log.e("Home", "Parsing transactions failed", e);
                        Toast.makeText(Home.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String message = "Failed to load transactions";
                    Log.e("Home", message, error);
                    Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
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

        String getFormattedDate() {
            String monthName = new DateFormatSymbols().getShortMonths()[Math.max(0, month - 1)];
            return monthName + " " + day + ", " + year;
        }

        String getFormattedAmounts() {
            NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.getDefault());
            return "Change Donated: +" + currency.format(amountDonated) +
                    "\nTransaction Total: " + currency.format(total);
        }
    }

    private static class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {

        private final List<Transaction> transactions = new ArrayList<>();

        void setTransactions(List<Transaction> newTransactions) {
            transactions.clear();
            transactions.addAll(newTransactions);
            notifyDataSetChanged();
        }

        @Override
        public TransactionViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TransactionViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            holder.nameText.setText(transaction.name);
            holder.dateText.setText(transaction.getFormattedDate());
            holder.amountsText.setText(transaction.getFormattedAmounts());
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        static class TransactionViewHolder extends RecyclerView.ViewHolder {
            final TextView nameText;
            final TextView dateText;
            final TextView amountsText;

            TransactionViewHolder(android.view.View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.transaction_name);
                dateText = itemView.findViewById(R.id.transaction_date);
                amountsText = itemView.findViewById(R.id.transaction_amounts);
            }
        }
    }
}
