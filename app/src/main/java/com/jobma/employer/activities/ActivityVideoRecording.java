package com.jobma.employer.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jobma.employer.R;
import com.jobma.employer.util.CameraPreview;
import com.jobma.employer.util.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityVideoRecording extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 4;
    private Camera mCamera = null;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private ImageButton imageSwitch, imageRecord, imageStop;
    private double timeConsumed = 0.0;
    boolean recording = false;
    private CountDownTimer countDownTimerThinkTime, countDownTimerDuration;
    private File file;
    //private long durationTimer;
    private TextView tvTimer;
    private boolean isFront;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_recording);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.initView();
        this.setOnClickListener();
        this.initialize();
    }

    private void initView() {
        this.imageSwitch = findViewById(R.id.imageSwitch);
        this.imageRecord = findViewById(R.id.imageRecord);
        this.imageStop = findViewById(R.id.imageStop);
        this.tvTimer = findViewById(R.id.tvTimer);
    }

    private void setOnClickListener() {

        imageStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ((timeConsumed) < 5) {
                        Toast.makeText(ActivityVideoRecording.this, "Minimum recording should be 5 seconds", Toast.LENGTH_SHORT).show();
                    } else {
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
                        Toast.makeText(ActivityVideoRecording.this, "Stop recording...", Toast.LENGTH_SHORT).show();
                        if (countDownTimerDuration != null) {
                            countDownTimerDuration.cancel();
                            countDownTimerDuration = null;
                        }

                        if (getIntent() != null) {
                            if (getIntent().hasExtra("from")) {
                                Intent intent = new Intent();
                                intent.putExtra("path", file.getPath());
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                EventBus.getDefault().post(new MessageEvent(file.getPath()));
                                onBackPressed();
                            }
                        } else {
                            EventBus.getDefault().post(new MessageEvent(file.getPath()));
                            onBackPressed();
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initialize() {
        cameraPreview = findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(this, mCamera);
        cameraPreview.addView(mPreview);
        imageRecord = findViewById(R.id.imageRecord);
        imageRecord.setOnClickListener(captureListener);
        imageSwitch = findViewById(R.id.imageSwitch);
        imageSwitch.setOnClickListener(switchCameraListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        } else {
            // code for lollipop and pre-lollipop devices
        }
    }


    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int recordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            //return false;
        }
        return true;
    }

    private void videoAutomaticallyStartsRecording() {
        if (recording) {
            mediaRecorder.stop(); // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            Toast.makeText(ActivityVideoRecording.this, "Video captured!", Toast.LENGTH_LONG).show();
            recording = false;
        } else {
            if (!prepareMediaRecorder()) {
                Toast.makeText(ActivityVideoRecording.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                finish();
            }
            // work on UiThread for better performance
            runOnUiThread(new Runnable() {
                public void run() {
                    // If there are stories, add them to the table
                    try {
                        mediaRecorder.start();
                    } catch (final Exception ex) {
                        // Log.i("---","Exception in thread");
                    }
                }
            });
            recording = true;
        }
    }

    private void timerForDuration() {
        countDownTimerDuration = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                long millisElapsed = 500000 - millisUntilFinished;
                long minutes = (millisElapsed / 1000) / 60;
                long seconds = (millisElapsed / 1000) % 60;
                tvTimer.setText(String.format("%02d:%02d:%02d", 0L, minutes, seconds));
                timeConsumed++;
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
            }

            public void onFinish() {
            }
        }.start();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        isFront = true;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        isFront = false;
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other applications
        System.out.println("onPause is called");
        releaseCamera();
    }

    @Override
    public void onBackPressed() {
//        System.out.println("onBackPressed is called");
//        //ActivityVideoRecording.this.finish();
//        finishAffinity();
//        //moveTaskToBack(true);
//
//        if (countDownTimerThinkTime != null) {
//            countDownTimerThinkTime.cancel();
//            countDownTimerThinkTime = null;
//        }
//        if (countDownTimerDuration != null) {
//            countDownTimerDuration.cancel();
//            countDownTimerDuration = null;
//        }
        super.onBackPressed();

    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("onStop is called");
        if (countDownTimerThinkTime != null) {
            countDownTimerThinkTime.cancel();
        }
    }

    public void onResume() {
        super.onResume();
        System.out.println("onResume is called");

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast toast = Toast.makeText(ActivityVideoRecording.this, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }

        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                imageSwitch.setVisibility(View.GONE);
            }
            mCamera = Camera.open(findBackFacingCamera());
            mPreview.refreshCamera(mCamera);
            mCamera.setDisplayOrientation(90); // ORIENTATION SET BY SHAHZEB IT WAS NOT
        }
    }

    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa
                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast.makeText(ActivityVideoRecording.this, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId);
                mPreview.refreshCamera(mCamera);
                mCamera.setDisplayOrientation(90);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId);
                mPreview.refreshCamera(mCamera);
                mCamera.setDisplayOrientation(90);
            }
        }
    }

    View.OnClickListener captureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            imageSwitch.setVisibility(View.GONE);
            imageRecord.setVisibility(View.GONE);
            imageStop.setVisibility(View.VISIBLE);
            // timerForDuration();
            if (recording) {
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(ActivityVideoRecording.this, "Video captured!", Toast.LENGTH_LONG).show();
                recording = false;
                countDownTimerThinkTime.cancel();
            } else {
                if (!prepareMediaRecorder()) {
                    Toast.makeText(ActivityVideoRecording.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }

                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table
                        try {
                            mediaRecorder.start();
                            if (countDownTimerThinkTime != null) {
                                countDownTimerThinkTime.cancel();
                            } else {
                                countDownTimerThinkTime = new CountDownTimer(500000, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        long millisElapsed = 500000 - millisUntilFinished;
                                        long minutes = (millisElapsed / 1000) / 60;
                                        long seconds = (millisElapsed / 1000) % 60;
                                        tvTimer.setText(String.format("%02d:%02d:%02d", 0L, minutes, seconds));
                                        timeConsumed++;
                                    }

                                    public void onFinish() {
                                        imageRecord.setVisibility(View.GONE);
                                        imageStop.setVisibility(View.VISIBLE);
                                        imageSwitch.setVisibility(View.GONE);
                                        timerForDuration();
                                        videoAutomaticallyStartsRecording();
                                    }
                                }.start();
                            }
                        } catch (final Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                recording = true;
            }
        }
    };

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mediaRecorder.setOrientationHint(isFront ? 270 : 90);
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        mediaRecorder.setMaxDuration(120000);  // Set max duration 120 sec.
//        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M
        mediaRecorder.setVideoSize(640, 480);
        file = new File(Environment.getExternalStorageDirectory(), "videocapture_profile.mp4");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(file);
        } else {
            mediaRecorder.setOutputFile(file.getPath());
        }
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with all permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for all permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                            showDialogOK("Camera and Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onClick(View view) {

    }

}
