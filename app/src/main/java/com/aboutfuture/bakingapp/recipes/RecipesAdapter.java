package com.aboutfuture.bakingapp.recipes;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboutfuture.bakingapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder> {

    private final Context mContext;
    private ArrayList<Recipe> mRecipes = new ArrayList<Recipe>() {
    };
    private final GridItemClickListener mOnClickListener;

    public interface GridItemClickListener {
        void onGridItemClick(Recipe recipeClicked);
    }

    public RecipesAdapter(Context context, GridItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public RecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recipe_list_item, parent, false);
        view.setFocusable(true);
        return new RecipesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesViewHolder holder, int position) {
        holder.recipeImageView.setImageResource(R.drawable.cheesecake);
        holder.recipeNameTextView.setText(mRecipes.get(position).getName());
        holder.recipeServingSizeTextView.setText(
                String.format(
                        mContext.getString(R.string.servings),
                        mRecipes.get(position).getServings()
                )
        );
    }

    @Override
    public int getItemCount() {
        if (mRecipes != null)
            return mRecipes.size();
        else
            return 0;
    }

    // This method swaps the old movie result with the newly loaded ones and notify the change
    public void swapRecipes(ArrayList<Recipe> newRecipes) {
        mRecipes = newRecipes;
        notifyDataSetChanged();
    }

    public class RecipesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_iv)
        ImageView recipeImageView;
        @BindView(R.id.recipe_name_tv_)
        TextView recipeNameTextView;
        @BindView(R.id.recipe_serving_tv)
        TextView recipeServingSizeTextView;

        public RecipesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mOnClickListener.onGridItemClick(mRecipes.get(adapterPosition));
        }
    }
}
