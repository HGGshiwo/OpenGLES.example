package com.example.myapplication.Object;

import android.opengl.Matrix;

public class Object3D {

    public float[] currMatrix;//当前变换矩阵
    public float[] up;
    public float[] front;

    public Object3D(){
        up = new float[]{0f,1f,0f};
        front = new float[]{0f,0f,-1f};
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix,0,0,0,1,0);//注意这里是set
    }

    public Object3D(
            float x,
            float y,
            float z,
            float a,
            float rx,
            float ry,
            float rz

    ){
        currMatrix = new float[16];
        Matrix.translateM(currMatrix,0, x, y, z);
        Matrix.rotateM(currMatrix,0, a, rx, ry, rz);
    }

    public void translate(float x,float y,float z){//设置沿xyz轴移动
        Matrix.translateM(currMatrix, 0, x, y, z);
    }

    public void rotate(float angle,float x,float y,float z)//设置绕xyz轴移动
    {
        Matrix.rotateM(currMatrix,0,angle,x,y,z);
        float[] rotateMatrix = new float[16];
        float[] rotateVector =  new float[4];
        Matrix.setRotateM(rotateMatrix,0,angle,x,y,z);
        Matrix.multiplyMV(
                rotateVector,
                0,
                rotateMatrix,
                0,
                front,
                0
        );
        front[0]=rotateMatrix[0];
        front[1]=rotateMatrix[1];
        front[2]=rotateMatrix[2];
    }
}
