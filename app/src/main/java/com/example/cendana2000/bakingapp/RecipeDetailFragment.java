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
    private Bundle mSavedInstanceState;
    private View mRootView;
    private Context mContext;
    private String mVideoUrlString = null;
    private Long mPlayerCurrentPosition = null;

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
        mContext = getActivity().getBaseContext();
        mRootView = rootView;
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
            mVideoUrlString = recipeStep.getVideoURL();

            mRecipeDetail.setText(recipeStep.getDescription());
        }

        if(savedInstanceState != null) {
            mSavedInstanceState = savedInstanceState;
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23)) {
            initializePlayer();
        }
    }

    private void initializePlayer() {
        if(mVideoUrlString != null && !mVideoUrlString.isEmpty()) {
            // 1. Create a default TrackSelector
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);

            SimpleExoPlayerView simpleExoPlayerView = (SimpleExoPlayerView) mRootView.findViewById(R.id.player_recipe_detail);
            simpleExoPlayerView.setPlayer(player);
            simpleExoPlayerView.setVisibility(View.VISIBLE);

            // Measures bandwidth during playback. Can be null if not required.
            DefaultBandwidthMeter bandwidthMetero = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                    Util.getUserAgent(mContext, getString(R.string.app_name)), bandwidthMetero);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            Uri mp4VideoUri = Uri.parse(mVideoUrlString);
            MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri,
                    dataSourceFactory, extractorsFactory, null, null);

            player.prepare(videoSource);
            if(mSavedInstanceState != null) {
                player.seekTo(mSavedInstanceState.getLong(EXOPLAYER_KEY));
                mSavedInstanceState = null;
            } else if(mPlayerCurrentPosition != null) {
                player.seekTo(mPlayerCurrentPosition);
                mPlayerCurrentPosition = null;
            }
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPlayerCurrentPosition != null) {
            outState.putLong(EXOPLAYER_KEY, mPlayerCurrentPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        setPlayerCurrentPosition();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    private void setPlayerCurrentPosition() {
        if(player != null) {
            mPlayerCurrentPosition = player.getCurrentPosition();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if(player != null) {
            player.release();
            player = null;
        }
    }
}
