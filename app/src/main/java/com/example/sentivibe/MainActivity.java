package com.example.sentivibe;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sentivibe.data.RetrofitClient;
import com.example.sentivibe.data.SentimentModels.ScoreRequest;
import com.example.sentivibe.data.SentimentModels.SentimentResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputText;
    private Button btnAnalyze;
    private TextView txtResult;
    private ProgressBar progressBar;
    private LineChart lineChart;

    private final List<Entry> entries = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.inputText);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        txtResult = findViewById(R.id.txtResult);
        progressBar = findViewById(R.id.progressBar);
        lineChart = findViewById(R.id.lineChart);

        setupChart();

        btnAnalyze.setOnClickListener(view -> {
            String text = inputText.getText().toString().trim();
            if (!text.isEmpty()) {
                analyzeText(text);
            }
        });
    }

    private void setupChart() {
        LineDataSet dataSet = new LineDataSet(entries, "VADER Compound Score");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(true);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(-1f);
        lineChart.getAxisLeft().setAxisMaximum(1f);
        lineChart.invalidate(); // refresh
    }

    private void addPointToChart(float y) {
        LineData data = lineChart.getData();
        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null) {
                set = new LineDataSet(null, "VADER Compound Score");
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), y), 0);
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(10);
            lineChart.moveViewToX(data.getEntryCount());
        }
    }

    private void analyzeText(String text) {
        showLoading(true);
        ScoreRequest request = new ScoreRequest(text);
        Call<SentimentResponse> call = RetrofitClient.getApi().score(request);

        call.enqueue(new Callback<SentimentResponse>() {
            @Override
            public void onResponse(@NonNull Call<SentimentResponse> call, @NonNull Response<SentimentResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    SentimentResponse body = response.body();
                    if (body.vader != null && body.textblob != null) {
                        double compound = body.vader.compound;
                        double polarity = body.textblob.polarity;
                        String resultText = String.format(Locale.US, "VADER Compound: %.3f\nTextBlob Polarity: %.3f", compound, polarity);
                        txtResult.setText(resultText);
                        addPointToChart((float) compound);
                    } else {
                        txtResult.setText("Incomplete data from server.");
                    }
                } else {
                    txtResult.setText(getString(R.string.server_error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SentimentResponse> call, @NonNull Throwable t) {
                showLoading(false);
                txtResult.setText(getString(R.string.network_error, t.getMessage()));
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnAnalyze.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnAnalyze.setEnabled(true);
        }
    }
}
