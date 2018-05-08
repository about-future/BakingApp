package com.aboutfuture.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Recipe;

import static com.aboutfuture.bakingapp.data.RecipesContract.*;

public class RecipesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "recipes.db";

    public RecipesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Recipes Table
    private static final String SQL_CREATE_RECIPES_TABLE =
            "CREATE TABLE " + RecipesEntry.TABLE_NAME       + " ("                                      +
                    RecipesEntry._ID                         + " INTEGER PRIMARY KEY AUTOINCREMENT, "    +
                    RecipesEntry.COLUMN_RECIPE_ID             + " INTEGER NOT NULL, "                     +
                    RecipesEntry.COLUMN_NAME            + " TEXT, "                                 +
                    //RecipesEntry.COLUMN_INGREDIENTS + " BLOB, " +
                    //RecipesEntry.COLUMN_STEPS + " BLOB, " +
                    RecipesEntry.COLUMN_SERVINGS           + " TEXT, "                                 +
                    RecipesEntry.COLUMN_IMAGE           + " TEXT);";

    private static final String SQL_DELETE_RECIPES_ENTRIES =
            "DROP TABLE IF EXISTS " + RecipesEntry.TABLE_NAME;

    // Ingredients Table
    private static final String SQL_CREATE_INGREDIENTS_TABLE =
            "CREATE TABLE " + IngredientsEntry.TABLE_NAME       + " ("                                      +
                    IngredientsEntry._ID                         + " INTEGER PRIMARY KEY AUTOINCREMENT, "    +
                    IngredientsEntry.COLUMN_RECIPE_ID             + " INTEGER NOT NULL, "                     +
                    IngredientsEntry.COLUMN_QUANTITY            + " TEXT, "                                 +
                    IngredientsEntry.COLUMN_MEASURE           + " TEXT, "                                 +
                    IngredientsEntry.COLUMN_INGREDIENT_NAME           + " TEXT);";

    private static final String SQL_DELETE_INGREDIENTS_ENTRIES =
            "DROP TABLE IF EXISTS " + IngredientsEntry.TABLE_NAME;

    // Steps Table
    private static final String SQL_CREATE_STEPS_TABLE =
            "CREATE TABLE " + StepsEntry.TABLE_NAME       + " ("                                      +
                    StepsEntry._ID                         + " INTEGER PRIMARY KEY AUTOINCREMENT, "    +
                    StepsEntry.COLUMN_RECIPE_ID             + " INTEGER NOT NULL, "                     +
                    StepsEntry.COLUMN_STEP_ID            + " TEXT, "                                 +
                    StepsEntry.COLUMN_SHORT_DESC           + " TEXT, "                                 +
                    StepsEntry.COLUMN_DESCRIPTION           + " TEXT, "                                 +
                    StepsEntry.COLUMN_VIDEO_URL           + " TEXT, "                                 +
                    StepsEntry.COLUMN_THUMBNAIL           + " TEXT);";
//TODO: Check all tables
    private static final String SQL_DELETE_STEPS_ENTRIES =
            "DROP TABLE IF EXISTS " + StepsEntry.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
