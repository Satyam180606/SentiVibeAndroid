package com.example.sentivibe.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.sentivibe.data.SentimentModels.SentimentResponse;
import com.example.sentivibe.model.AnalysisResult;
import com.example.sentivibe.repository.SentimentRepository;
import com.google.firebase.auth.FirebaseAuth;

/**
 * ViewModel for the MainActivity.
 * This class is responsible for preparing and managing the data for the MainActivity.
 * It communicates with the SentimentRepository to fetch data and handles the business logic.
 */
public class MainViewModel extends ViewModel {

    private final SentimentRepository repository;

    public MainViewModel() {
        repository = SentimentRepository.getInstance();
    }

    /**
     * Triggers the sentiment analysis in the repository.
     *
     * @param text The text to be analyzed.
     * @return A LiveData object that the UI can observe for the result.
     */
    public LiveData<SentimentResponse> analyzeText(String text) {
        return repository.analyzeText(text);
    }

    /**
     * Saves the analysis result to the history via the repository.
     *
     * @param text The original text that was analyzed.
     * @param response The response received from the sentiment analysis API.
     */
    public void saveResultToHistory(String text, SentimentResponse response) {
        if (response != null && response.vader != null && response.textblob != null) {
            String userId = FirebaseAuth.getInstance().getUid();
            if (userId != null) {
                AnalysisResult result = new AnalysisResult(
                        text,
                        response.vader.compound,
                        response.textblob.polarity,
                        userId
                );
                repository.saveResultToHistory(result);
            }
        }
    }
}
