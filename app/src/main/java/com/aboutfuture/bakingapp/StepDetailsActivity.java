package com.aboutfuture.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;
import java.util.Set;

public class StepDetailsActivity extends AppCompatActivity {

    private ArrayList<Step> mSteps;
    private ArrayList<Ingredient> mIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);

        if (savedInstanceState == null) {
            // Get the correct recipe steps and the index to access in the array of recipe steps
            // from the intent. Set the default value to 0 (as in the first step)
            int mStepNumber = getIntent().getIntExtra(RecipesActivity.NUMBER_STEP_KEY, 0);

            FragmentManager fragmentManager = getSupportFragmentManager();

            StepDetailsFragment stepFragment = new StepDetailsFragment();
            if (mStepNumber == 0) {
                mIngredients = getIntent().getParcelableArrayListExtra(RecipesActivity.INGREDIENTS_LIST_KEY);
                stepFragment.setIngredients(mIngredients);
            } else {
                mSteps = getIntent().getParcelableArrayListExtra(RecipesActivity.RECIPE_STEP_KEY);
                stepFragment.setSteps(mSteps);

            }
            stepFragment.setPosition(mStepNumber);
            fragmentManager.beginTransaction()
                    .add(R.id.step_details_container, stepFragment)
                    .commit();
        }
    }
}
