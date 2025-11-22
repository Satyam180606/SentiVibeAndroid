package com.example.sentivibe.data;

public class SentimentModels {
    public static class ScoreRequest {
        public String text;
        public ScoreRequest(String text) { this.text = text; }
    }

    public static class SentimentResponse {
        public Vader vader;
        public TextBlob textblob;
    }

    public static class Vader {
        public double neg;
        public double neu;
        public double pos;
        public double compound;
    }

    public static class TextBlob {
        public double polarity;
        public double subjectivity;
    }
}
