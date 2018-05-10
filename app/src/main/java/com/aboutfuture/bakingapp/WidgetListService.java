package com.aboutfuture.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.aboutfuture.bakingapp.data.RecipesContract;

import java.util.Objects;

public class WidgetListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int id = Integer.valueOf(Objects.requireNonNull(intent.getData()).getSchemeSpecificPart());
        return new ListViewsFactory(getApplicationContext(), id);
    }

    class ListViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        Context mContext;
        int mRecipeId;
        Cursor mCursor;

        ListViewsFactory(Context context, int recipeId) {
            mContext = context;
            mRecipeId = recipeId;
        }

        @Override
        public void onCreate() {
            Uri uri = RecipesContract.IngredientsEntry.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(mRecipeId))
                    .build();

            mCursor = mContext.getContentResolver().query(
                    uri,
                    null,
                    null,
                    null,
                    null);
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            if (mCursor != null)
                mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (mCursor == null || mCursor.getCount() == 0) return null;

            mCursor.moveToPosition(position);
            int quantityColumnIndex = mCursor.getColumnIndex(RecipesContract.IngredientsEntry.COLUMN_QUANTITY);
            int measureColumnIndex = mCursor.getColumnIndex(RecipesContract.IngredientsEntry.COLUMN_MEASURE);
            int nameColumnIndex = mCursor.getColumnIndex(RecipesContract.IngredientsEntry.COLUMN_INGREDIENT_NAME);

            double quantity = mCursor.getDouble(quantityColumnIndex);
            String stringQuantity;
            if (quantity - (int)quantity != 0) {
                stringQuantity = String.valueOf(quantity);
            } else {
                stringQuantity = String.valueOf((int) quantity);
            }
            String measure = mCursor.getString(measureColumnIndex);
            String ingredientName = mCursor.getString(nameColumnIndex);

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredients_list_item);
            views.setTextViewText(R.id.widget_quantity_tv, stringQuantity);
            views.setTextViewText(R.id.widget_measure_tv, measure);
            views.setTextViewText(R.id.widget_ingredient_name_tv, ingredientName);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
