package com.aboutfuture.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;

import static com.aboutfuture.bakingapp.data.RecipesContract.*;
import static com.aboutfuture.bakingapp.data.RecipesContract.CONTENT_AUTHORITY;
import static com.aboutfuture.bakingapp.data.RecipesContract.PATH_INGREDIENTS;
import static com.aboutfuture.bakingapp.data.RecipesContract.PATH_RECIPES;
import static com.aboutfuture.bakingapp.data.RecipesContract.PATH_STEPS;

public class RecipesProvider extends ContentProvider {

    // The instance of subclass FavouritesDbHelper of SQLiteOpenHelper, will be used to access our database.
    private RecipesDbHelper mDbHelper;

    // URI matcher code for the content URI for the recipes table
    private static final int RECIPES = 100;
    // URI matcher code for the content URI for a single recipe in the recipes table
    private static final int RECIPE_ID = 101;

    // URI matcher code for the content URI for the cast table
    private static final int INGREDIENTS = 200;
    // URI matcher code for the content URI for a single actor in the cast table
    private static final int INGREDIENT_ID = 201;

    // URI matcher code for the content URI for the steps table
    private static final int STEPS = 300;
    // URI matcher code for the content URI for a single step in the steps table
    private static final int STEP_ID = 301;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_RECIPES, RECIPES);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_RECIPES + "/#", RECIPE_ID);

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INGREDIENTS, INGREDIENTS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INGREDIENTS + "/#", INGREDIENT_ID);

        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_STEPS, STEPS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_STEPS + "/#", STEP_ID);
    }

    @Override
    public boolean onCreate() {
        // Instantiate our subclass of SQLiteOpenHelper and pass the context, which is the current activity.
        mDbHelper = new RecipesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        switch (sUriMatcher.match(uri)) {
            case RECIPES:
                cursor = database.query(RecipesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case RECIPE_ID:
                selection = RecipesEntry.COLUMN_RECIPE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the recipes table where the recipe_id equals the passed
                // id and return a Cursor containing that row of the table
                cursor = database.query(RecipesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case INGREDIENTS:
                cursor = database.query(IngredientsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case INGREDIENT_ID:
                selection = IngredientsEntry.COLUMN_RECIPE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query on the ingredients table where the recipe_id equals (i.e. 5)
                // to return a Cursor containing those rows of the table.
                cursor = database.query(IngredientsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case STEPS:
                cursor = database.query(StepsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case STEP_ID:
                selection = StepsEntry.COLUMN_RECIPE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StepsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown Uri " + uri);
        }

        // Set notification URI on the Cursor, so we know what content URI the Cursor was created for.
        // If the data in this URI changes, than we know we need to update the Cursor.
        if (getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case RECIPES:
                return RecipesEntry.CONTENT_LIST_TYPE;
            case RECIPE_ID:
                return RecipesEntry.CONTENT_ITEM_TYPE;
            case INGREDIENTS:
                return IngredientsEntry.CONTENT_LIST_TYPE;
            case INGREDIENT_ID:
                return IngredientsEntry.CONTENT_ITEM_TYPE;
            case STEPS:
                return StepsEntry.CONTENT_LIST_TYPE;
            case STEP_ID:
                return StepsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + sUriMatcher.match(uri));
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        switch (sUriMatcher.match(uri)) {
            case RECIPES:
                return insertFavourite(RecipesEntry.TABLE_NAME, uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Insert a recipe into the database with the given content values.
    // Return the new content URI for that specific row in the database.
    private Uri insertFavourite(String tableName, Uri uri, ContentValues contentValues) {
        // Get writable database and insert the given values
        long id = mDbHelper.getWritableDatabase().insert(tableName, null, contentValues);
        // Notify all listeners that the data has changed
        if (getContext() != null && id > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        // Return the id appended to the uri
        return ContentUris.withAppendedId(uri, id);
    }

    // Handles requests to insert a set of new rows in a selected table. In this app, we are going
    // to be inserting multiple rows of ingredients or recipe steps
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (sUriMatcher.match(uri)) {
            case INGREDIENTS:
                return bulkInsertData(IngredientsEntry.TABLE_NAME, uri, values);

            case STEPS:
                return bulkInsertData(StepsEntry.TABLE_NAME, uri, values);

            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int bulkInsertData (String tableName, Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.beginTransaction();
        int rowsInserted = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (getContext() != null && rowsInserted > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsInserted;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
