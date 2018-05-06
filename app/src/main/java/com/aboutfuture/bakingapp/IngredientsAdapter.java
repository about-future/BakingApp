package com.aboutfuture.bakingapp;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Ingredient;

import java.util.ArrayList;

public class IngredientsAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Ingredient> mIngredients;

    public IngredientsAdapter(Context context, ArrayList<Ingredient> ingredients) {
        mContext = context;
        mIngredients = ingredients;
    }

    @Override
    public int getCount() {
        //if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        //LinearLayout ingredientItemLayout;
        TextView ingredientTextView;
        if (convertView == null) {
            // If the view is not recycled, this creates a new View to hold an ingredient
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ingredient_list_item, viewGroup, false);
            ingredientTextView = convertView.findViewById(R.id.ingredient_tv);

        } else {
            ingredientTextView = (TextView) convertView;
        }

        ingredientTextView.setText(TextUtils.concat(
                //TODO: format quantity
                String.valueOf( mIngredients.get(position).getQuantity()),
                " ",
                mIngredients.get(position).getMeasure(),
                " ",
                mIngredients.get(position).getIngredientName()));

        return ingredientTextView;
    }
}
