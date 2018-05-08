package com.aboutfuture.bakingapp;

import android.content.Context;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MasterRecipeFragment extends Fragment implements MasterRecipeAdapter.ItemClickListener {

    private ArrayList<Step> mSteps;
    private ArrayList<Ingredient> mIngredients;
    private OnStepClickListener mCallback;

    @BindView(R.id.ingredients_text_view)
    TextView ingredientsTextView;
    @BindView(R.id.recipe_steps_rv)
    RecyclerView stepListRecyclerView;

    @Override
    public void onItemClicked(int stepClicked) {
        mCallback.onStepSelected(stepClicked);
    }

    public interface OnStepClickListener {
        void onStepSelected(int position);
    }

    // Required empty public constructor
    public MasterRecipeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(RecipesActivity.RECIPE_STEP_KEY);
            mIngredients = savedInstanceState.getParcelableArrayList(RecipesActivity.INGREDIENTS_LIST_KEY);
        }

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_master_recipe, container, false);
        // Bind the views
        ButterKnife.bind(this, rootView);

        String ingredientsList = "";
        for (int i = 0; i < mIngredients.size(); i++) {
            // Format quantity
            double quantity = mIngredients.get(i).getQuantity();
            String stringQuantity;
            if (quantity - (int)quantity != 0) {
                stringQuantity = String.valueOf(quantity);
            } else {
                stringQuantity = String.valueOf((int) quantity);
            }

            ingredientsList = TextUtils.concat(
                    ingredientsList,
                    stringQuantity,
                    " ",
                    mIngredients.get(i).getMeasure(),
                    " ",
                    mIngredients.get(i).getIngredientName(),
                    "\n").toString();
        }
        ingredientsTextView.setText(ingredientsList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        stepListRecyclerView.setLayoutManager(mLayoutManager);

        MasterRecipeAdapter mAdapter = new MasterRecipeAdapter(getContext(), mSteps, this);
        stepListRecyclerView.setAdapter(mAdapter);

        stepListRecyclerView.setHasFixedSize(true);
        stepListRecyclerView.setNestedScrollingEnabled(false);

        return rootView;
    }

    public void setSteps(ArrayList<Step> steps) { mSteps = steps; }
    public void setIngredients(ArrayList<Ingredient> ingredients) { mIngredients = ingredients; }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(RecipesActivity.RECIPE_STEP_KEY, mSteps);
        outState.putParcelableArrayList(RecipesActivity.INGREDIENTS_LIST_KEY, mIngredients);
    }
}
