package com.aboutfuture.bakingapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class RecipesContract {

    private RecipesContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.aboutfuture.bakingapp";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /* Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.aboutfuture.bakingapp/recipes/ is a valid path for
     * looking at movie data. content://com.aboutfuture.bakingapp/videos/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "videos".
     */
    public static final String PATH_RECIPES = "recipes";

    // Inner class that defines constant values for the recipes database table.
    // Each entry in the table represents a single recipe.
    public static abstract class RecipesEntry implements BaseColumns {
        // The content URI to access the recipe data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_RECIPES);
        // The MIME type of the {@link #CONTENT_URI} for a list of recipes.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPES;
        // The MIME type of the {@link #CONTENT_URI} for a single recipe.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPES;

        public final static String TABLE_NAME =                 "recipes";
        public final static String _ID = BaseColumns._ID;                           // Type: INTEGER (Unique ID)
        public final static String COLUMN_RECIPE_ID =           "recipe_id";
        public final static String COLUMN_NAME =                "name";
        public final static String COLUMN_INGREDIENTS =         "poster_path";
        public final static String COLUMN_STEPS =               "backdrop_path";
        public final static String COLUMN_SERVINGS =            "servings";
        public final static String COLUMN_IMAGE =               "image";
    }

    // Inner class that defines constant values for the recipe ingredients database table.
    // Each entry in the table represents a single ingredient.
    public static final String PATH_INGREDIENTS = "ingredients";

    public static abstract class IngredientsEntry implements BaseColumns {
        // The content URI to access the section data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INGREDIENTS);
        // The MIME type of the {@link #CONTENT_URI} for a list of ingredients.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;
        // The MIME type of the {@link #CONTENT_URI} for a single ingredient.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INGREDIENTS;

        public final static String TABLE_NAME =             "recipe_ingredients";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_RECIPE_ID =       "recipe_id";
        public final static String COLUMN_QUANTITY =        "quantity";
        public final static String COLUMN_MEASURE =         "measure";
        public final static String COLUMN_INGREDIENT_NAME = "ingredient";
    }

    // Inner class that defines constant values for the recipe steps database table.
    // Each entry in the table represents a single step.
    public static final String PATH_STEPS = "steps";

    public static abstract class StepsEntry implements BaseColumns {
        // The content URI to access the section data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STEPS);
        // The MIME type of the {@link #CONTENT_URI} for a list of ingredients.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STEPS;
        // The MIME type of the {@link #CONTENT_URI} for a single ingredient.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STEPS;

        public final static String TABLE_NAME =             "recipe_steps";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_RECIPE_ID =       "recipe_id";
        public final static String COLUMN_STEP_ID =         "step_id";
        public final static String COLUMN_SHORT_DESC =      "shortDescription";
        public final static String COLUMN_DESCRIPTION =     "description";
        public final static String COLUMN_VIDEO_URL =       "videoURL";
        public final static String COLUMN_THUMBNAIL =       "thumbnailURL";
    }
}
