package com.aboutfuture.bakingapp;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;
import java.util.Set;

public class StepDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            if(getSupportActionBar()!= null) {
                getSupportActionBar().hide();
            }
        } else {
            if(getSupportActionBar()!= null) {
                getSupportActionBar().show();
            }
        }

        if (savedInstanceState == null) {
            // Get the correct recipe steps and the index to access in the array of recipe steps
            // from the intent. Set the default value to 0 (as in the first step)
            int stepNumber = getIntent().getIntExtra(RecipesActivity.NUMBER_STEP_KEY, 0);

            FragmentManager fragmentManager = getSupportFragmentManager();

            StepDetailsFragment stepFragment = new StepDetailsFragment();
            ArrayList<Step> mSteps = getIntent().getParcelableArrayListExtra(RecipesActivity.RECIPE_STEP_KEY);
            stepFragment.setSteps(mSteps);
            stepFragment.setPosition(stepNumber);
            fragmentManager.beginTransaction()
                    .add(R.id.step_details_container, stepFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
