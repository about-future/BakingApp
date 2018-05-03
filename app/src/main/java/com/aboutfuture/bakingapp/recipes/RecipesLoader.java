package com.aboutfuture.bakingapp.recipes;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.aboutfuture.bakingapp.retrofit.ApiClient;
import com.aboutfuture.bakingapp.retrofit.ApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;

public class RecipesLoader  extends AsyncTaskLoader<ArrayList<Recipe>> {
    private ArrayList<Recipe> cachedRecipes;

    public RecipesLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (cachedRecipes == null)
            forceLoad();
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        ApiInterface recipesApiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<Recipe>> call = recipesApiInterface.getRecipes();

        ArrayList<Recipe> result = new ArrayList<>();
        try {
            //result = call.execute().body();
            result = Objects.requireNonNull(call.execute().body());
        } catch (IOException e) {
            Log.v("Recipes Loader", "Error: " + e.toString());
        }

        return result;
    }

    @Override
    public void deliverResult(ArrayList<Recipe> data) {
        cachedRecipes = data;
        super.deliverResult(data);
    }

}
