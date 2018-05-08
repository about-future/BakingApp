package com.aboutfuture.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MasterRecipeAdapter extends RecyclerView.Adapter<MasterRecipeAdapter.StepViewHolder> {

    private ArrayList<Step> mSteps;
    private final ItemClickListener mOnClickListener;

    public interface ItemClickListener {
        void onItemClicked(int stepClicked);
    }

    public MasterRecipeAdapter(ArrayList<Step> steps, ItemClickListener listener) {
        mSteps = steps;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public MasterRecipeAdapter.StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_item, parent, false);
        view.setFocusable(false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        // Set the text and return the newly created TextView
        if (position == 0) {
            holder.stepDescriptionTextView.setText(mSteps.get(position).getShortDescription());
        } else {
            holder.stepDescriptionTextView.setText(TextUtils.concat(
                    String.valueOf(position),
                    ". ",
                    mSteps.get(position).getShortDescription()));
        }
    }

    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        return mSteps.size();
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.step_short_description_tv)
        TextView stepDescriptionTextView;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onItemClicked(clickedPosition);
        }
    }
}
