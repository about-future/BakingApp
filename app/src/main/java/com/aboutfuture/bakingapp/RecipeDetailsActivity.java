package com.aboutfuture.bakingapp;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;
import com.aboutfuture.bakingapp.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity {

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

                    Log.v("RECIPE", mRecipe.getIngredients().get(0).getIngredientName());
                }

//                if (intent.hasExtra(RecipesActivity.RECIPE_ID_KEY)) {
//                    // Save the passed recipe Id
//                    mRecipeId = intent.getIntExtra(RecipesActivity.RECIPE_ID_KEY, 0);
//                    // Set the title of our activity as the recipe title, passed from the other activity
//                    mRecipeName = intent.getStringExtra(RecipesActivity.RECIPE_NAME_KEY);
//                    setTitle(mRecipeName);
//
//                    Log.v("EXTRAS", String.valueOf(mRecipeId) + "| " + mRecipeName);
//                }
//
//                if (intent.hasExtra(RecipesActivity.INGREDIENTS_LIST_KEY)) {
//                    mRecipeIngredients = intent.getParcelableArrayListExtra(RecipesActivity.INGREDIENTS_LIST_KEY);
//
//                    Log.v("SIZE", String.valueOf(mRecipeIngredients.size()));
//
//                    String ingredientList = "START: \n";
//                    for (int i = 0; i < mRecipeIngredients.size(); i++) {
//                        ingredientList = ingredientList.concat(String.valueOf(i) + ": ").concat(String.valueOf(mRecipeIngredients.get(i).getIngredientName())).concat("\n");
//                    }
//
//                    Log.v("Ingredients", ingredientList);
//                }
//
//                if (intent.hasExtra(RecipesActivity.STEPS_LIST_KEY)) {
//                    mRecipeSteps = intent.getParcelableArrayListExtra(RecipesActivity.STEPS_LIST_KEY);
//                    Log.v("STEPS SIZE", String.valueOf(mRecipeSteps.size()));
//                }
            } else {
                closeOnError(errorMsg);
            }
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.details_menu, menu);
//
//        return true;
//    }

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
}
