package com.example.cendana2000.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cendana2000.bakingapp.Network.RecipeIngredientResponse;
import com.example.cendana2000.bakingapp.Network.RecipeResponse;
import com.example.cendana2000.bakingapp.Network.RecipeStepResponse;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */

    /**
     * The dummy content this fragment is presenting.
     */
    private int mPosition;
    private RecipeResponse mRecipeResponse;
    private SimpleExoPlayer player;
    @BindView(R.id.recipe_detail) TextView mRecipeDetail;
    private String EXOPLAYER_KEY = "player_position";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(RecipeListActivity.POSITION_TAG)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mPosition = getArguments().getInt(RecipeListActivity.POSITION_TAG);
            mRecipeResponse = getArguments().getParcelable(RecipeListActivity.RECIPE_RESPONSES_TAG);

            Activity activity = this.getActivity();
            Toolbar toolbar = (Toolbar) activity.findViewById(R.id.detail_toolbar);
            if (toolbar != null) {
                if(mPosition == 0) {
                    toolbar.setTitle(mRecipeResponse.getName()+ "'s Ingredient");
                } else {
                    int index = mPosition - 1;
                    toolbar.setTitle(mRecipeResponse.getSteps().get(index).getShortDescription());
                }
                ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        Context context = container.getContext();
        ButterKnife.bind(this, rootView);

        // Show the dummy content as text in a TextView.
        if (mPosition == 0) {
            String ingredient = "";
            for (RecipeIngredientResponse recipeIngredient: mRecipeResponse.getIngredients()) {
                ingredient = ingredient.concat(recipeIngredient.getQuantity() + " "+ recipeIngredient.getMeasure() + " of " + recipeIngredient.getIngredient() + "\n\n");
            }
            mRecipeDetail.setText(ingredient);
        } else {
            int index  = mPosition - 1;
            RecipeStepResponse recipeStep = mRecipeResponse.getSteps().get(index);
            String videoUrlString = recipeStep.getVideoURL();
            String stepDescription = recipeStep.getDescription();

            if(videoUrlString != null && !videoUrlString.isEmpty()) {
                Long currentPosition = null;
                if(savedInstanceState != null) {
                    currentPosition = savedInstanceState.getLong(EXOPLAYER_KEY);
                }
                preparedExoplayer(context, rootView, recipeStep.getVideoURL(), currentPosition);
            }

            mRecipeDetail.setText(stepDescription);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(player != null) {
            player.setPlayWhenReady(true);
        }
    }

    private void preparedExoplayer(Context context, View rootView, String videoStepString, Long currentPosition) {
        // 1. Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        SimpleExoPlayerView simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.simple_exo_player_recipe_detail);
        simpleExoPlayerView.setPlayer(player);
        simpleExoPlayerView.setVisibility(View.VISIBLE);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMetero = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getString(R.string.app_name)), bandwidthMetero);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        Uri mp4VideoUri = Uri.parse(videoStepString);
        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri,
                dataSourceFactory, extractorsFactory, null, null);

        player.prepare(videoSource);
        if(currentPosition != null) {
            player.seekTo(currentPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(player != null) {
            outState.putLong(EXOPLAYER_KEY, player.getCurrentPosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(player != null) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player != null) {
            player.release();
            player = null;
        }
    }
}
