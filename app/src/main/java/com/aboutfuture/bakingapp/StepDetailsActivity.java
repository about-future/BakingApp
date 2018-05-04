package com.aboutfuture.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;

public class StepDetailsActivity extends AppCompatActivity {

    private Recipe mRecipe;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);

        if (savedInstanceState == null) {

            mRecipe = getIntent().getParcelableExtra(RecipesActivity.RECIPE_KEY);
            mPosition = getIntent().getIntExtra(RecipesActivity.POSITION_KEY, 1);
            //Log.v("STEP " + mStep.getId(), mStep.getDescription());

            FragmentManager fragmentManager = getSupportFragmentManager();

            StepDetailsFragment stepFragment = new StepDetailsFragment();
            stepFragment.setSteps(mRecipe);
            stepFragment.setPosition(mPosition);
            fragmentManager.beginTransaction()
                    .add(R.id.step_details_container, stepFragment)
                    .commit();
        }
    }
}
