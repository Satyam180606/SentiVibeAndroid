package com.example.sentivibe.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sentivibe.data.ApiService;
import com.example.sentivibe.data.RetrofitClient;
import com.example.sentivibe.data.SentimentModels.ScoreRequest;
import com.example.sentivibe.data.SentimentModels.SentimentResponse;
import com.example.sentivibe.model.AnalysisResult;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for handling all data operations for sentiment analysis.
 * This class abstracts the data sources (Network and Database) from the rest of the app.
 * It follows the Singleton pattern to ensure only one instance of the repository exists.
 */
public class SentimentRepository {

    private static final String TAG = "SentimentRepository";
    private static volatile SentimentRepository instance;

    private final ApiService apiService;
    private final FirebaseFirestore database;

    // Private constructor for Singleton pattern
    private SentimentRepository() {
        apiService = RetrofitClient.getApi();
        database = FirebaseFirestore.getInstance();
    }

    /**
     * Returns the single instance of the SentimentRepository.
     */
    public static SentimentRepository getInstance() {
        if (instance == null) {
            synchronized (SentimentRepository.class) {
                if (instance == null) {
                    instance = new SentimentRepository();
                }
            }
        }
        return instance;
    }

    /**
     * Analyzes the text by calling the backend API.
     * Uses LiveData to post the result back to the ViewModel asynchronously.
     *
     * @param text The text to analyze.
     * @return A LiveData object containing the SentimentResponse.
     */
    public LiveData<SentimentResponse> analyzeText(String text) {
        MutableLiveData<SentimentResponse> responseLiveData = new MutableLiveData<>();
        ScoreRequest request = new ScoreRequest(text);

        apiService.score(request).enqueue(new Callback<SentimentResponse>() {
            @Override
            public void onResponse(@NonNull Call<SentimentResponse> call, @NonNull Response<SentimentResponse> response) {
                if (response.isSuccessful()) {
                    responseLiveData.postValue(response.body());
                } else {
                    // Post null or a custom error object if the response was not successful
                    responseLiveData.postValue(null);
                    Log.e(TAG, "API call failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SentimentResponse> call, @NonNull Throwable t) {
                responseLiveData.postValue(null);
                Log.e(TAG, "API call failed with exception: ", t);
            }
        });

        return responseLiveData;
    }

    /**
     * Saves a successful analysis result to the Firestore database.
     *
     * @param result The AnalysisResult object to save.
     */
    public void saveResultToHistory(AnalysisResult result) {
        database.collection("analysis_history")
                .add(result)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Result saved with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving result", e));
    }
}
