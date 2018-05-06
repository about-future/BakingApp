package com.aboutfuture.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.RecipesAdapter;
import com.aboutfuture.bakingapp.recipes.RecipesLoader;
import com.aboutfuture.bakingapp.utils.NetworkUtils;
import com.aboutfuture.bakingapp.utils.ScreenUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesActivity extends AppCompatActivity implements
        RecipesAdapter.GridItemClickListener,
        LoaderManager.LoaderCallbacks {

    private static final int RECIPES_LOADER_ID = 534;
    private static final String RECIPES_LIST_KEY = "recipes_list";
    private static final String POSITION_KEY = "current_position";

    public static final String RECIPE_KEY = "clicked_recipe";
    public static final String RECIPE_ID_KEY = "recipe_id";
    public static final String RECIPE_NAME_KEY = "recipe_name";
    public static final String INGREDIENTS_LIST_KEY = "ingredients_list";
    public static final String STEPS_LIST_KEY = "steps_list";
    public static final String RECIPE_STEP_KEY = "recipe_step";
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
            fetchRecipes(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(RECIPES_LIST_KEY, mRecipes);

        outState.putInt(POSITION_KEY, mGridLayoutManager.findFirstVisibleItemPosition());
        //outState.putParcelable(POSITION_KEY, mGridLayoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPES_LIST_KEY)) {
                mRecipes = savedInstanceState.getParcelableArrayList(RECIPES_LIST_KEY);

                if (mRecipes != null) {
                    showRecipes();
                    //mRecipesAdapter = new RecipesAdapter(this, this);
                    //mRecipesRecyclerView.setAdapter(mRecipesAdapter);
                    mRecipesAdapter.swapRecipes(mRecipes);
                } else {
                    fetchRecipes(this);
                }
            }

            if (savedInstanceState.containsKey(POSITION_KEY)) {
                //mGridLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(POSITION_KEY));
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
            getSupportLoaderManager().restartLoader(RECIPES_LOADER_ID, null, this);
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
    public void onGridItemClick(Recipe recipeClicked) {
        Intent recipeDetailsIntent = new Intent(RecipesActivity.this, RecipeDetailsActivity.class);
//        recipeDetailsIntent.putExtra(RecipesActivity.RECIPE_ID_KEY, recipeClicked.getId());
//        recipeDetailsIntent.putExtra(RecipesActivity.RECIPE_NAME_KEY, recipeClicked.getName());
//        recipeDetailsIntent.putParcelableArrayListExtra(INGREDIENTS_LIST_KEY, recipeClicked.getIngredients());
//        recipeDetailsIntent.putParcelableArrayListExtra(STEPS_LIST_KEY, recipeClicked.getSteps());
        recipeDetailsIntent.putExtra(RECIPE_KEY, recipeClicked);
        startActivity(recipeDetailsIntent);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case RECIPES_LOADER_ID:
                // If the loaded id matches recipes loader, return a new recipes loader
                return new RecipesLoader(getApplicationContext());

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        if (data != null) {
            showRecipes();
        } else {
            //showNoResults();
        }

        mRecipes = (ArrayList<Recipe>) data;
        mRecipesAdapter.swapRecipes((ArrayList<Recipe>) data);

        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
            mRecipesRecyclerView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
