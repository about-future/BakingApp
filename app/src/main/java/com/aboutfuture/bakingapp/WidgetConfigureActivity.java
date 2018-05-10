package com.aboutfuture.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aboutfuture.bakingapp.data.RecipesContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WidgetConfigureActivity extends AppCompatActivity implements
        WidgetConfigureAdapter.RecipeClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DATABASE_LOADER_ID = 416;

    private static final String[] RECIPES_PROJECTION = {
            RecipesContract.RecipesEntry.COLUMN_RECIPE_ID,
            RecipesContract.RecipesEntry.COLUMN_NAME
    };

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.recipes_config_rv)
    RecyclerView mRecyclerView;
    private WidgetConfigureAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_configure);
        ButterKnife.bind(this);

        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WidgetConfigureAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(DATABASE_LOADER_ID, null, this);
    }

    @Override
    public void onItemClick(int recipeId, String recipeName) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WidgetConfigureActivity.this);
        WidgetIngredientsProvider.updateAppWidget(WidgetConfigureActivity.this, appWidgetManager, appWidgetId, recipeId, recipeName);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case DATABASE_LOADER_ID:
                // If the loader id matches database loader, return a cursor loader
                return new CursorLoader(
                        getApplicationContext(),
                        RecipesContract.RecipesEntry.CONTENT_URI,
                        RECIPES_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
