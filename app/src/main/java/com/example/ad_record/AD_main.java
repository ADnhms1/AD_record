package com.example.ad_record;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AD_main extends Fragment implements View.OnClickListener {

    private NavController navController;
    private ImageButton imageView;
    private ImageButton recordButton;
    boolean isRecording = false;
    MediaRecorder mediaRecorder;
    Chronometer chronometer;
    TextView fileNameView;

    public AD_main() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ad_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        imageView = view.findViewById(R.id.recordListButton);
        recordButton = view.findViewById(R.id.record_button);
        chronometer = view.findViewById(R.id.chronometer2);
        fileNameView = view.findViewById(R.id.record_FileName);

        imageView.setOnClickListener(this);
        recordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.recordListButton :
                if(isRecording)
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopRecording();
                            navController.navigate(R.id.action_AD_main_to_AD_recordlist);
                        }
                    });
                    alertDialog.setNegativeButton("Cancel",null);
                    alertDialog.setTitle("Audio Still Recording");
                    alertDialog.setMessage("Are you sure you want to stop?");
                    alertDialog.create().show();
                }
                else
                {
                    navController.navigate(R.id.action_AD_main_to_AD_recordlist);
                }
                break;
            case R.id.record_button :
                if(isRecording)
                {
                    // stops recording
                    stopRecording();
                    recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped));
                    isRecording = false;
                }
                else
                {
                    if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                    {
                        // starts recording
                        recordButton.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording));
                        startRecording();
                        isRecording = true;
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},1);
                    }
                }

        }
    }

    void stopRecording()
    {
        chronometer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;
        fileNameView.setText(R.string.fileNameViewText);
        isRecording = false;
    }


    void startRecording()
    {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        String filePath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
        String fileName = "Recording"+format.format(new Date())+".3gp";
        fileNameView.setText(fileName);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(filePath + "/" + fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
        isRecording = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording)
        {
            stopRecording();
        }
    }
}
