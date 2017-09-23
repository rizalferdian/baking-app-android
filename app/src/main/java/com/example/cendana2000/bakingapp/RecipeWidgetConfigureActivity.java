package com.example.cendana2000.bakingapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.cendana2000.bakingapp.Network.RecipeIngredientResponse;
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

import static com.example.cendana2000.bakingapp.MainActivity.calculateNoOfColumns;

/**
 * The configuration screen for the {@link RecipeWidget RecipeWidget} AppWidget.
 */
public class RecipeWidgetConfigureActivity extends Activity implements RecipeAdapter.ListItemClickListener {

    public static final String PREFS_WIDGET_TITLE = "com.example.cendana2000.bakingapp.RecipeWidget.widget_title";
    public static final String PREFS_WIDGET_TEXT = "com.example.cendana2000.bakingapp.RecipeWidget.widget_text";

    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    @BindView(R.id.recycler_recipe_widget) RecyclerView mRecipeRecycler;
    RecipeAdapter mRecipeAdapter;

    public RecipeWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveWidgetPref(Context context, int appWidgetId, String text, String PREFS_WIDGET_TITLE) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_WIDGET_TITLE, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadWidgetPref(Context context, int appWidgetId, String PREFS_WIDGET_TITLE) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_WIDGET_TITLE, 0);
        String value = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);

        return value;
    }

    static void deleteWidgetPref(Context context, int appWidgetId, String PREFS_WIDGET_TITLE) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_WIDGET_TITLE, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.recipe_widget_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        ButterKnife.bind(this);

        GridLayoutManager mRecipeLayout = new GridLayoutManager(this, calculateNoOfColumns(this));
        mRecipeRecycler.setLayoutManager(mRecipeLayout);

        mRecipeAdapter = new RecipeAdapter(this);
        mRecipeRecycler.setAdapter(mRecipeAdapter);
        mRecipeRecycler.setHasFixedSize(true);

        fetchRecipeData();

//        mAppWidgetText.setText(loadWidgetPref(RecipeWidgetConfigureActivity.this, mAppWidgetId));
    }

    private void fetchRecipeData() {
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
            }

            @Override
            public void onFailure(Call<List<RecipeResponse>> call, Throwable t) {
            }
        });
    }

    private void setDataToAdapter(List<RecipeResponse> recipeResponse) {
        mRecipeAdapter.setData(recipeResponse);
    }

    @Override
    public void onListItemClick(RecipeResponse recipeResponse) {
        final Context context = RecipeWidgetConfigureActivity.this;

        // When the button is clicked, store the string locally
        String widgetTitle = recipeResponse.getName();
        String widgetText = "";
        for (RecipeIngredientResponse recipeIngredient: recipeResponse.getIngredients()) {
            widgetText = widgetText.concat(recipeIngredient.getQuantity() + " "+ recipeIngredient.getMeasure() + " of " + recipeIngredient.getIngredient() + "\n\n");
        }

        saveWidgetPref(context, mAppWidgetId, widgetTitle, PREFS_WIDGET_TITLE);
        saveWidgetPref(context, mAppWidgetId, widgetText, PREFS_WIDGET_TEXT);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RecipeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}

