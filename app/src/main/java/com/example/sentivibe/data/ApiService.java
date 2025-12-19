package com.example.sentivibe.data;

import com.example.sentivibe.data.SentimentModels.ScoreRequest;
import com.example.sentivibe.data.SentimentModels.SentimentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("score")
    Call<SentimentResponse> score(@Body ScoreRequest req);

    @POST("fetch")
    Call<FetchResponse> fetch(@Body FetchRequest req);
}
