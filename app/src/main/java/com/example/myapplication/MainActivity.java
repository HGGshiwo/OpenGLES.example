package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    public enum Mode{OBJECT, LIGHT1, LIGHT2, CAMERA};

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN ,
                LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mGLSurfaceView = new MySurfaceView(this);
        setContentView(mGLSurfaceView);
        addContentView(
            View.inflate(this, R.layout.view_layout,null),
            new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        );

        mGLSurfaceView.requestFocus();
        mGLSurfaceView.setFocusableInTouchMode(true);

        objectSetting = findViewById(R.id.CardView_object);
        objectSetting.setVisibility(View.GONE);

        lightSetting = findViewById(R.id.CardView_light);
        lightSetting.setVisibility(View.GONE);

        mode = Mode.CAMERA;

        class MovingThread extends Thread{
            @Override
            public void run(){
                super.run();
                while (isMovingDown){
                    switch (mode){
                        case CAMERA:
                            mGLSurfaceView.moveCameraBack();
                            break;
                        case LIGHT1:
                        case LIGHT2:
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
                while (isMovingUp){
                    switch (mode){
                        case CAMERA:
                            mGLSurfaceView.moveCameraForward();
                            break;
                        case LIGHT1:
                        case LIGHT2:
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
                while (isMovingLeft){
                    switch (mode){
                        case CAMERA:
                            mGLSurfaceView.moveCameraLeft();
                            break;
                        case LIGHT1:
                        case LIGHT2:
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
                while (isMovingRight){
                    switch (mode){
                        case CAMERA:
                            mGLSurfaceView.moveCameraRight();
                            break;
                        case LIGHT1:
                        case LIGHT2:
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
        buttonDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isMovingDown = true;
                        MovingThread mt = new MovingThread();
                        mt.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        isMovingDown = false;
                }
                return true;
            }
        });

        buttonUp = findViewById(R.id.button_up);
        buttonUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isMovingUp = true;
                        MovingThread mt = new MovingThread();
                        mt.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        isMovingUp = false;
                }
                return true;
            }
        });

        buttonLeft = findViewById(R.id.button_left);
        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isMovingLeft = true;
                        MovingThread mt = new MovingThread();
                        mt.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        isMovingLeft = false;
                }
                return true;
            }
        });

        buttonRight = findViewById(R.id.button_right);
        buttonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isMovingRight = true;
                        MovingThread mt = new MovingThread();
                        mt.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        isMovingRight = false;
                }
                return true;
            }
        });
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

    public void chooseLight1(View v){
        mode = Mode.LIGHT1;
        objectSetting.setVisibility(View.GONE);
        lightSetting.setVisibility(View.VISIBLE);
    }

    public void chooseLight2(View v){
        mode = Mode.LIGHT2;
        objectSetting.setVisibility(View.GONE);
        lightSetting.setVisibility(View.VISIBLE);
    }

    public void chooseObject(View v){
        mode = Mode.OBJECT;
        lightSetting.setVisibility(View.GONE);
        objectSetting.setVisibility(View.VISIBLE);
    }

    public void chooseOK(View v){
        objectSetting.setVisibility(View.GONE);
        lightSetting.setVisibility(View.GONE);
        mode = Mode.CAMERA;
    }
}



