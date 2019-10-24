package com.example.cendana2000.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.example.cendana2000.bakingapp.Network.RecipeResponse;
import com.example.cendana2000.bakingapp.Network.RecipeService;
import com.example.cendana2000.bakingapp.Network.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.ListItemClickListener {
    @BindView(R.id.recycler_recipe) RecyclerView mRecipeRecycler;
    @BindView(R.id.text_error) TextView mTextError;

    RecipeAdapter mRecipeAdapter;
    private CountingIdlingResource mIdlingResource = new CountingIdlingResource("Network_Call");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager mRecipeLayout = new GridLayoutManager(this, calculateNoOfColumns(this));
        mRecipeRecycler.setLayoutManager(mRecipeLayout);

        mRecipeAdapter = new RecipeAdapter(this);
        mRecipeRecycler.setAdapter(mRecipeAdapter);
        mRecipeRecycler.setHasFixedSize(true);

        fetchRecipeData();
    }

    private void fetchRecipeData() {
        mIdlingResource.increment();
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RecipeService service = retrofit.create(RecipeService.class);

        Call<List<RecipeResponse>> call = service.getRecipe();
        call.enqueue(new Callback<List<RecipeResponse>>() {
            @Override
            public void onResponse(Call<List<RecipeResponse>> call, Response<List<RecipeResponse>> response) {
                List<RecipeResponse> recipeResponsesList = response.body();
                setDataToAdapter(recipeResponsesList);
                mIdlingResource.decrement();
            }

            @Override
            public void onFailure(Call<List<RecipeResponse>> call, Throwable t) {
                mTextError.setVisibility(View.VISIBLE);
                mIdlingResource.decrement();
            }
        });
    }

    private void setDataToAdapter(List<RecipeResponse> recipeResponse) {
        mRecipeAdapter.setData(recipeResponse);
    }

    // method to calculate the columns
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180 * 2;
        if(dpWidth > 900) {
            return (int) (dpWidth / scalingFactor);
        }
        return 1;
    }

    @Override
    public void onListItemClick(final RecipeResponse recipeResponse) {
        Intent intent = new Intent(this, RecipeListActivity.class);
        intent.putExtra(RecipeListActivity.RECIPE_RESPONSES_TAG, recipeResponse);
        startActivity(intent);
    }

    public CountingIdlingResource getIdlingResource() {
        return mIdlingResource;
    }
}
