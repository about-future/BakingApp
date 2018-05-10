package com.aboutfuture.bakingapp;


import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.ButterKnife;

import static com.aboutfuture.bakingapp.data.RecipesContract.*;

public class RecipeDetailsActivity extends AppCompatActivity implements
        MasterRecipeFragment.OnStepClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INGREDIENTS_LOADER_ID = 436876;
    private static final int STEPS_LOADER_ID = 436263;

    // Query projection used to retrieve recipe ingredients
    private static final String[] INGREDIENTS_PROJECTION = {
            IngredientsEntry.COLUMN_QUANTITY,
            IngredientsEntry.COLUMN_MEASURE,
            IngredientsEntry.COLUMN_INGREDIENT_NAME
    };

    // Query projection used to retrieve recipe steps
    private static final String[] STEPS_PROJECTION = {
            StepsEntry.COLUMN_STEP_ID,
            StepsEntry.COLUMN_SHORT_DESC,
            StepsEntry.COLUMN_DESCRIPTION,
            StepsEntry.COLUMN_VIDEO_URL,
            StepsEntry.COLUMN_THUMBNAIL
    };

    private int mRecipeId;
    private String mRecipeName;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Step> mSteps;

    private MasterRecipeFragment mMasterFragment;
    private StepDetailsFragment mStepFragment;

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
                if (intent.hasExtra(RecipesActivity.RECIPE_ID_KEY)) {
                    mRecipeId = intent.getIntExtra(RecipesActivity.RECIPE_ID_KEY, 0);
                    mRecipeName = intent.getStringExtra(RecipesActivity.RECIPE_NAME_KEY);
                    setTitle(mRecipeName);

                    getSupportLoaderManager().restartLoader(INGREDIENTS_LOADER_ID, null, this);
                }
            } else {
                closeOnError(errorMsg);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(RecipesActivity.RECIPE_ID_KEY, mRecipeId);
        outState.putString(RecipesActivity.RECIPE_NAME_KEY, mRecipeName);
        outState.putParcelableArrayList(RecipesActivity.RECIPE_INGREDIENTS_KEY, mIngredients);
        outState.putParcelableArrayList(RecipesActivity.RECIPE_STEPS_KEY, mSteps);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RecipesActivity.RECIPE_ID_KEY))
                mRecipeId = savedInstanceState.getInt(RecipesActivity.RECIPE_ID_KEY);

            if (savedInstanceState.containsKey(RecipesActivity.RECIPE_NAME_KEY)) {
                mRecipeName = savedInstanceState.getString(RecipesActivity.RECIPE_NAME_KEY);
                setTitle(mRecipeName);
            }

            if (savedInstanceState.containsKey(RecipesActivity.RECIPE_INGREDIENTS_KEY))
                mIngredients = savedInstanceState.getParcelableArrayList(RecipesActivity.RECIPE_INGREDIENTS_KEY);

            if (savedInstanceState.containsKey(RecipesActivity.RECIPE_STEPS_KEY))
                mSteps = savedInstanceState.getParcelableArrayList(RecipesActivity.RECIPE_STEPS_KEY);
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
            newFragment.setSteps(mSteps);
            newFragment.setPosition(position);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_details_container, newFragment)
                    .commit();

        } else {
            Intent intent = new Intent(this, StepDetailsActivity.class);
            intent.putExtra(RecipesActivity.RECIPE_STEPS_KEY, mSteps);
            intent.putExtra(RecipesActivity.NUMBER_STEP_KEY, position);
            startActivity(intent);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        switch (loaderId) {
            case INGREDIENTS_LOADER_ID:
                return new CursorLoader(
                        getApplicationContext(),
                        buildUriWithId(IngredientsEntry.CONTENT_URI, mRecipeId),
                        INGREDIENTS_PROJECTION,
                        null,
                        null,
                        null);

            case STEPS_LOADER_ID:
                return new CursorLoader(
                        getApplicationContext(),
                        buildUriWithId(StepsEntry.CONTENT_URI, mRecipeId),
                        STEPS_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case INGREDIENTS_LOADER_ID:
                if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
                    // Find the columns of ingredients attributes
                    int quantityColumnIndex = cursor.getColumnIndex(IngredientsEntry.COLUMN_QUANTITY);
                    int measureColumnIndex = cursor.getColumnIndex(IngredientsEntry.COLUMN_MEASURE);
                    int nameColumnIndex = cursor.getColumnIndex(IngredientsEntry.COLUMN_INGREDIENT_NAME);

                    // Recreate recipe ingredients
                    mIngredients = new ArrayList<>();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        // Set the extracted value from the Cursor for the given column index and use each
                        // value to create an Ingredient object
                        mIngredients.add(new Ingredient(
                                cursor.getDouble(quantityColumnIndex),
                                cursor.getString(measureColumnIndex),
                                cursor.getString(nameColumnIndex)));
                    }

//                    if (!cursor.isClosed()) {
                        cursor.close();
//                    }

                    Log.v("INGREDIENTS SIZE", String.valueOf(mIngredients.size()));

                    // Get recipe steps
                    getSupportLoaderManager().restartLoader(STEPS_LOADER_ID, null, this);

                    // Populate ingredients section
                    //mMasterFragment.setIngredients(mIngredients);
                }
                break;

            case STEPS_LOADER_ID:
                if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
                    // Find the columns of ingredients attributes
                    int stepIdColumnIndex = cursor.getColumnIndex(StepsEntry.COLUMN_STEP_ID);
                    int shortDescColumnIndex = cursor.getColumnIndex(StepsEntry.COLUMN_SHORT_DESC);
                    int descriptionColumnIndex = cursor.getColumnIndex(StepsEntry.COLUMN_DESCRIPTION);
                    int videoUrlColumnIndex = cursor.getColumnIndex(StepsEntry.COLUMN_VIDEO_URL);
                    int thumbnailUrlColumnIndex = cursor.getColumnIndex(StepsEntry.COLUMN_THUMBNAIL);

                    // Recreate recipe steps
                    mSteps = new ArrayList<>();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        // Set the extracted value from the Cursor for the given column index and use each
                        // value to create a Step object
                        mSteps.add(new Step(
                                cursor.getInt(stepIdColumnIndex),
                                cursor.getString(shortDescColumnIndex),
                                cursor.getString(descriptionColumnIndex),
                                cursor.getString(videoUrlColumnIndex),
                                cursor.getString(thumbnailUrlColumnIndex)));
                    }

                    if (!cursor.isClosed()) {
                        cursor.close();
                    }

                    Log.v("INGREDIENTS 2 SIZE", String.valueOf(mIngredients.size()));
                    Log.v("STEPS SIZE", String.valueOf(mSteps.size()));

                    // Populate steps section
                    //mMasterFragment.setSteps(mSteps);
                    //mStepFragment.setSteps(mSteps);

                    FragmentManager fragmentManager = getSupportFragmentManager();

                    mMasterFragment = new MasterRecipeFragment();
                    mMasterFragment.setIngredients(mIngredients);
                    mMasterFragment.setSteps(mSteps);
                    fragmentManager.beginTransaction()
                            .add(R.id.master_container, mMasterFragment)
                            .commit();

                    if (findViewById(R.id.step_details_container) != null) {
                        mTwoPane = true;

                        mStepFragment = new StepDetailsFragment();
                        mStepFragment.setSteps(mSteps);
                        fragmentManager.beginTransaction()
                                .add(R.id.step_details_container, mStepFragment)
                                .commit();

                        LinearLayout navigationButtons = findViewById(R.id.navigation_layout);
                        navigationButtons.setVisibility(View.GONE);
                    } else {
                        mTwoPane = false;
                    }

                    Log.v("TWO PANE", String.valueOf(mTwoPane));

                }
                break;

            default:
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
