package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.SeekBar;


public class MainActivity extends AppCompatActivity {

    public enum Mode {OBJECT, LIGHT, CAMERA}

    private MySurfaceView mGLSurfaceView;
    private CardView objectSetting;
    private CardView lightSetting;
    public Mode mode;

    private boolean isMovingRight;
    private boolean isMovingLeft;
    private boolean isMovingUp;
    private boolean isMovingDown;

    private Button buttonLeft;
    private Button buttonRight;
    private Button buttonUp;
    private Button buttonDown;

    private SeekBar seekBarAmbient;
    private SeekBar seekBarDiffuse;
    private SeekBar seekBarSpecular;
    private SeekBar seekBarShininess;

    LocationManager manager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mode = Mode.CAMERA;


        initView();
        initInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    public void chooseLight(View v) {
        mode = Mode.LIGHT;
        objectSetting.setVisibility(View.GONE);
        lightSetting.setVisibility(View.VISIBLE);
    }

    public void chooseObject(View v) {
        mode = Mode.OBJECT;
        lightSetting.setVisibility(View.GONE);
        objectSetting.setVisibility(View.VISIBLE);
    }

    public void chooseOK(View v) {
        objectSetting.setVisibility(View.GONE);
        lightSetting.setVisibility(View.GONE);
        mode = Mode.CAMERA;
    }

    private void initView() {

        mGLSurfaceView = new MySurfaceView(this);
        setContentView(mGLSurfaceView);
        addContentView(
                View.inflate(this, R.layout.view_layout, null),
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        );

        mGLSurfaceView.requestFocus();
        mGLSurfaceView.setFocusableInTouchMode(true);
    }

    private void initInterface() {
        objectSetting = findViewById(R.id.CardView_object);
        objectSetting.setVisibility(View.GONE);

        lightSetting = findViewById(R.id.CardView_light);
        lightSetting.setVisibility(View.GONE);


        class MovingThread extends Thread {
            @Override
            public void run() {
                super.run();
                while (isMovingDown) {
                    switch (mode) {
                        case CAMERA:
                            mGLSurfaceView.moveCameraBack();
                            break;
                        case LIGHT:
                            mGLSurfaceView.moveLightBack();
                            break;
                        case OBJECT:
                            mGLSurfaceView.moveObjectBack();
                            break;
                    }
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (isMovingUp) {
                    switch (mode) {
                        case CAMERA:
                            mGLSurfaceView.moveCameraForward();
                            break;
                        case LIGHT:
                            mGLSurfaceView.moveLightForward();
                            break;
                        case OBJECT:
                            mGLSurfaceView.moveObjectForward();
                            break;
                    }
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (isMovingLeft) {
                    switch (mode) {
                        case CAMERA:
                            mGLSurfaceView.moveCameraLeft();
                            break;
                        case LIGHT:
                            mGLSurfaceView.moveLightLeft();
                            break;
                        case OBJECT:
                            mGLSurfaceView.moveObjectLeft();
                            break;
                    }
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (isMovingRight) {
                    switch (mode) {
                        case CAMERA:
                            mGLSurfaceView.moveCameraRight();
                            break;
                        case LIGHT:
                            mGLSurfaceView.moveLightRight();
                            break;
                        case OBJECT:
                            mGLSurfaceView.moveObjectRight();
                            break;
                    }
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        buttonDown = findViewById(R.id.button_down);
        buttonDown.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMovingDown = true;
                    MovingThread mt = new MovingThread();
                    mt.start();
                    break;
                case MotionEvent.ACTION_UP:
                    isMovingDown = false;
            }
            return true;
        });

        buttonUp = findViewById(R.id.button_up);
        buttonUp.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMovingUp = true;
                    MovingThread mt = new MovingThread();
                    mt.start();
                    break;
                case MotionEvent.ACTION_UP:
                    isMovingUp = false;
            }
            return true;
        });

        buttonLeft = findViewById(R.id.button_left);
        buttonLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMovingLeft = true;
                    MovingThread mt = new MovingThread();
                    mt.start();
                    break;
                case MotionEvent.ACTION_UP:
                    isMovingLeft = false;
            }
            return true;
        });

        buttonRight = findViewById(R.id.button_right);
        buttonRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isMovingRight = true;
                    MovingThread mt = new MovingThread();
                    mt.start();
                    break;
                case MotionEvent.ACTION_UP:
                    isMovingRight = false;
            }
            return true;
        });

        seekBarAmbient = findViewById(R.id.SeekBar_ambient);
        seekBarAmbient.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGLSurfaceView.setAmbient(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarDiffuse = findViewById(R.id.SeekBar_diffuse);
        seekBarDiffuse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGLSurfaceView.setDiffuse(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarSpecular = findViewById(R.id.SeekBar_specular);
        seekBarSpecular.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGLSurfaceView.setSecular(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarShininess = findViewById(R.id.SeekBar_shininess);
        seekBarShininess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mGLSurfaceView.setShininess(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initLocation() {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

}



