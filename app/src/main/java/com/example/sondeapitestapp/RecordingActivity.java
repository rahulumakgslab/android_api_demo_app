package com.example.sondeapitestapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sondeapitestapp.utils.WaveUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RecordingActivity extends AppCompatActivity {

    private TextView textViewTimer;

    private static final int PERMISSIONS_RECORD_AUDIO = 1;
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int SAMPLE_RATE_HZ = 44100;
    private static final int AUDIO_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AUDIO_BUFFER_SIZE_BYTES = 4096;
    private static final String TAG = RecordingActivity.class.getSimpleName();
    private static final long COUNTDOWN_PERIOD = 30000;
    private AudioRecord mAudioRecord;
    private String mFilePath;
    private TextView tvTimer;
    private boolean recording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        textViewTimer = findViewById(R.id.textViewTimer);
        findViewById(R.id.buttonFinish).setOnClickListener(onFinishButtonListener);

    }

    private View.OnClickListener onFinishButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (((Button) view).getText().equals(getString(R.string.start_recording))) {
                requestAudioPermissions();
                ((Button) view).setText(R.string.stop_recording);
            } else {
                stopRecordingAndSendResult();
            }
        }
    };

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead for recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_RECORD_AUDIO) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                startRecording();
            } else {
                // permission denied
                Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
            }
        }
    }

    // starts recording a file at internal memory
    private void startRecording() {
        mAudioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE_HZ, AUDIO_CHANNEL_CONFIG, AUDIO_FORMAT, AUDIO_BUFFER_SIZE_BYTES);
        mAudioRecord.startRecording();
        mFilePath = getFilesDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".wav";
        recording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile(mFilePath);
            }
        }).start();
        startCountDownTimer();
    }

    private void startCountDownTimer() {
        new CountDownTimer(COUNTDOWN_PERIOD, 1000) {

            public void onTick(long millisUntilFinished) {
                textViewTimer.setText(millisUntilFinished / 1000 + " seconds remaining ");
            }

            public void onFinish() {
                stopRecordingAndSendResult();
            }

        }.start();
    }

    private void stopRecordingAndSendResult() {
        stopRecording();
        Intent intent = new Intent();
        intent.putExtra("filePath", mFilePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void stopRecording() {
        recording = false;
    }

    private void writeAudioDataToFile(String filename) {
        String audioFilename = filename + ".pcm";
        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(audioFilename);
            int bufferSizeBytes = AUDIO_BUFFER_SIZE_BYTES;
            short[] audioBuffer = new short[bufferSizeBytes / 2]; // assumes 16-bit encoding
            byte[] outputBuffer = new byte[bufferSizeBytes];
            int totalBytesRead = 0;
            while (recording) {
                int numShortsRead = mAudioRecord.read(audioBuffer, 0, audioBuffer.length);

                for (int i = 0; i < numShortsRead; i++) {
                    outputBuffer[i * 2] = (byte) (audioBuffer[i] & 0x00FF);
                    outputBuffer[i * 2 + 1] = (byte) (audioBuffer[i] >> 8);
                    audioBuffer[i] = 0;
                }

                int numBytesRead = numShortsRead * 2;
                totalBytesRead += numBytesRead;
                outputStream.write(outputBuffer, 0, numBytesRead);

            }

            OutputStream waveOutputStream = new FileOutputStream(mFilePath);
            InputStream dataInputStream = new FileInputStream(audioFilename);
            short numChannels = 1;
            short sampleSizeBytes = 2;
            WaveUtils.pcmToWave(waveOutputStream,
                    dataInputStream,
                    totalBytesRead,
                    numChannels,
                    SAMPLE_RATE_HZ,
                    sampleSizeBytes);
        } catch (Exception e) {
            Log.e(TAG, "Error : " + e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error : " + e);
            }

            try {
                if (mAudioRecord != null) {
                    mAudioRecord.stop();
                    mAudioRecord.release();
                    mAudioRecord = null;
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error : " + e);
            }

            // delete PCM file
            File file = new File(audioFilename);
            file.delete();
        }
    }
}