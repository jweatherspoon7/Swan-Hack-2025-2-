package com.robin.swanhack25;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCharitiesActivity extends AppCompatActivity {

    private RecyclerView charitiesRecyclerView;
    private Button finishButton;
    private CharitiesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_charities);

        charitiesRecyclerView = findViewById(R.id.select_charities_recycler);
        finishButton = findViewById(R.id.select_charities_finish_btn);

        List<Charity> charities = new ArrayList<>();
        charities.add(new Charity("American Red Cross", R.drawable.americanredcross));
        charities.add(new Charity("Make-A-Wish", R.drawable.makeawish));
        charities.add(new Charity("Salvation Army", R.drawable.salvationarmy));
        charities.add(new Charity("St. Jude Hospital", R.drawable.stjude));

        adapter = new CharitiesAdapter(charities);
        charitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        charitiesRecyclerView.setAdapter(adapter);

        finishButton.setOnClickListener(v -> {
            Intent intent = new Intent(SelectCharitiesActivity.this, Home.class);
            startActivity(intent);
            finish();
        });
    }

    private static class Charity {
        final String name;
        final int imageResId;
        boolean added;

        Charity(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
            this.added = false;
        }
    }

    private class CharitiesAdapter extends RecyclerView.Adapter<CharitiesAdapter.CharityViewHolder> {

        private final List<Charity> charities;

        CharitiesAdapter(List<Charity> charities) {
            this.charities = charities;
        }

        @NonNull
        @Override
        public CharityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_charity, parent, false);
            return new CharityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CharityViewHolder holder, int position) {
            Charity charity = charities.get(position);
            holder.nameText.setText(charity.name);
            holder.imageView.setImageResource(charity.imageResId);

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
            return charities.size();
        }

        class CharityViewHolder extends RecyclerView.ViewHolder {
            final ImageView imageView;
            final TextView nameText;
            final Button addButton;

            CharityViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.charity_image);
                nameText = itemView.findViewById(R.id.charity_name);
                addButton = itemView.findViewById(R.id.charity_add_button);
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
                Toast.makeText(SelectCharitiesActivity.this, "Failed to build request", Toast.LENGTH_SHORT).show();
                return;
            }

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    url,
                    response -> {
                        Toast.makeText(SelectCharitiesActivity.this, response, Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Toast.makeText(SelectCharitiesActivity.this, "Failed to add charity", Toast.LENGTH_SHORT).show();
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

            // Disable further clicks immediately as requested
            charity.added = true;
            notifyItemChanged(position);

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }
    }
}
