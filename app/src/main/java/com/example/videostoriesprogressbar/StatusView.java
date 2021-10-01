package com.example.videostoriesprogressbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.videostoriesprogressbar.Handler.KtkProgressStoriesHandler;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class StatusView extends AppCompatActivity {
    public SimpleExoPlayer player;
    private ZoomableExoPlayerView mExoPlayerView;
    //Uri-Path of Downloaded Videos
    ArrayList<Uri> videoUriList = new ArrayList<>();
    //Custom-Progress Bar
    KtkProgressStoriesHandler storiesProgressView1;
    //Duration-List of Videos
    ArrayList<Long> durationlist = new ArrayList<>();
    //Videocounter for skiping and reverse cases
    private int videocount = 0;
    int ResumePlayCheck = -1;
    //For Syncing player States with skip and reverse cases
    Boolean approchedHere = false;
    View reverse, skip;
    private boolean skipcheck = false;
    ArrayList<URI> allDownlodedFiles = new ArrayList<>();
    Boolean pausevidBar = true;

    //For Cheecking time limit for long press avoiding skip and reverse cases
    long pressTime = 0L;
    long limit = 500L;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_view);
        listFiles();
        storiesProgressView1 = findViewById(R.id.stories);
        ActionBar bar = getSupportActionBar();
        bar.hide();
        //Adding durations with respect to sequence of videos
        durationlist.add(7213L);
        durationlist.add(15047L);
        durationlist.add(11376L);
        durationlist.add(15047L);
        durationlist.add(15047L);
        durationlist.add(15047L);
        //setting no of progress bars
        storiesProgressView1.setStoriesCount(durationlist.size());
        //checking either the videos exists or not
        if (allDownlodedFiles != null) {
            for (int i = 0; i < allDownlodedFiles.size(); i++) {
                videoUriList.add(Uri.parse(String.valueOf(allDownlodedFiles.get(i))));
            }
        }
        //2-views divided for skip and reverse cases
        reverse = findViewById(R.id.reverse);
        skip = findViewById(R.id.skip);
        //video-Player
        mExoPlayerView = findViewById(R.id.exo_player_view);

        //Touch Delay for reverse case
        reverse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            //pausing progressbar
                            storiesProgressView1.pauseProgressBar();
                            //pausevidBar stop from playing past video
                            pausevidBar = false;
                            // press time count for
                            pressTime = System.currentTimeMillis();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        player.setPlayWhenReady(false);
                        mExoPlayerView.hideController();
                        return false;
                    case MotionEvent.ACTION_UP:
                        long now = System.currentTimeMillis();
                        pausevidBar = limit >= now - pressTime;
                        try {
                            //Resume progessBar
                            storiesProgressView1.resumeProgressBar();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //Player state set to true
                        player.setPlayWhenReady(true);
                        mExoPlayerView.hideController();
                }
                return false;
            }
        });
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pausevidBar) {
                    approchedHere = true;
                    skipcheck = true;
                    videocount--;
                    int next = videocount;
                    next++;
                    if (videocount == -1) {
                        finish();
                    } else {
                        storiesProgressView1.ClearProgressBar(videocount, next);
                    }
                    if (videocount >= 0 && videocount < videoUriList.size()) {
                        player.release();
                        setupPlayer();
                    } else {
                        finish();
                    }
                }
            }
        });

        skip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        try {
                            storiesProgressView1.pauseProgressBar();
                            pausevidBar = false;
                            pressTime = System.currentTimeMillis();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        player.setPlayWhenReady(false);
                        mExoPlayerView.hideController();
                        return false;
                    case MotionEvent.ACTION_UP:
                        long now = System.currentTimeMillis();
                        pausevidBar = limit >= now - pressTime;
                        try {
                            storiesProgressView1.resumeProgressBar();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        player.setPlayWhenReady(true);
                        mExoPlayerView.hideController();
                }
                return false;
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pausevidBar) {
                    approchedHere = true;
                    skipcheck = true;
                    storiesProgressView1.fillProgressBar(videocount);
                    videocount++;
                    if (videocount > 0 && videocount < videoUriList.size()) {
                        player.release();
                        setupPlayer();
                    } else {
                        finish();
                    }
                }
            }
        });
        setupPlayer();
    }

    private void listFiles() {
        String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/StatusVideos/";
        File dir = new File(path);
        File[] files = dir.listFiles();

        if (dir.exists()) {
            if (dir.listFiles() != null) {
                for (File file : files) {
                    boolean addthisfile = file.getName().contains("TestVideo_");
                    if (addthisfile) {
                        allDownlodedFiles.add(file.toURI());
                    }
                    Log.d("FileUri'S", "" + file.toURI());
                }
            }
        }
    }

    private void setupPlayer() {
        String userAgent = Util.getUserAgent(this, getApplicationInfo().packageName);
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS
                , DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, null, httpDataSourceFactory);
        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource();

        if (videocount >= 0) {
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(new DefaultExtractorsFactory())
                    .createMediaSource(videoUriList.get(videocount));

            concatenatedSource.addMediaSource(new MergingMediaSource(mediaSource));
        }
        initExoPlayer(concatenatedSource);
        player.addListener(new Player.DefaultEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playWhenReady && playbackState == player.STATE_READY) {

                    if (ResumePlayCheck == -1) {
                        storiesProgressView1.startfrom(durationlist.get(videocount), videocount);
                        storiesProgressView1.startProgress();
                        ResumePlayCheck = -2;
                    } else {
                        storiesProgressView1.resumeProgressBar();
                    }
                    if (approchedHere) {
                        if (videocount > 0 && durationlist.size() > videocount) {
                            storiesProgressView1.startfrom(durationlist.get(videocount), videocount);
                            storiesProgressView1.startProgress();
                            approchedHere = false;
                        }
                        if (skipcheck) {
                            storiesProgressView1.startfrom(durationlist.get(videocount), videocount);
                            storiesProgressView1.startProgress();
                        }
                    }

                } else if (playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                    if (skipcheck == true) {
                        skipcheck = false;
                        if (videocount < videoUriList.size()) {
                            approchedHere = false;
                        } else {
                            finish();
                        }
                    }

                    try {
                        storiesProgressView1.pauseProgressBar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        storiesProgressView1.pauseProgressBar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (playbackState == player.STATE_ENDED) {
                    if (skipcheck == true) {
                        skipcheck = false;
                        if (videocount < videoUriList.size()) {
                            setupPlayer();
                            approchedHere = false;
                        } else {
                            finish();
                        }
                    } else {
                        videocount++;
                        if (videocount < videoUriList.size()) {
                            setupPlayer();
                            approchedHere = true;
                        } else {
                            finish();
                        }
                    }
                    try {
                        storiesProgressView1.pauseProgressBar();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initExoPlayer(MediaSource compositeSource) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
        mExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        mExoPlayerView.setPlayer(player);

        player.prepare(compositeSource);

        mExoPlayerView.getPlayer().setPlayWhenReady(true);
        mExoPlayerView.hideController();
    }
}