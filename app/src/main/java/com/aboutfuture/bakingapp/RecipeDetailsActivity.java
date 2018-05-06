package com.aboutfuture.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity implements MasterRecipeFragment.OnStepClickListener {

    private Recipe mRecipe;

    @BindString(R.string.error_message)
    String errorMsg;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(RecipesActivity.RECIPE_KEY)) {
                    mRecipe = intent.getParcelableExtra(RecipesActivity.RECIPE_KEY);
                    setTitle(mRecipe.getName());

                    FragmentManager fragmentManager = getSupportFragmentManager();

                    MasterRecipeFragment masterFragment = new MasterRecipeFragment();
                    masterFragment.setSteps(mRecipe.getSteps());
                    masterFragment.setIngredients(mRecipe.getIngredients());
                    fragmentManager.beginTransaction()
                            .add(R.id.master_container, masterFragment)
                            .commit();

                    if (findViewById(R.id.step_details_container) != null) {
                        mTwoPane = true;

                        StepDetailsFragment stepFragment = new StepDetailsFragment();
                        stepFragment.setSteps(mRecipe.getSteps());
                        fragmentManager.beginTransaction()
                                .add(R.id.step_details_container, stepFragment)
                                .commit();

                        LinearLayout navigationButtons = findViewById(R.id.navigation_layout);
                        navigationButtons.setVisibility(View.GONE);
                    } else {
                        mTwoPane = false;
                    }
                }
            } else {
                closeOnError(errorMsg);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RecipesActivity.RECIPE_KEY, mRecipe);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RecipesActivity.RECIPE_KEY)) {
                mRecipe = savedInstanceState.getParcelable(RecipesActivity.RECIPE_KEY);
                if (mRecipe != null) {
                    setTitle(mRecipe.getName());
                }
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void closeOnError(String message) {
        finish();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onStepSelected(int position) {
        if (mTwoPane) {
            StepDetailsFragment newFragment = new StepDetailsFragment();
            newFragment.setSteps(mRecipe.getSteps());
            newFragment.setPosition(position);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_details_container, newFragment)
                    .commit();

            // TODO: Hide navigation buttons

        } else {
            Intent intent = new Intent(this, StepDetailsActivity.class);
            intent.putExtra(RecipesActivity.RECIPE_STEP_KEY, mRecipe.getSteps());
            intent.putExtra(RecipesActivity.NUMBER_STEP_KEY, position);
            startActivity(intent);
        }
    }
}
