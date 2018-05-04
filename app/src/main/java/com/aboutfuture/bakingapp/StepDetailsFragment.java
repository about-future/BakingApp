package com.aboutfuture.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Recipe;
import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;
import java.util.List;


public class StepDetailsFragment extends Fragment {

    private Recipe mRecipe;
    private int mPosition;

    public StepDetailsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRecipe = savedInstanceState.getParcelable(RecipesActivity.RECIPE_KEY);
            mPosition = savedInstanceState.getInt(RecipesActivity.POSITION_KEY);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);

        // Get a reference to the ImageView in the fragment layout
        final TextView videoUrlTextView = (TextView) rootView.findViewById(R.id.step_video_url);
        final TextView descriptionTextView = (TextView) rootView.findViewById(R.id.step_description_tv);
        final ImageView previousStepImageView = (ImageView) rootView.findViewById(R.id.previous_step);
        final ImageView nextStepImageView = (ImageView) rootView.findViewById(R.id.next_step);

        if (mRecipe != null) {
            if (mPosition > 0 && mPosition < mRecipe.getSteps().size() + 1) {
                videoUrlTextView.setText(mRecipe.getSteps().get(mPosition - 1).getVideoURL());
                descriptionTextView.setText(mRecipe.getSteps().get(mPosition - 1).getDescription());
            }

            if(mPosition == 0) {
                previousStepImageView.setVisibility(View.INVISIBLE);
                videoUrlTextView.setVisibility(View.GONE);
                descriptionTextView.setText(mRecipe.getIngredients().get(0).getIngredientName());
            }

            if (mPosition == mRecipe.getSteps().size()) {
                nextStepImageView.setVisibility(View.INVISIBLE);
            }

            previousStepImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mPosition > 0) {
                        mPosition--;
                    }

                    nextStepImageView.setVisibility(View.VISIBLE);

                    if (mPosition == 0) {
                        videoUrlTextView.setVisibility(View.GONE);
                        previousStepImageView.setVisibility(View.INVISIBLE);
                        descriptionTextView.setText(mRecipe.getIngredients().get(0).getIngredientName());
                    } else {
                        videoUrlTextView.setVisibility(View.VISIBLE);
                        videoUrlTextView.setText(mRecipe.getSteps().get(mPosition - 1).getVideoURL());
                        descriptionTextView.setText(mRecipe.getSteps().get(mPosition - 1).getDescription());
                    }
                }
            });

            nextStepImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mPosition < mRecipe.getSteps().size() + 1) {
                        mPosition++;
                    }

                    videoUrlTextView.setVisibility(View.VISIBLE);
                    previousStepImageView.setVisibility(View.VISIBLE);

                    if (mPosition == mRecipe.getSteps().size()) {
                        nextStepImageView.setVisibility(View.INVISIBLE);
                    }

                    videoUrlTextView.setText(mRecipe.getSteps().get(mPosition - 1).getVideoURL());
                    descriptionTextView.setText(mRecipe.getSteps().get(mPosition - 1).getDescription());
                }
            });
        }

        return rootView;
    }

    public void setSteps(Recipe recipe) {
        mRecipe = recipe;
    }
    public void setPosition(int position) { mPosition = position; }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(RecipesActivity.RECIPE_KEY, mRecipe);
        outState.putInt(RecipesActivity.POSITION_KEY, mPosition);
    }
}
