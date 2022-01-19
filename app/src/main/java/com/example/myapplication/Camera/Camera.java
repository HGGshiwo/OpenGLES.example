package com.example.myapplication.Camera;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.example.myapplication.Object.Object3D;
import com.example.myapplication.Shader.Shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Camera extends Object3D {

    public float[] mVMatrix;//摄像机位置朝向9参数矩阵
    public float[] mProjMatrix;//4x4矩阵 投影用
    public FloatBuffer positionBuffer;

    public Camera(
            float left,		//near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,		//near面距离
            float far,       //far面距离
            float x,
            float y,
            float z
    ){
        super(x,y,z);
        mVMatrix  = new float[16];
        mProjMatrix = new float[16];
        setProjectFrustum(left,right,bottom,top,near,far);
        setLookAt();
        setPositionBuffer();
    }

    public void setLookAt() {
        Matrix.setLookAtM
                (
                        mVMatrix,
                        0,
                        position[0],
                        position[1],
                        position[2],
                        position[0]+front[0],
                        position[1]+front[1],
                        position[2]+front[2],
                        up[0],
                        up[1],
                        up[2]
                );
    }

    protected void setPositionBuffer(){
        ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        positionBuffer=llbb.asFloatBuffer();
        positionBuffer.put(position);
        positionBuffer.position(0);
    }

    public void setProjectFrustum
            (
                    float left,		//near面的left
                    float right,    //near面的right
                    float bottom,   //near面的bottom
                    float top,      //near面的top
                    float near,		//near面距离
                    float far       //far面距离
            ) {
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    //设置正交投影参数
    public void setProjectOrtho
    (
            float left,		//near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,		//near面距离
            float far       //far面距离
    ) {
        Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void rotate(float angle,float x,float y,float z){
        super.rotate(angle,x,y,z);
        setLookAt();
    }

    @Override
    public void translate(float x,float y,float z){
        super.translate(x,y,z);
        setPositionBuffer();
        setLookAt();
    }

    @Override
    public void moveForward(float x){
        super.moveForward(x);
        setPositionBuffer();
        setLookAt();
    }

    @Override
    public void moveLeft(float x){
        super.moveLeft(x);
        setPositionBuffer();
        setLookAt();
    }

    @Override
    public void rotateRight(float angle){
        super.rotateRight(angle);
        setPositionBuffer();
        setLookAt();
    }

    @Override
    public void rotateUp(float angle){
        super.rotateUp(angle);
        setPositionBuffer();
        setLookAt();
    }
}
