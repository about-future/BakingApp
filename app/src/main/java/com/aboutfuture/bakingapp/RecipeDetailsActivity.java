package com.aboutfuture.bakingapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity implements MasterRecipeFragment.OnStepClickListener {

    private int mRecipeId;
    private String mRecipeName;
    private ArrayList<Ingredient> mRecipeIngredients;
    private ArrayList<Step> mRecipeSteps;
    private Toast mToast;
    private Recipe mRecipe;

    @BindString(R.string.error_message)
    String errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        // We initialize and set the toolbar
        //setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(RecipesActivity.RECIPE_KEY)) {
                    mRecipe = intent.getParcelableExtra(RecipesActivity.RECIPE_KEY);
                    setTitle(mRecipe.getName());

//                    mRecipeSteps = new ArrayList<>();
//                    mRecipeSteps.add(new Step(0, getString(R.string.ingredients), null, null, null));
//                    mRecipeSteps.addAll(mRecipe.getSteps());

                    FragmentManager fragmentManager = getSupportFragmentManager();

                    MasterRecipeFragment masterFragment = new MasterRecipeFragment();
                    masterFragment.setRecipe(mRecipe);
                    fragmentManager.beginTransaction()
                            .add(R.id.master_container, masterFragment)
                            .commit();
                }
            } else {
                closeOnError(errorMsg);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RecipesActivity.RECIPE_KEY, mRecipe);
        outState.putParcelableArrayList(RecipesActivity.RECIPE_KEY, mRecipeSteps);

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
//            if (savedInstanceState.containsKey(RECIPE_STEPS_KEY)) {
//                mRecipeSteps = savedInstanceState.getParcelableArrayList(RECIPE_STEPS_KEY);
//            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void closeOnError(String message) {
        finish();
        toastThis(message);
    }

    private void toastThis(String toastMessage) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();
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
        //Toast.makeText(this, "Position: " + position, Toast.LENGTH_SHORT).show();

//        if (position == 0) {
//
//        } else {
            Intent intent = new Intent(this, StepDetailsActivity.class);
            intent.putExtra(RecipesActivity.RECIPE_KEY, mRecipe);
            intent.putExtra(RecipesActivity.POSITION_KEY, position);
            startActivity(intent);
        //}
    }
}
