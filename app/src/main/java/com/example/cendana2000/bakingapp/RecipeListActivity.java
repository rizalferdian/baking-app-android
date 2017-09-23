package com.example.cendana2000.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cendana2000.bakingapp.Network.RecipeResponse;
import com.example.cendana2000.bakingapp.Network.RecipeStepResponse;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static final String RECIPE_RESPONSES_TAG = "recipe_response";
    public static final String POSITION_TAG = "position";

    @BindView(R.id.recipe_list) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if(intent.hasExtra(RECIPE_RESPONSES_TAG)){
            RecipeResponse recipeResponse = intent.getParcelableExtra(RECIPE_RESPONSES_TAG);

            setupToolbar(recipeResponse.getName() + "'s Recipes");
            setupRecyclerView(recyclerView, recipeResponse);

            if (findViewById(R.id.recipe_detail_container) != null) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-w900dp).
                // If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, @NonNull RecipeResponse recipeResponse) {
        recyclerView.setAdapter(new RecipeListAdapter(recipeResponse));
    }

    private void setupToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }

    public class RecipeListAdapter
            extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

        private final RecipeResponse mValues;
        private Context context;

        public RecipeListAdapter(RecipeResponse items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            context = parent.getContext();
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_recipe_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues;

            if(position == 0) {
                holder.mRecipeListText.setText(holder.mItem.getName() + "'s Ingredient");
                String recipeImage = holder.mItem.getImage();
                if(recipeImage != null && !recipeImage.isEmpty()) {
                    Picasso.with(context).load(recipeImage).into(holder.mRecipeListImage);
                } else {
                    Picasso.with(context).load(R.drawable.ic_cake_orange).into(holder.mRecipeListImage);
                }
            } else {
                int index = position - 1;
                RecipeStepResponse currentStep = holder.mItem.getSteps().get(index);
                holder.mRecipeListText.setText(currentStep.getShortDescription());

                String recipeImage = currentStep.getThumbnailURL();
                if(recipeImage != null && !recipeImage.isEmpty()) {
                    Picasso.with(context)
                            .load(recipeImage)
                            .placeholder(R.drawable.ic_timer_black)
                            .error(R.drawable.ic_timer_black).into(holder.mRecipeListImage);
                } else {
                    Picasso.with(context).load(R.drawable.ic_timer_black).into(holder.mRecipeListImage);
                }
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {

                        Bundle arguments = new Bundle();
                        arguments.putInt(RecipeListActivity.POSITION_TAG, position);
                        arguments.putParcelable(RecipeListActivity.RECIPE_RESPONSES_TAG, holder.mItem);

                        RecipeDetailFragment fragment = new RecipeDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.recipe_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipeDetailActivity.class);
                        intent.putExtra(POSITION_TAG, position);
                        intent.putExtra(RECIPE_RESPONSES_TAG, holder.mItem);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if(mValues == null) return 0;
            return mValues.getSteps().size() + 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mRecipeListText;
            private final ImageView mRecipeListImage;
            public RecipeResponse mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mRecipeListText = (TextView) view.findViewById(R.id.text_recipe_list_item);
                mRecipeListImage = (ImageView) view.findViewById(R.id.image_recipe_list_item);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mRecipeListText.getText() + "'";
            }
        }
    }
}
