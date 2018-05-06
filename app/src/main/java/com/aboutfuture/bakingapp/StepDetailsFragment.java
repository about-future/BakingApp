package com.aboutfuture.bakingapp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Ingredient;
import com.aboutfuture.bakingapp.recipes.Step;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;


public class StepDetailsFragment extends Fragment implements Player.EventListener{

    private static final String VIDEO_POSITION_KEY = "position_key";

    private ArrayList<Step> mSteps;
    private ArrayList<Ingredient> mIngredients;
    private int mStepNumber;

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private String mVideoUri;
    private long mVideoPosition;

    private TextView descriptionTextView;
    private ListView ingredientsListView;
    private ImageView previousStepImageView;
    private ImageView nextStepImageView;

    public StepDetailsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(RecipesActivity.RECIPE_STEP_KEY);
            mIngredients = savedInstanceState.getParcelableArrayList(RecipesActivity.INGREDIENTS_LIST_KEY);
            mStepNumber = savedInstanceState.getInt(RecipesActivity.NUMBER_STEP_KEY);
            mVideoPosition = savedInstanceState.getLong(VIDEO_POSITION_KEY, 0);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);

        // Initialize the player view
        mPlayerView = rootView.findViewById(R.id.playerView);

        // Load the cake slice image as the background image if no image available or until video is ready.
        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                (getResources(), R.drawable.cake));

        // Get a reference to the ImageView in the fragment layout
        descriptionTextView = rootView.findViewById(R.id.step_description_tv);
        ingredientsListView = rootView.findViewById(R.id.ingredients_list_view);

        // TODO: hide buttons if twopane is on
        previousStepImageView = rootView.findViewById(R.id.previous_step);
        nextStepImageView = rootView.findViewById(R.id.next_step);

        if (mSteps != null) {
            if (!TextUtils.isEmpty(mSteps.get(mStepNumber).getVideoURL())) {
                mPlayerView.setVisibility(View.VISIBLE);
                // Show and initialize the player if there is a video url available
                initializePlayer(
                        getContext(),
                        Uri.parse(mSteps.get(mStepNumber).getVideoURL()));
            } else {
                // Otherwise, hide player and don't initialize it
                mPlayerView.setVisibility(View.GONE);
            }

            // Hide list for ingredients
            ingredientsListView.setVisibility(View.GONE);

            // Set step description
            descriptionTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setText(mSteps.get(mStepNumber).getDescription());
            Log.v("mSteps", String.valueOf(mSteps.size()));
        }

        if (mIngredients != null) {
            mPlayerView.setVisibility(View.GONE);
            ingredientsListView.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.GONE);

            IngredientsAdapter mAdapter = new IngredientsAdapter(getContext(), mIngredients);
            ingredientsListView.setAdapter(mAdapter);
            Log.v("mIngredients", String.valueOf(mIngredients.size()));
        }

        //navigation();

        return rootView;
    }

    private void initializePlayer(Context context, Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            //mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(context, "BakingApp");
            DataSource.Factory factory = new DefaultDataSourceFactory(context, userAgent);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(factory).createMediaSource(mediaUri);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            if (mVideoPosition != 0) {
                mExoPlayer.seekTo(mVideoPosition);
            }
        }
    }

    private void releasePlayer() {
        //TODO: mNotificationManager.cancelAll();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        //mMediaSession.setActive(false);
    }

    private void navigation() {
        if(mStepNumber == 0) {
            previousStepImageView.setVisibility(View.INVISIBLE);
            ingredientsListView.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.GONE);

            IngredientsAdapter mAdapter = new IngredientsAdapter(getContext(), mIngredients);
            ingredientsListView.setAdapter(mAdapter);
        }

        if (mStepNumber == mSteps.size() - 1) {
            ingredientsListView.setVisibility(View.GONE);
            descriptionTextView.setVisibility(View.VISIBLE);
            nextStepImageView.setVisibility(View.INVISIBLE);
        }

        previousStepImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStepNumber > 0) {
                    mStepNumber--;
                }

                nextStepImageView.setVisibility(View.VISIBLE);

                if (mStepNumber == 0) {
                    previousStepImageView.setVisibility(View.INVISIBLE);
                    ingredientsListView.setVisibility(View.VISIBLE);
                    IngredientsAdapter mAdapter = new IngredientsAdapter(getContext(), mIngredients);
                    ingredientsListView.setAdapter(mAdapter);
                    descriptionTextView.setVisibility(View.GONE);
                } else {
                    ingredientsListView.setVisibility(View.GONE);
                    descriptionTextView.setVisibility(View.VISIBLE);
                    descriptionTextView.setText(mSteps.get(mStepNumber).getDescription());
                }
            }
        });

        nextStepImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStepNumber < mSteps.size() - 1) {
                    mStepNumber++;
                }
                previousStepImageView.setVisibility(View.VISIBLE);

                if (mStepNumber == mSteps.size() - 1) {
                    nextStepImageView.setVisibility(View.INVISIBLE);
                }

                descriptionTextView.setText(mSteps.get(mStepNumber).getDescription());
            }
        });
    }

    public void setSteps(ArrayList<Step> steps) { mSteps = steps; }
    public void setIngredients(ArrayList<Ingredient> ingredients) { mIngredients = ingredients; }
    public void setPosition(int position) { mStepNumber = position; }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(RecipesActivity.RECIPE_STEP_KEY, mSteps);
        outState.putParcelableArrayList(RecipesActivity.INGREDIENTS_LIST_KEY, mIngredients);
        outState.putInt(RecipesActivity.NUMBER_STEP_KEY, mStepNumber);
        outState.putLong(VIDEO_POSITION_KEY, mExoPlayer.getCurrentPosition());
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        if((playbackState == Player.STATE_READY) && playWhenReady){
//            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
//                    mExoPlayer.getCurrentPosition(), 1f);
//        } else if((playbackState == Player.STATE_READY)){
//            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
//                    mExoPlayer.getCurrentPosition(), 1f);
//        }
        //mMediaSession.setPlaybackState(mStateBuilder.build());

        //TODO: showNotification(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
