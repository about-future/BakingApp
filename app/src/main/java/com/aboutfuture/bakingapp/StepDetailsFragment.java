package com.aboutfuture.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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


public class StepDetailsFragment extends Fragment implements Player.EventListener {

    private static final String VIDEO_POSITION_KEY = "position_key";

    private ArrayList<Step> mSteps;
    private int mStepNumber;

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private String mVideoUri;
    private long mVideoPosition;

    private ImageView previousStepImageView;
    private ImageView nextStepImageView;

    public StepDetailsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(RecipesActivity.RECIPE_STEP_KEY);
            mStepNumber = savedInstanceState.getInt(RecipesActivity.NUMBER_STEP_KEY);
            mVideoPosition = savedInstanceState.getLong(VIDEO_POSITION_KEY, 0);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);

        // Get a reference to the player view
        mPlayerView = rootView.findViewById(R.id.playerView);

        // Load the cake slice image as the background image if no image available or until video is ready.
        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                (getResources(), R.drawable.cake));

        // Initialize the Media Session.
        initializeMediaSession(getContext());

        // Get a reference to the ImageView in the fragment layout
        final TextView descriptionTextView = rootView.findViewById(R.id.step_description_tv);
        // Set step description
        descriptionTextView.setText(mSteps.get(mStepNumber).getDescription());

        // Set activity title
        if (getActivity() != null)
            getActivity().setTitle(mSteps.get(mStepNumber).getShortDescription());

        if (!TextUtils.isEmpty(mSteps.get(mStepNumber).getVideoURL())) {
            // Show player
            mPlayerView.setVisibility(View.VISIBLE);
            // Release player
            releasePlayer();
            // Initialize the player if there is a video url available
            initializePlayer(
                    getContext(),
                    Uri.parse(mSteps.get(mStepNumber).getVideoURL()));
        } else {
            // Otherwise, hide player and don't initialize it
            mPlayerView.setVisibility(View.GONE);
        }

        previousStepImageView = rootView.findViewById(R.id.previous_step);
        nextStepImageView = rootView.findViewById(R.id.next_step);

        if (mStepNumber == 0) {
            previousStepImageView.setVisibility(View.INVISIBLE);
        }

        if (mStepNumber == mSteps.size() - 1) {
            nextStepImageView.setVisibility(View.INVISIBLE);
        }

        previousStepImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStepNumber > 0) {
                    mStepNumber--;
                }

                nextStepImageView.setVisibility(View.VISIBLE);
                if (mStepNumber == 0) {
                    previousStepImageView.setVisibility(View.INVISIBLE);
                }

                // Set step description
                descriptionTextView.setText(mSteps.get(mStepNumber).getDescription());

                // Reset video position
                mVideoPosition = 0;
                // Release player
                releasePlayer();

                if (!TextUtils.isEmpty(mSteps.get(mStepNumber).getVideoURL())) {
                    // Show player
                    mPlayerView.setVisibility(View.VISIBLE);
                    // Initialize the player if there is a video url available
                    initializePlayer(
                            getContext(),
                            Uri.parse(mSteps.get(mStepNumber).getVideoURL()));
                } else {
                    // Otherwise, hide player and don't initialize it
                    mPlayerView.setVisibility(View.GONE);
                }

                // Set activity title
                if (getActivity() != null)
                    getActivity().setTitle(mSteps.get(mStepNumber).getShortDescription());
            }
        });

        nextStepImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStepNumber < mSteps.size() - 1) {
                    mStepNumber++;
                }

                previousStepImageView.setVisibility(View.VISIBLE);
                if (mStepNumber == mSteps.size() - 1) {
                    nextStepImageView.setVisibility(View.INVISIBLE);
                }

                // Set step description
                descriptionTextView.setText(mSteps.get(mStepNumber).getDescription());

                // Reset video position
                mVideoPosition = 0;
                // Release player
                releasePlayer();

                if (!TextUtils.isEmpty(mSteps.get(mStepNumber).getVideoURL())) {
                    // Show player
                    mPlayerView.setVisibility(View.VISIBLE);
                    // Initialize the player if there is a video url available
                    initializePlayer(
                            getContext(),
                            Uri.parse(mSteps.get(mStepNumber).getVideoURL()));
                } else {
                    // Otherwise, hide player and don't initialize it
                    mPlayerView.setVisibility(View.GONE);
                }

                // Set activity title
                if (getActivity() != null)
                    getActivity().setTitle(mSteps.get(mStepNumber).getShortDescription());
            }
        });

        return rootView;
    }

    private void initializePlayer(Context context, Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

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

    private void initializeMediaSession(Context context) {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(context, "BakingApp");

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void releasePlayer() {
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
        mMediaSession.setActive(false);
    }

    public void setSteps(ArrayList<Step> steps) {
        mSteps = steps;
    }

    public void setPosition(int position) {
        mStepNumber = position;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(RecipesActivity.RECIPE_STEP_KEY, mSteps);
        outState.putInt(RecipesActivity.NUMBER_STEP_KEY, mStepNumber);
        if (mExoPlayer != null) {
            outState.putLong(VIDEO_POSITION_KEY, mExoPlayer.getCurrentPosition());
        }
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
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
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

    // Media Session Callbacks, where all external clients control the player.
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
