package com.aboutfuture.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MasterRecipeFragment extends Fragment {

    private ArrayList<Step> mSteps;
    private ArrayList<Ingredient> mIngredients;
    private OnStepClickListener mCallback;

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
        TextView ingredientsTextView = rootView.findViewById(R.id.ingredients_text_view);
        ListView listView = rootView.findViewById(R.id.recipe_steps_list_view);

        String ingredientsList = " ";
        Log.v("INGREDIENTS SIZE", String.valueOf(mIngredients.size()));

        //TODO: Ingredients not done
        for (int i = 0; i < mIngredients.size(); i++) {
            TextUtils.concat(
                    ingredientsList,
                    //TODO: format quantity
                    String.valueOf(mIngredients.get(i).getQuantity()),
                    " ",
                    mIngredients.get(i).getMeasure(),
                    " ",
                    mIngredients.get(i).getIngredientName(),
                    "\n"
            );

            Log.v("INGREDIENTS", mIngredients.get(i).getIngredientName());
        }
        ingredientsTextView.setText(ingredientsList);

        MasterRecipeAdapter mAdapter = new MasterRecipeAdapter(getContext(), mSteps);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Trigger the callback method and pass in the position that was clicked
                mCallback.onStepSelected(position);
            }
        });

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
