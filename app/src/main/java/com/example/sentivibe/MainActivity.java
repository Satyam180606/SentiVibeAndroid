package com.example.sentivibe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sentivibe.data.RetrofitClient;
import com.example.sentivibe.data.SentimentModels.ScoreRequest;
import com.example.sentivibe.data.SentimentModels.SentimentResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private TextView txtResult;
    private LineChart lineChart;

    private final List<Entry> entries = new ArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputText = findViewById(R.id.inputText);
        Button btnAnalyze = findViewById(R.id.btnAnalyze);
        txtResult = findViewById(R.id.txtResult);
        lineChart = findViewById(R.id.lineChart);

        setupChart();

        btnAnalyze.setOnClickListener(view -> {
            String text = inputText.getText().toString().trim();
            if (!text.isEmpty()) analyzeText(text);
        });
    }

    private void setupChart() {
        LineDataSet ds = new LineDataSet(entries, "Polarity");
        ds.setDrawCircles(true);
        LineData ld = new LineData(ds);
        lineChart.setData(ld);
        lineChart.getDescription().setEnabled(false);
    }

    private void addPoint(float y) {
        int x = counter.incrementAndGet();
        entries.add(new Entry(x, y));
        lineChart.getData().notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void analyzeText(String text) {
        txtResult.setText(R.string.analyzing);
        ScoreRequest req = new ScoreRequest(text);
        Call<SentimentResponse> call = RetrofitClient.getApi().score(req);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SentimentResponse> call, @NonNull Response<SentimentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SentimentResponse body = response.body();
                    if (body.vader != null && body.textblob != null) {
                        double compound = body.vader.compound;
                        double tb = body.textblob.polarity;
                        String res = String.format(Locale.US, "VADER: %.3f | TextBlob: %.3f", compound, tb);
                        txtResult.setText(res);
                        addPoint((float) compound);
                    } else {
                        txtResult.setText("Incomplete server response");
                    }
                } else {
                    txtResult.setText(getString(R.string.server_error, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SentimentResponse> call, @NonNull Throwable t) {
                txtResult.setText(getString(R.string.network_error, t.getMessage()));
            }
        });
    }
}
