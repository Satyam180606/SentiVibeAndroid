package com.example.sentivibe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sentivibe.R;
import com.example.sentivibe.model.AnalysisResult;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for the RecyclerView in HistoryActivity.
 * This class binds the list of AnalysisResult objects from Firestore to the RecyclerView items.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<AnalysisResult> results;

    public HistoryAdapter(List<AnalysisResult> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        AnalysisResult result = results.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return results != null ? results.size() : 0;
    }

    /**
     * ViewHolder for a single history item.
     */
    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtInput;
        private final TextView txtScores;
        private final TextView txtTimestamp;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInput = itemView.findViewById(R.id.txtHistoryInput);
            txtScores = itemView.findViewById(R.id.txtHistoryScores);
            txtTimestamp = itemView.findViewById(R.id.txtHistoryTimestamp);
        }

        void bind(AnalysisResult result) {
            txtInput.setText(result.getInputText());
            txtScores.setText(String.format(Locale.US, "VADER: %.2f | TextBlob: %.2f",
                    result.getVaderCompound(), result.getTextblobPolarity()));

            // Format the timestamp from the Date object
            if (result.getTimestamp() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                txtTimestamp.setText(sdf.format(result.getTimestamp()));
            }
        }
    }
}
