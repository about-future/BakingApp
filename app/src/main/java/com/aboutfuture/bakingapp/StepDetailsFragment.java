package com.aboutfuture.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aboutfuture.bakingapp.recipes.Step;
import com.aboutfuture.bakingapp.utils.ScreenUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
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

import butterknife.BindView;
import butterknife.ButterKnife;


public class StepDetailsFragment extends Fragment implements Player.EventListener {

    private static final String VIDEO_POSITION_KEY = "position_key";
    private static final String VIDEO_PLAY_STATE_KEY = "playing_state";

    private ArrayList<Step> mSteps;
    private int mStepNumber;

    private SimpleExoPlayer mExoPlayer;

    @BindView(R.id.playerView)
    PlayerView mPlayerView;
    @BindView(R.id.step_description_tv)
    TextView descriptionTextView;

    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private long mVideoPosition;
    private boolean mVideoPlayState;

    @BindView(R.id.previous_step)
    LinearLayout previousStepLayout;
    @BindView(R.id.next_step)
    LinearLayout nextStepLayout;

    public StepDetailsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(RecipesActivity.RECIPE_STEPS_KEY);
            mStepNumber = savedInstanceState.getInt(RecipesActivity.NUMBER_STEP_KEY);
            mVideoPosition = savedInstanceState.getLong(VIDEO_POSITION_KEY, 0);
            mVideoPlayState = savedInstanceState.getBoolean(VIDEO_PLAY_STATE_KEY, true);
        }

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);
        // Bind the views
        ButterKnife.bind(this, rootView);

        //TODO: thumbnail case
        String thumbnailUrl;
        if (!TextUtils.isEmpty(mSteps.get(mStepNumber).getThumbnailURL())) {
            thumbnailUrl = mSteps.get(mStepNumber).getThumbnailURL();
        }
        // Set a background image until video is ready
        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.baking));

        //TODO: onPause and onResume (pause video and save position)

        // Initialize the Media Session.
        initializeMediaSession(getContext());

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

        if (mStepNumber == 0) {
            previousStepLayout.setVisibility(View.INVISIBLE);
        }

        if (mStepNumber == mSteps.size() - 1) {
            nextStepLayout.setVisibility(View.INVISIBLE);
        }

        previousStepLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStepNumber > 0) {
                    mStepNumber--;
                }

                nextStepLayout.setVisibility(View.VISIBLE);
                if (mStepNumber == 0) {
                    previousStepLayout.setVisibility(View.INVISIBLE);
                }

                // Set step description
                descriptionTextView.setText(mSteps.get(mStepNumber).getDescription());

                // Stop player and reset video position
                if (mExoPlayer != null) {
                    mVideoPosition = 0;
                    mExoPlayer.stop();
                }

                if (!TextUtils.isEmpty(mSteps.get(mStepNumber).getVideoURL())) {
                    // Show player
                    mPlayerView.setVisibility(View.VISIBLE);
                    // Initialize the player if there is a video url available
                    initializePlayer(
                            getContext(),
                            Uri.parse(mSteps.get(mStepNumber).getVideoURL()));
                } else {
                    // Release player
                    releasePlayer();
                    // Otherwise, hide player and don't initialize it
                    mPlayerView.setVisibility(View.GONE);
                }

                // Set activity title
                if (getActivity() != null)
                    getActivity().setTitle(mSteps.get(mStepNumber).getShortDescription());
            }
        });

        nextStepLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStepNumber < mSteps.size() - 1) {
                    mStepNumber++;
                }

                previousStepLayout.setVisibility(View.VISIBLE);
                if (mStepNumber == mSteps.size() - 1) {
                    nextStepLayout.setVisibility(View.INVISIBLE);
                }

                // Set step description
                descriptionTextView.setText(mSteps.get(mStepNumber).getDescription());

                // Stop player and reset video position
                if (mExoPlayer != null) {
                    mVideoPosition = 0;
                    mExoPlayer.stop();
                }

                if (!TextUtils.isEmpty(mSteps.get(mStepNumber).getVideoURL())) {
                    // Show player
                    mPlayerView.setVisibility(View.VISIBLE);
                    // Initialize the player if there is a video url available
                    initializePlayer(
                            getContext(),
                            Uri.parse(mSteps.get(mStepNumber).getVideoURL()));
                } else {
                    // Release player
                    releasePlayer();
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
        }

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(context, "BakingApp");
            DataSource.Factory factory = new DefaultDataSourceFactory(context, userAgent);
            MediaSource mediaSource = new ExtractorMediaSource.Factory(factory).createMediaSource(mediaUri);
            mExoPlayer.prepare(mediaSource);

            // Resume playing state and playing position
            if (mVideoPosition != 0) {
                mExoPlayer.seekTo(mVideoPosition);
                mExoPlayer.setPlayWhenReady(mVideoPlayState);
            } else {
                // Otherwise, if position is 0, the video never played and should start by default
                mExoPlayer.setPlayWhenReady(true);
            }
        //}
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
        outState.putParcelableArrayList(RecipesActivity.RECIPE_STEPS_KEY, mSteps);
        outState.putInt(RecipesActivity.NUMBER_STEP_KEY, mStepNumber);
        if (mExoPlayer != null) {
            outState.putLong(VIDEO_POSITION_KEY, mExoPlayer.getCurrentPosition());
            outState.putBoolean(VIDEO_PLAY_STATE_KEY, mVideoPlayState);
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
            mStateBuilder.setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(),
                    1f);
            mVideoPlayState = true;

            // If starting to play a video or play button is clicked and if in landscape mode
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (getActivity() != null) {
                    // Hide action and status bar
                    hideSystemUI();
                    // Set the video view size as big as the screen
                    float[] screenSize = ScreenUtils.getScreenSize(getActivity());
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayerView.getLayoutParams();
                    params.width = (int) screenSize[0]; // params.MATCH_PARENT;
                    params.height = (int) screenSize[1]; //params.MATCH_PARENT;
                    mPlayerView.setLayoutParams(params);
                }
            }

        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(
                    PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(),
                    1f);
            mVideoPlayState = false;
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

    private void hideSystemUI() {
        // Enable fullscreen "lean back" mode
        if (getActivity() != null) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    //View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

    }

    private void showSystemUI() {
        if (getActivity() != null) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
