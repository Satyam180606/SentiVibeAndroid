package com.example.sentivibe;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

/**
 * This activity handles fetching and analyzing a post from a social media URL.
 */
public class SocialAnalysisActivity extends AppCompatActivity {

    public static final String EXTRA_SOCIAL_PLATFORM = "EXTRA_SOCIAL_PLATFORM";
    public static final String PLATFORM_X = "X";
    public static final String PLATFORM_REDDIT = "Reddit";

    private Toolbar toolbar;
    private ImageView socialLogo;
    private TextInputEditText urlInput;
    private MaterialButton analyzeButton;
    private ProgressBar progressBar;
    private MaterialCardView resultsCard;

    private String platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_analysis);

        // Initialize all views
        initializeViews();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get the selected platform (X or Reddit) from the Intent
        platform = getIntent().getStringExtra(EXTRA_SOCIAL_PLATFORM);
        if (platform == null) {
            // Default to X if something goes wrong
            platform = PLATFORM_X;
        }

        // Update the UI based on the selected platform
        updateUiForPlatform();

        // Set click listener for the analyze button
        analyzeButton.setOnClickListener(v -> {
            String url = urlInput.getText().toString().trim();
            if (url.isEmpty()) {
                urlInput.setError("Please paste a valid URL");
                return;
            }
            // For now, just show a toast. We will build the API call next.
            Toast.makeText(this, "Fetching data for: " + url, Toast.LENGTH_SHORT).show();
        });
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar_social);
        socialLogo = findViewById(R.id.img_social_logo);
        urlInput = findViewById(R.id.urlInputText);
        analyzeButton = findViewById(R.id.btnFetchAndAnalyze);
        progressBar = findViewById(R.id.progress_bar_social);
        resultsCard = findViewById(R.id.card_post_results);
    }

    /**
     * Configures the UI elements (like the logo) based on whether
     * the user chose X or Reddit.
     */
    private void updateUiForPlatform() {
        if (PLATFORM_X.equals(platform)) {
            // Here you would set the actual X logo
            socialLogo.setImageResource(android.R.drawable.ic_menu_share); // Placeholder
        } else if (PLATFORM_REDDIT.equals(platform)) {
            // Here you would set the actual Reddit logo
            socialLogo.setImageResource(android.R.drawable.ic_menu_share); // Placeholder
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the back button click
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
