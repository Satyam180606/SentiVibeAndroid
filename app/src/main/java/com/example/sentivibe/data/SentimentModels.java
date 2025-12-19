package com.example.sentivibe.data;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.Locale;

/**
 * Contains the data models for API requests and responses.
 */
public class SentimentModels {

    public static class ScoreRequest {
        @SerializedName("text")
        public final String text;

        public ScoreRequest(String text) {
            this.text = text;
        }
    }

    public static class SentimentResponse {
        @SerializedName("vader")
        public Vader vader;

        @SerializedName("textblob")
        public TextBlob textblob;

        // Added field for the new LLM analysis
        @SerializedName("llm_analysis")
        public String llmAnalysis;

        @NonNull
        @Override
        public String toString() {
            return "SentimentResponse{" +
                    "vader=" + vader +
                    ", textblob=" + textblob +
                    ", llmAnalysis='" + llmAnalysis + '\'' +
                    '}';
        }
    }

    public static class Vader {
        @SerializedName("neg")
        public double neg;

        @SerializedName("neu")
        public double neu;

        @SerializedName("pos")
        public double pos;

        @SerializedName("compound")
        public double compound;

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.US,
                    "Vader{neg=%.3f, neu=%.3f, pos=%.3f, compound=%.3f}",
                    neg, neu, pos, compound);
        }
    }

    public static class TextBlob {
        @SerializedName("polarity")
        public double polarity;

        @SerializedName("subjectivity")
        public double subjectivity;

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.US,
                    "TextBlob{polarity=%.3f, subjectivity=%.3f}",
                    polarity, subjectivity);
        }
    }
}
