package com.example.gymtrackmovil.api;

import com.example.gymtrackmovil.models.QuoteResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {
    @GET("quotes/random")
    Call<QuoteResponse> getRandomQuote();
}
