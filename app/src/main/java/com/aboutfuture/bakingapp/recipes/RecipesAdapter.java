package com.aboutfuture.bakingapp.recipes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboutfuture.bakingapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder> {

    private final Context mContext;
    private ArrayList<Recipe> mRecipes = new ArrayList<Recipe>() {
    };
    private final GridItemClickListener mOnClickListener;

    public interface GridItemClickListener {
        void onGridItemClick(int recipeId, String recipeName);
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
    public void onBindViewHolder(@NonNull final RecipesViewHolder holder, int position) {
        String recipeName = mRecipes.get(position).getName();
        final String imagePath = mRecipes.get(position).getImagePath();

        switch (recipeName) {
            case "Cheesecake":
                holder.recipeImageView.setImageResource(R.drawable.cheesecake);
                break;
            case "Nutella Pie":
                holder.recipeImageView.setImageResource(R.drawable.nutella_pie);
                break;
            case "Brownies":
                holder.recipeImageView.setImageResource(R.drawable.brownies);
                break;
            case "Yellow Cake":
                holder.recipeImageView.setImageResource(R.drawable.yellow_cake);
                break;
            default:

                if (!TextUtils.isEmpty(imagePath)) {
                    // Try loading image from device memory or cache
                    Picasso.get()
                            .load(imagePath)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(holder.recipeImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    // Yay!
                                }

                                @Override
                                public void onError(Exception e) {
                                    // Try again online, if cache loading failed
                                    Picasso.get()
                                            .load(imagePath)
                                            .error(R.drawable.cake)
                                            .into(holder.recipeImageView);
                                }
                            });
                } else {
                    holder.recipeImageView.setImageResource(R.drawable.cake);
                }
                holder.allRightsReservedTextView.setVisibility(View.INVISIBLE);
                break;
        }

        holder.recipeNameTextView.setText(recipeName);
        holder.recipeServingSizeTextView.setText(
                String.format(
                        mContext.getString(R.string.servings),
                        mRecipes.get(position).getServings()));
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mRecipes.size();
    }

    // This method swaps the old recipe result with the newly loaded ones and notify the change
    public void swapRecipes(ArrayList<Recipe> newRecipes) {
        mRecipes = newRecipes;
        notifyDataSetChanged();
    }

    public class RecipesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.recipe_iv)
        ImageView recipeImageView;
        @BindView(R.id.all_rights_reserved_tv)
        TextView allRightsReservedTextView;
        @BindView(R.id.recipe_name_tv_)
        TextView recipeNameTextView;
        @BindView(R.id.recipe_serving_tv)
        TextView recipeServingSizeTextView;

        RecipesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mOnClickListener.onGridItemClick(
                    mRecipes.get(adapterPosition).getId(),
                    mRecipes.get(adapterPosition).getName());
        }
    }
}
