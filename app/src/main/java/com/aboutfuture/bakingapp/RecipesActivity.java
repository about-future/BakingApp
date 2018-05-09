package com.aboutfuture.bakingapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.RecipesAdapter;
import com.aboutfuture.bakingapp.recipes.RecipesLoader;
import com.aboutfuture.bakingapp.utils.NetworkUtils;
import com.aboutfuture.bakingapp.utils.ScreenUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.aboutfuture.bakingapp.data.RecipesContract.*;

public class RecipesActivity extends AppCompatActivity implements
        RecipesAdapter.GridItemClickListener,
        LoaderManager.LoaderCallbacks {

    private static final int RECIPES_LOADER_ID = 534;
    private static final int DATABASE_LOADER_ID = 316;
    private static final String RECIPES_LIST_KEY = "recipes_list";
    private static final String POSITION_KEY = "current_position";

    public static final String RECIPE_ID_KEY = "recipe_id";
    public static final String RECIPE_NAME_KEY = "recipe_name";
    public static final String RECIPE_INGREDIENTS_KEY = "recipe_ingredients";
    public static final String RECIPE_STEPS_KEY = "recipe_steps";
    public static final String NUMBER_STEP_KEY = "number_step";

    @BindView(R.id.recipes_rv)
    RecyclerView mRecipesRecyclerView;
    @BindView(R.id.recipes_messages_tv)
    TextView mMessagesTextView;
    @BindView(R.id.recipes_cloud_iv)
    ImageView mNoConnectionImageView;
    @BindView(R.id.recipes_loading_pb)
    ProgressBar mLoading;
    private RecipesAdapter mRecipesAdapter;
    private GridLayoutManager mGridLayoutManager;
    private int mPosition = RecyclerView.NO_POSITION;

    private ArrayList<Recipe> mRecipes;
    private Bundle mBundleState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        ButterKnife.bind(this);

        mLoading.setVisibility(View.VISIBLE);
        // The layout manager for our RecyclerView will be a GridLayout, so we can display our movies
        // on columns. The number of columns is dictated by the orientation and size of the device
        mGridLayoutManager = new GridLayoutManager(
                this,
                ScreenUtils.getNumberOfColumns(this, 300, 1));
        mRecipesRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecipesRecyclerView.setHasFixedSize(true);
        mRecipesAdapter = new RecipesAdapter(this, this);
        mRecipesRecyclerView.setAdapter(mRecipesAdapter);

        if (savedInstanceState == null) {
            getSupportLoaderManager().initLoader(DATABASE_LOADER_ID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(RECIPES_LIST_KEY, mRecipes);
        outState.putInt(POSITION_KEY, mGridLayoutManager.findFirstVisibleItemPosition());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPES_LIST_KEY)) {
                mRecipes = savedInstanceState.getParcelableArrayList(RECIPES_LIST_KEY);

                if (mRecipes != null) {
                    showRecipes();
                    mRecipesAdapter.swapRecipes(mRecipes);
                }
            }

            if (savedInstanceState.containsKey(POSITION_KEY)) {
                mPosition = savedInstanceState.getInt(POSITION_KEY);
                if (mPosition == RecyclerView.NO_POSITION) {
                    mPosition = 0;
                }
                mRecipesRecyclerView.smoothScrollToPosition(mPosition);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBundleState = new Bundle();
        mBundleState.putParcelable(POSITION_KEY, mGridLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBundleState != null) {
            if (mBundleState.containsKey(POSITION_KEY))
                mGridLayoutManager.onRestoreInstanceState(mBundleState.getParcelable(POSITION_KEY));
        }
    }

    // Fetch data or show connection error
    private void fetchRecipes(Context context) {
        // If there is a network connection, fetch data
        if (NetworkUtils.isConnected(context)) {
            showLoading();

            //Init or restart loader
            getSupportLoaderManager().initLoader(RECIPES_LOADER_ID, null, this);
        }
        // If no connection and the loader id is not FAVOURITES_LOADER_ID
        else {
            // Hide loading indicator, hide data and display connection error message
            showError();
        }
    }

    // Show loading progress
    private void showLoading() {
        mLoading.setVisibility(View.VISIBLE);
        mMessagesTextView.setVisibility(View.VISIBLE);
        mMessagesTextView.setText(R.string.loading);
        mRecipesRecyclerView.setVisibility(View.INVISIBLE);
        mNoConnectionImageView.setVisibility(View.INVISIBLE);
    }

    // Show movie data and hide no connection icon and message
    private void showRecipes() {
        mRecipesRecyclerView.setVisibility(View.VISIBLE);
        mNoConnectionImageView.setVisibility(View.INVISIBLE);
        mMessagesTextView.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.GONE);
    }

    // Hide the movie data and loading indicator and show error message
    private void showError() {
        mRecipesRecyclerView.setVisibility(View.INVISIBLE);
        mNoConnectionImageView.setVisibility(View.VISIBLE);
        mMessagesTextView.setText(R.string.no_internet);
        mMessagesTextView.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onGridItemClick(int recipeId, String recipeName) {
        Intent recipeDetailsIntent = new Intent(RecipesActivity.this, RecipeDetailsActivity.class);
        recipeDetailsIntent.putExtra(RECIPE_ID_KEY, recipeId);
        recipeDetailsIntent.putExtra(RECIPE_NAME_KEY, recipeName);
        startActivity(recipeDetailsIntent);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case RECIPES_LOADER_ID:
                // If the loaded id matches recipes loader, return a new recipes loader
                return new RecipesLoader(getApplicationContext());

            case DATABASE_LOADER_ID:
                // If the loader id matches database loader, return a cursor loader
                return new CursorLoader(
                        getApplicationContext(),
                        RecipesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        switch (loader.getId()) {
            case RECIPES_LOADER_ID:
                mRecipes = (ArrayList<Recipe>) data;
                mRecipesAdapter.swapRecipes((ArrayList<Recipe>) data);

                if (data != null) {
                    insertRecipes((ArrayList<Recipe>) data);
                }

                if (mPosition == RecyclerView.NO_POSITION) {
                    mPosition = 0;
                    mRecipesRecyclerView.smoothScrollToPosition(mPosition);
                }

                showRecipes();

                break;

            case DATABASE_LOADER_ID:
                if (data == null || ((Cursor) data).getCount() == 0) {
                    fetchRecipes(getApplicationContext());
                } else {
                    Cursor cursor = (Cursor) data;

                    if (!cursor.isClosed()) {
                        // Recreate recipes array
                        // Find the columns of recipe attributes that we're interested in
                        int recipeIdColumnIndex = cursor.getColumnIndex(RecipesEntry.COLUMN_RECIPE_ID);
                        int nameColumnIndex = cursor.getColumnIndex(RecipesEntry.COLUMN_NAME);
                        int servingsColumnIndex = cursor.getColumnIndex(RecipesEntry.COLUMN_SERVINGS);
                        int imageColumnIndex = cursor.getColumnIndex(RecipesEntry.COLUMN_IMAGE);

                        mRecipes = new ArrayList<>();
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);
                            // Set the extracted value from the Cursor for the given column index and use each
                            // value to create a Recipe object
                            mRecipes.add(new Recipe(
                                    cursor.getInt(recipeIdColumnIndex),
                                    cursor.getString(nameColumnIndex),
                                    cursor.getInt(servingsColumnIndex),
                                    cursor.getString(imageColumnIndex)));
                        }

                        cursor.close();

                        mRecipesAdapter.swapRecipes(mRecipes);

                        // If the RecyclerView has no position, we assume the first position in the list
                        if (mPosition == RecyclerView.NO_POSITION) {
                            mPosition = 0;
                            // Scroll the RecyclerView to mPosition
                            mRecipesRecyclerView.smoothScrollToPosition(mPosition);
                        }

                        showRecipes();
                    }
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private void insertRecipes(ArrayList<Recipe> recipes) {
        for (int i = 0; i < recipes.size(); i++) {
            // Recipe insertion
            ContentValues recipeValues = new ContentValues();
            recipeValues.put(RecipesEntry.COLUMN_RECIPE_ID, recipes.get(i).getId());
            recipeValues.put(RecipesEntry.COLUMN_NAME, recipes.get(i).getName());
            recipeValues.put(RecipesEntry.COLUMN_SERVINGS, recipes.get(i).getServings());
            recipeValues.put(RecipesEntry.COLUMN_IMAGE, recipes.get(i).getImagePath());
            getContentResolver().insert(RecipesEntry.CONTENT_URI, recipeValues);

            // Ingredients insertion
            ContentValues[] allIngredientsValues = new ContentValues[mRecipes.get(i).getIngredients().size()];

            // For each ingredient, get the data and put it in ingredientValue
            for (int j = 0; j < mRecipes.get(i).getIngredients().size(); j++) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put(IngredientsEntry.COLUMN_RECIPE_ID, recipes.get(i).getId());
                ingredientValues.put(IngredientsEntry.COLUMN_QUANTITY, mRecipes.get(i).getIngredients().get(j).getQuantity());
                ingredientValues.put(IngredientsEntry.COLUMN_MEASURE, mRecipes.get(i).getIngredients().get(j).getMeasure());
                ingredientValues.put(IngredientsEntry.COLUMN_INGREDIENT_NAME, mRecipes.get(i).getIngredients().get(j).getIngredientName());

                // Add each ingredientValues to the array of values
                allIngredientsValues[j] = ingredientValues;
            }

            // If we have ingredients values to insert, insert them and update the value of ingredientsResponse
            if (allIngredientsValues.length != 0) {
                getContentResolver().bulkInsert(IngredientsEntry.CONTENT_URI, allIngredientsValues);
            }

            // Steps insertion
            ContentValues[] allStepsValues = new ContentValues[mRecipes.get(i).getSteps().size()];

            // For each step, get the data and put it in stepValue
            for (int j = 0; j < mRecipes.get(i).getSteps().size(); j++) {
                ContentValues stepValues = new ContentValues();
                stepValues.put(StepsEntry.COLUMN_RECIPE_ID, recipes.get(i).getId());
                stepValues.put(StepsEntry.COLUMN_STEP_ID, mRecipes.get(i).getSteps().get(j).getId());
                stepValues.put(StepsEntry.COLUMN_SHORT_DESC, mRecipes.get(i).getSteps().get(j).getShortDescription());
                stepValues.put(StepsEntry.COLUMN_DESCRIPTION, mRecipes.get(i).getSteps().get(j).getDescription());
                stepValues.put(StepsEntry.COLUMN_VIDEO_URL, mRecipes.get(i).getSteps().get(j).getVideoURL());
                stepValues.put(StepsEntry.COLUMN_THUMBNAIL, mRecipes.get(i).getSteps().get(j).getThumbnailURL());

                // Add each stepValues to the array of values
                allStepsValues[j] = stepValues;
            }

            // If we have steps values to insert, insert them and update the value of stepsResponse
            if (allStepsValues.length != 0) {
                getContentResolver().bulkInsert(StepsEntry.CONTENT_URI, allStepsValues);
            }
        }
    }
}
