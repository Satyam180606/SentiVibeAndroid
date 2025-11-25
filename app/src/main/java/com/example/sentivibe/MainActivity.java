package com.example.sentivibe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sentivibe.data.RetrofitClient;
import com.example.sentivibe.data.SentimentModels.ScoreRequest;
import com.example.sentivibe.data.SentimentModels.SentimentResponse;
import com.example.sentivibe.model.AnalysisResult;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextInputEditText inputText;
    private MaterialButton btnAnalyze;
    private TextView txtResult;
    private ProgressBar progressBar;
    private LineChart lineChart;
    private Toolbar toolbar;
    private FirebaseFirestore db;

    private final List<Entry> chartEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setSupportActionBar(toolbar);
        setupChart();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        btnAnalyze.setOnClickListener(view -> {
            String text = inputText.getText().toString().trim();
            if (!text.isEmpty()) {
                analyzeText(text);
            } else {
                inputText.setError("Please enter some text to analyze.");
            }
        });
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        inputText = findViewById(R.id.inputText);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        txtResult = findViewById(R.id.txtResult);
        progressBar = findViewById(R.id.progressBar);
        lineChart = findViewById(R.id.lineChart);
    }

    private void setupChart() {
        // Chart setup remains the same
    }

    private void addPointToChart(float y) {
        // Chart update logic remains the same
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

                        // Save the result to Firestore
                        saveResultToFirestore(text, compound, polarity);
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

    private void saveResultToFirestore(String text, double vaderCompound, double textblobPolarity) {
        AnalysisResult result = new AnalysisResult(text, vaderCompound, textblobPolarity);

        db.collection("analysis_history")
                .add(result)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(MainActivity.this, "Result saved to history", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(MainActivity.this, "Error saving result", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
