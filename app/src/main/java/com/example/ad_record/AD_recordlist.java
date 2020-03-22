package com.example.ad_record;


import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class AD_recordlist extends Fragment implements audioListAdapter.onItemClick{

    ConstraintLayout bottomsheet;
    BottomSheetBehavior bottomSheetBehavior;
    audioListAdapter audioListAdapter;
    File[] files;
    RecyclerView audioList;
    MediaPlayer mediaPlayer = null;
    boolean isPlaying = false;
    File fileToPlay;

    // UI elements

    ImageView playPauseButton;
    TextView playerHeader,playerFileName;
    SeekBar seekbar;
    Handler seekBarHandler;
    Runnable updateSeekBar;
    ImageButton prevButton,nextButton;

    public AD_recordlist() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ad_recordlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        audioList = (RecyclerView) view.findViewById(R.id.audioList);
        bottomsheet = view.findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        files = directory.listFiles();
        audioListAdapter = new audioListAdapter(files,this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioListAdapter);

        // UI initialize

        playPauseButton = view.findViewById(R.id.playPauseButton);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFileName = view.findViewById(R.id.playerFileName);
        seekbar = view.findViewById(R.id.seekBar);
        prevButton = view.findViewById(R.id.previousButton);
        nextButton = view.findViewById(R.id.nextButton);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAudio();
                int position = mediaPlayer.getCurrentPosition();
                mediaPlayer.seekTo(position-1000);
                resumeAudio();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAudio();
                int position = mediaPlayer.getCurrentPosition();
                mediaPlayer.seekTo(position+1000);
                resumeAudio();
            }
        });


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null)
                {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null)
                {
                    // changes the audio position
                    int progress = seekBar.getProgress();
//                    Log.d("ADcheck", "onStopTrackingTouch: " + progress);
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });


        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying)
                {
                   pauseAudio();
                }
                else
                {
                    resumeAudio();
                }
            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN)
                {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void onClickListener(File file, int position) {
        if(isPlaying)
        {
            stopAudio();
            fileToPlay = file;
            playAudio(fileToPlay);
        }
        else
        {
            fileToPlay = file;
            playAudio(fileToPlay);
        }
    }

    private void stopAudio() {
        // stop audio
        isPlaying = false;
        mediaPlayer.stop();
        playPauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn,null));
        playerHeader.setText("Not Playing");

        // collapse the bootsheet once audio finished
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void playAudio(File fileToPlay) {
        // play audio
        mediaPlayer = new MediaPlayer();
        try {
            // expands the bootsheet once audio finished
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            playPauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn,null));
            playerFileName.setText(fileToPlay.getName());
            playerHeader.setText("Playing");
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                playerHeader.setText("Finished!");
            }
        });

        seekbar.setMax(mediaPlayer.getDuration());
        seekBarHandler = new Handler();
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                seekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this,500);
            }
        };
        seekBarHandler.postDelayed(updateSeekBar,0);
    }

    void pauseAudio()
    {
        mediaPlayer.pause();
        playPauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn));
        playerHeader.setText("Paused");
        isPlaying = false;
//        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    void resumeAudio()
    {
        if(fileToPlay != null)
        {
            mediaPlayer.start();
            playPauseButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn));
            playerHeader.setText("Playing");
            isPlaying = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying)
        {
            stopAudio();
        }
    }
}

