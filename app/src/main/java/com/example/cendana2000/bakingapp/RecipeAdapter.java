package com.example.cendana2000.bakingapp;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cendana2000.bakingapp.Network.RecipeResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Cendana2000 on 17-Sep-17.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{
    private List<RecipeResponse> mRecipeResponseList;
    private final ListItemClickListener mOnClickListener;
    private Context context;

    public RecipeAdapter(ListItemClickListener listItemClickListener) {
        this.mOnClickListener = listItemClickListener;
    }

    interface ListItemClickListener {
        void onListItemClick(RecipeResponse recipeResponse);
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int resourceId = R.layout.item_recipe;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View recipeView = layoutInflater.inflate(resourceId, parent, false);

        return new RecipeViewHolder(recipeView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        RecipeResponse currentRecipe = mRecipeResponseList.get(position);
        String recipeName = currentRecipe.getName();
        String recipeImage = currentRecipe.getImage();

        holder.mRecipeText.setText(recipeName);
        if(recipeImage != null && !recipeImage.isEmpty()) {
            Picasso.with(context).load(recipeImage).into(holder.mRecipeImage);
        } else {
            Picasso.with(context).load(R.drawable.ic_cake_orange).into(holder.mRecipeImage);
        }
    }

    @Override
    public int getItemCount() {
        if(mRecipeResponseList == null) return 0;
        return mRecipeResponseList.size();
    }

    public void setData(List<RecipeResponse> recipeResponse) {
        mRecipeResponseList = recipeResponse;
        notifyDataSetChanged();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mRecipeText;
        ImageView mRecipeImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mRecipeText = (TextView) itemView.findViewById(R.id.text_recipe_item);
            mRecipeText.setOnClickListener(this);

            mRecipeImage = (ImageView) itemView.findViewById(R.id.image_recipe_item);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            RecipeResponse recipeResponse = mRecipeResponseList.get(position);
            mOnClickListener.onListItemClick(recipeResponse);
        }
    }
}
