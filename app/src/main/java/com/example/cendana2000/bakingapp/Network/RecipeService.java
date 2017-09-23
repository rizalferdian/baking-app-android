package com.example.cendana2000.bakingapp.Network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Cendana2000 on 17-Sep-17.
 */

public interface RecipeService {
    @GET("android-baking-app-json")
    Call<List<RecipeResponse>> getRecipe();
}
