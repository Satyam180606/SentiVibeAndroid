package com.example.sentivibe.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

/**
 * Represents a single sentiment analysis result to be stored in Firebase Firestore.
 * This is a Plain Old Java Object (POJO) that Firestore can automatically serialize.
 */
public class AnalysisResult {

    private String inputText;
    private double vaderCompound;
    private double textblobPolarity;
    private @ServerTimestamp Date timestamp;

    // Required empty public constructor for Firestore deserialization
    public AnalysisResult() {}

    public AnalysisResult(String inputText, double vaderCompound, double textblobPolarity) {
        this.inputText = inputText;
        this.vaderCompound = vaderCompound;
        this.textblobPolarity = textblobPolarity;
    }

    // --- Getters and Setters for Firestore --- //

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public double getVaderCompound() {
        return vaderCompound;
    }

    public void setVaderCompound(double vaderCompound) {
        this.vaderCompound = vaderCompound;
    }

    public double getTextblobPolarity() {
        return textblobPolarity;
    }

    public void setTextblobPolarity(double textblobPolarity) {
        this.textblobPolarity = textblobPolarity;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
