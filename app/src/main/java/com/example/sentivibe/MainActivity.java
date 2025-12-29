package com.example.sentivibe;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.sentivibe.data.ApiService;
import com.example.sentivibe.data.FetchRequest;
import com.example.sentivibe.data.FetchResponse;
import com.example.sentivibe.data.RetrofitClient;
import com.example.sentivibe.viewmodel.MainViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final List<Entry> chartEntries = new ArrayList<>();
    private DrawerLayout drawerLayout;
    // UI Components
    private TextInputEditText linkInput;
    private MaterialButton btnFetchAndAnalyze;
    private TextView txtResult;
    private ProgressBar progressBar;
    private TextView progressText;
    private LineChart lineChart;
    private Toolbar toolbar;
    private TextView txtLlmResult;
    private MaterialCardView postCard;
    private MaterialCardView analysisCard;
    private TextView txtPost;
    private TextView txtAuthor;
    // ViewModel
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        initializeViews();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // INNOVATION FEATURE: Observe status messages from ViewModel
        mainViewModel.getStatusMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        setupChart();
        setupClickListeners();
        updateNavHeader();
    }

    private void initializeViews() {
        linkInput = findViewById(R.id.linkInput);
        btnFetchAndAnalyze = findViewById(R.id.btnFetchAndAnalyze);
        txtResult = findViewById(R.id.txtResult);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        lineChart = findViewById(R.id.lineChart);
        txtLlmResult = findViewById(R.id.txt_llm_result);
        postCard = findViewById(R.id.postCard);
        analysisCard = findViewById(R.id.analysisCard);
        txtPost = findViewById(R.id.txtPost);
        txtAuthor = findViewById(R.id.txtAuthor);
    }

    private void setupClickListeners() {
        btnFetchAndAnalyze.setOnClickListener(view -> {
            String link = linkInput.getText().toString().trim();
            if (!link.isEmpty()) {
                // Hide previous results
                postCard.setVisibility(View.GONE);
                analysisCard.setVisibility(View.GONE);
                fetchDataAndAnalyze(link);
            } else {
                linkInput.setError("Please enter a link to analyze.");
            }
        });
    }

    private void fetchDataAndAnalyze(String link) {
        showLoading(true, "Fetching content from link...");

        ApiService apiService = RetrofitClient.getApi();
        apiService.fetch(new FetchRequest(link)).enqueue(new Callback<FetchResponse>() {
            @Override
            public void onResponse(Call<FetchResponse> call, Response<FetchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String postText = response.body().getContent();
                    String authorText = response.body().getAuthor();

                    // Display the fetched post
                    txtPost.setText(postText);
                    txtAuthor.setText(authorText);

                    // Now, analyze the fetched text
                    analyzeText(postText);
                } else {
                    showLoading(false, null);
                    Toast.makeText(MainActivity.this, "Error: Failed to fetch content from the link.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<FetchResponse> call, Throwable t) {
                showLoading(false, null);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void analyzeText(String text) {
        showLoading(true, "Analyzing text...");
        mainViewModel.analyzeText(text).observe(this, response -> {
            showLoading(false, null);

            if (response != null && response.vader != null && response.textblob != null && response.llmAnalysis != null) {
                // On success, update the UI
                postCard.setVisibility(View.VISIBLE);
                analysisCard.setVisibility(View.VISIBLE);
                double compound = response.vader.compound;
                double polarity = response.textblob.polarity;
                String resultText = String.format(Locale.US, "VADER Compound: %.3f\nTextBlob Polarity: %.3f", compound, polarity);
                txtResult.setText(resultText);

                if (response.llmAnalysis.toLowerCase(Locale.ROOT).contains("unhealthy")) {
                    txtLlmResult.setText("Unhealthy sentiment");
                } else {
                    txtLlmResult.setText("Healthy sentiment");
                }

                addPointToChart((float) compound);

                mainViewModel.saveResultToHistory(text, response);
                // Note: The success toast is now handled via statusMessage in ViewModel

            } else {
                // On failure, show an error message
                Toast.makeText(this, "Error: Analysis failed", Toast.LENGTH_LONG).show();
                // Ensure cards are hidden on failure
                postCard.setVisibility(View.GONE);
                analysisCard.setVisibility(View.GONE);
            }
        });
    }

    private void setupChart() {
        // Chart setup logic remains the same...
    }

    private void addPointToChart(float y) {
        // Chart update logic remains the same...
    }

    private void showLoading(boolean isLoading, String message) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            progressText.setText(message);
        } else {
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
        }
        btnFetchAndAnalyze.setEnabled(!isLoading);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(this, HistoryActivity.class));
        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userEmailTextView = headerView.findViewById(R.id.userEmailTextView);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmailTextView.setText(user.getEmail());
        }
    }
}
