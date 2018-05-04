package com.aboutfuture.bakingapp;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;

public class MasterRecipeAdapter extends BaseAdapter {

    private Context mContext;
    private Recipe mRecipe;

    public MasterRecipeAdapter(Context context, Recipe recipe) {
        mContext = context;
        mRecipe = recipe;
    }

    @Override
    public int getCount() { return mRecipe.getSteps().size() + 1; }

    @Override
    public Object getItem(int i) { return null; }

    @Override
    public long getItemId(int i) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        TextView stepDescriptionTextView;
        if (convertView == null) {
            // If the view is not recycled, this creates a new TextView to hold a step title
            convertView = LayoutInflater.from(mContext).inflate(R.layout.step_list_item, viewGroup, false);
            stepDescriptionTextView = convertView.findViewById(R.id.step_short_description_tv);
        } else {
            stepDescriptionTextView = (TextView) convertView;
        }

        // Set the text and return the newly created TextView
        if (position == 0) {
            stepDescriptionTextView.setText(mContext.getString(R.string.ingredients));
        } else if (position == 1) {
            stepDescriptionTextView.setText(mRecipe.getSteps().get(position - 1).getShortDescription());
        } else {
            stepDescriptionTextView.setText(TextUtils.concat(
                    String.valueOf(position - 1),
                    ". ",
                    mRecipe.getSteps().get(position - 1).getShortDescription()));
        }

        return stepDescriptionTextView;
    }
}
