package com.aboutfuture.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetIngredientsProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, int recipeId, String recipeName) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_list_widget);

        // Set widget title and create an intent to launch RecipeDetailsActivity
        Intent intent = new Intent(context, RecipeDetailsActivity.class);
        intent.putExtra(RecipesActivity.RECIPE_ID_KEY, recipeId);
        intent.putExtra(RecipesActivity.RECIPE_NAME_KEY, recipeName);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_title_tv, pendingIntent);
        views.setTextViewText(R.id.widget_title_tv, recipeName.concat(" ").concat(context.getString(R.string.ingredients)));

        // Set the list of ingredients for the selected recipe
        Intent adapterIntent = new Intent(context, WidgetListService.class);
        adapterIntent.setData(Uri.fromParts("content", String.valueOf(recipeId), null));
        views.setRemoteAdapter(R.id.widget_list_view, adapterIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

