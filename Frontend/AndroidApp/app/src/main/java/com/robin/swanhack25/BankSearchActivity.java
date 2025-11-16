package com.robin.swanhack25;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BankSearchActivity extends AppCompatActivity {

    private SearchView bankSearchView;
    private RecyclerView banksRecyclerView;
    private BankAdapter bankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_search);

        bankSearchView = findViewById(R.id.bank_search_view);
        banksRecyclerView = findViewById(R.id.banks_recycler_view);

        List<Bank> banks = createMockBanks();

        bankAdapter = new BankAdapter(banks, bank -> {
            Intent intent = new Intent(BankSearchActivity.this, Home.class);
            startActivity(intent);
            finish();
        });

        banksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        banksRecyclerView.setAdapter(bankAdapter);

        bankSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bankAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bankAdapter.filter(newText);
                return true;
            }
        });
    }

    private List<Bank> createMockBanks() {
        List<Bank> banks = new ArrayList<>();
        banks.add(new Bank("Chase", R.drawable.chase));
        banks.add(new Bank("Bank of America", R.drawable.boa));
        banks.add(new Bank("Wells Fargo", R.drawable.wellsfargo));
        banks.add(new Bank("Capital One", R.drawable.c1));

        return banks;
    }

    private static class Bank {
        final String name;
        final int logoResId;

        Bank(String name, int logoResId) {
            this.name = name;
            this.logoResId = logoResId;
        }
    }

    private interface OnBankConnectListener {
        void onConnect(Bank bank);
    }

    private static class BankAdapter extends RecyclerView.Adapter<BankAdapter.BankViewHolder> {

        private final List<Bank> originalBanks;
        private final List<Bank> displayedBanks;
        private final OnBankConnectListener listener;

        BankAdapter(List<Bank> banks, OnBankConnectListener listener) {
            this.originalBanks = new ArrayList<>(banks);
            this.displayedBanks = new ArrayList<>(banks);
            this.listener = listener;
        }

        @Override
        public BankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_bank, parent, false);
            return new BankViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BankViewHolder holder, int position) {
            Bank bank = displayedBanks.get(position);
            holder.bankNameText.setText(bank.name);
            holder.bankLogoImage.setImageResource(bank.logoResId);

            View.OnClickListener clickListener = v -> listener.onConnect(bank);

            holder.connectButton.setOnClickListener(clickListener);
            holder.itemView.setOnClickListener(clickListener);
        }

        @Override
        public int getItemCount() {
            return displayedBanks.size();
        }

        void filter(String query) {
            displayedBanks.clear();
            if (query == null || query.trim().isEmpty()) {
                displayedBanks.addAll(originalBanks);
            } else {
                String lowerQuery = query.toLowerCase();
                for (Bank bank : originalBanks) {
                    if (bank.name.toLowerCase().contains(lowerQuery)) {
                        displayedBanks.add(bank);
                    }
                }
            }
            notifyDataSetChanged();
        }

        static class BankViewHolder extends RecyclerView.ViewHolder {
            final ImageView bankLogoImage;
            final TextView bankNameText;
            final Button connectButton;

            BankViewHolder(View itemView) {
                super(itemView);
                bankLogoImage = itemView.findViewById(R.id.bank_logo_image);
                bankNameText = itemView.findViewById(R.id.bank_name_text);
                connectButton = itemView.findViewById(R.id.bank_connect_button);
            }
        }
    }
}

