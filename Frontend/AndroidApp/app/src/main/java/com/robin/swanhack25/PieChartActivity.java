package com.robin.swanhack25;


import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class PieChartActivity extends AppCompatActivity {

    private PieChart pieChart;
    private LinearLayout container;

    private ArrayList<Float> values = new ArrayList<>();
    private ArrayList<TextView> labels = new ArrayList<>();
    private ArrayList<SeekBar> seekBars = new ArrayList<>();
    private boolean isUpdating = false;  // prevent infinite seek bar updates

    private Button btnEvenOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);

        pieChart = findViewById(R.id.pieChart);
        container = findViewById(R.id.container);

        int numSlices = 8; // Change or pass via Intent
        initializeSlices(numSlices);
        updateChart();

        btnEvenOut = findViewById(R.id.btnEvenOut);

        btnEvenOut.setOnClickListener(v -> {
            evenOutSlices();
        });

    }

    private void initializeSlices(int count) {
        values.clear();
        labels.clear();
        seekBars.clear();
        container.removeAllViews();

        float defaultValue = 100f / count;

        for (int i = 0; i < count; i++) {
            values.add(defaultValue);

            // label
            TextView tv = new TextView(this);
            tv.setText("Slice " + (i + 1) + ": " + defaultValue + "%");
            tv.setTextSize(16);
            container.addView(tv);
            labels.add(tv);

            // seek bar
            SeekBar sb = new SeekBar(this);
            sb.setMax(100);
            sb.setProgress((int) defaultValue);
            container.addView(sb);
            seekBars.add(sb);

            final int index = i;

            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser || isUpdating) return;
                    adjustSlices(index, (float) progress);
                    updateChart();
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
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
            if (i == changedIndex) continue;

            if (otherSum == 0f) {
                values.set(i, remainingTotal / (values.size() - 1));
            } else {
                float scaled = (values.get(i) / otherSum) * remainingTotal;
                values.set(i, scaled);
            }
        }

        // Update SeekBars
        for (int i = 0; i < seekBars.size(); i++) {
            seekBars.get(i).setProgress(Math.round(values.get(i)));
        }

        isUpdating = false;
    }

    private void updateChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < values.size(); i++) {
            float v = values.get(i);
            entries.add(new PieEntry(v, "Slice " + (i + 1)));
            labels.get(i).setText("Slice " + (i + 1) + ": " + String.format("%.1f", v) + "%");
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void evenOutSlices() {
        isUpdating = true;

        float equalValue = 100f / values.size();

        for (int i = 0; i < values.size(); i++) {
            values.set(i, equalValue);
            seekBars.get(i).setProgress(Math.round(equalValue));
            labels.get(i).setText("Slice " + (i + 1) + ": " + String.format("%.1f", equalValue) + "%");
        }

        isUpdating = false;

        updateChart();
    }

}