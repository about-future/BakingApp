package com.aboutfuture.bakingapp.widget;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aboutfuture.bakingapp.R;
import com.aboutfuture.bakingapp.data.RecipesContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WidgetConfigureAdapter extends RecyclerView.Adapter<WidgetConfigureAdapter.RecipesViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private final RecipeClickListener mOnClickListener;

    public interface RecipeClickListener {
        void onItemClick(int recipeId, String recipeName);
    }

    public WidgetConfigureAdapter(Context context, WidgetConfigureAdapter.RecipeClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public WidgetConfigureAdapter.RecipesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, parent, false);
        view.setFocusable(true);
        return new WidgetConfigureAdapter.RecipesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final WidgetConfigureAdapter.RecipesViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int nameColumnIndex = mCursor.getColumnIndex(RecipesContract.RecipesEntry.COLUMN_NAME);
        holder.recipeNameTextView.setText(mCursor.getString(nameColumnIndex));
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    // This method swaps the old recipe result with the newly loaded ones and notify the change
    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class RecipesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.simple_name_tv)
        TextView recipeNameTextView;

        RecipesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idColumnIndex = mCursor.getColumnIndex(RecipesContract.RecipesEntry.COLUMN_RECIPE_ID);
            int nameColumnIndex = mCursor.getColumnIndex(RecipesContract.RecipesEntry.COLUMN_NAME);
            mOnClickListener.onItemClick(
                    mCursor.getInt(idColumnIndex),
                    mCursor.getString(nameColumnIndex));
        }
    }
}
