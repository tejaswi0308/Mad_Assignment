package com.example.multimediaplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private boolean isVideoActive = false;
    private boolean isVideoStopped = false;
    private static final int PICK_AUDIO_REQUEST = 1;
    private String currentVideoUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);

        findViewById(R.id.btnOpenURL).setOnClickListener(v -> {
            EditText urlEditText = new EditText(this);
            urlEditText.setHint("Enter stream URL");
            urlEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
            urlEditText.setTextColor(getColor(android.R.color.black));
            urlEditText.setHintTextColor(getColor(android.R.color.darker_gray));

            new android.app.AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog)
                    .setTitle("Stream Video URL")
                    .setView(urlEditText)
                    .setPositiveButton("Play", (dialog, which) -> {
                        String url = urlEditText.getText().toString().trim();

                        if (url.isEmpty()) {
                            Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        isVideoActive = true;
                        isVideoStopped = false;

                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }

                        currentVideoUrl = url;
                        loadAndPlayVideo(0);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        findViewById(R.id.btnOpenFile).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, PICK_AUDIO_REQUEST);
        });

        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            if (isVideoActive) {
                if (isVideoStopped && currentVideoUrl != null) {
                    isVideoStopped = false;
                    loadAndPlayVideo(0);
                } else {
                    videoView.start();
                }
            } else if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        });

        findViewById(R.id.btnPause).setOnClickListener(v -> {
            if (isVideoActive && videoView.isPlaying()) {
                videoView.pause();
            } else if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        findViewById(R.id.btnStop).setOnClickListener(v -> {
            if (isVideoActive) {
                videoView.stopPlayback();
                isVideoStopped = true;
            } else if (mediaPlayer != null) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        });

        findViewById(R.id.btnRestart).setOnClickListener(v -> {
            if (isVideoActive && currentVideoUrl != null) {
                isVideoStopped = false;
                loadAndPlayVideo(0);
            } else if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }
        });
    }

    private void loadAndPlayVideo(int seekPosition) {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(currentVideoUrl);
        videoView.requestFocus();

        videoView.setOnPreparedListener(mp -> {
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            videoView.seekTo(seekPosition == 0 ? 1 : seekPosition);
            videoView.start();
            Toast.makeText(this, "Video Playing...", Toast.LENGTH_SHORT).show();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e("VIDEO_DEBUG", "Error What: " + what + " Extra: " + extra);
            String errorMsg = "Stream Error. Try Cold Booting Emulator.";
            if (what == 1 && extra == -1004) errorMsg = "Check Internet Connection!";
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri audioUri = data.getData();
            isVideoActive = false;
            isVideoStopped = false;
            videoView.stopPlayback();

            try {
                if (mediaPlayer != null) mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(this, audioUri);
                mediaPlayer.start();
                Toast.makeText(this, "Playing Audio File", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}