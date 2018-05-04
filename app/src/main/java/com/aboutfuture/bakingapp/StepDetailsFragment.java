package com.aboutfuture.bakingapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Step;

import java.util.ArrayList;


public class StepDetailsFragment extends Fragment {

    private ArrayList<Step> mSteps;
    private int mPosition;

    public StepDetailsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(RecipesActivity.RECIPE_STEP_KEY);
            mPosition = savedInstanceState.getInt(RecipesActivity.POSITION_KEY);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);

        // Get a reference to the ImageView in the fragment layout
        final TextView videoUrlTextView = (TextView) rootView.findViewById(R.id.step_video_url);
        final TextView descriptionTextView = (TextView) rootView.findViewById(R.id.step_description_tv);

        final ImageView previousStepImageView = (ImageView) rootView.findViewById(R.id.previous_step);
        final ImageView nextStepImageView = (ImageView) rootView.findViewById(R.id.next_step);

        if (mSteps != null) {
            videoUrlTextView.setText(mSteps.get(mPosition).getVideoURL());
            descriptionTextView.setText(mSteps.get(mPosition).getDescription());

            if(mPosition == 0) {
                previousStepImageView.setVisibility(View.INVISIBLE);
                videoUrlTextView.setVisibility(View.GONE);
                descriptionTextView.setText("Tura bura"); //mRecipe.getIngredients().get(0).getIngredientName());
            }

            if (mPosition == mSteps.size() - 1) {
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
                        descriptionTextView.setText("Tura bura 2");//mRecipe.getIngredients().get(0).getIngredientName());
                    } else {
                        videoUrlTextView.setVisibility(View.VISIBLE);
                        videoUrlTextView.setText(mSteps.get(mPosition).getVideoURL());
                        descriptionTextView.setText(mSteps.get(mPosition).getDescription());
                    }
                }
            });

            nextStepImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mPosition < mSteps.size() - 1) {
                        mPosition++;
                    }

                    videoUrlTextView.setVisibility(View.VISIBLE);
                    previousStepImageView.setVisibility(View.VISIBLE);

                    if (mPosition == mSteps.size() - 1) {
                        nextStepImageView.setVisibility(View.INVISIBLE);
                    }

                    videoUrlTextView.setText(mSteps.get(mPosition).getVideoURL());
                    descriptionTextView.setText(mSteps.get(mPosition).getDescription());
                }
            });
        }

        return rootView;
    }

    public void setSteps(ArrayList<Step> steps) { mSteps = steps; }
    public void setPosition(int position) { mPosition = position; }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(RecipesActivity.RECIPE_STEP_KEY, mSteps);
        outState.putInt(RecipesActivity.POSITION_KEY, mPosition);
    }
}
