package com.example.myapplication.Object;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Object3D {

    public float[] currMatrix;//当前变换矩阵
    public float[] up; //相机lookAt使用
    public float[] front; //相机lookAt使用
    public float[] position; //相机lookAt使用

    public Object3D(){
        up = new float[]{0f,1f,0f};
        front = new float[]{0f,0f,-1f};
        position = new float[]{0,0,0};
        currMatrix = new float[16];
        Matrix.setRotateM(currMatrix,0,0,0,1,0);//注意这里是set
    }

    public Object3D(
            float x,
            float y,
            float z

    ){
        up = new float[]{0f,1f,0f};
        front = new float[]{0f,0f,-1f};
        position = new float[]{x,y,z};

        currMatrix = new float[16];
        Matrix.translateM(currMatrix, 0, x, y, z);//不能调用translate，因为子类重载
    }

    public void translate(float x,float y,float z){//设置沿xyz轴移动
        Matrix.translateM(currMatrix, 0, x, y, z);
        position[0]+=x;
        position[1]+=y;
        position[2]+=z;
    }

    public void moveForward(float x){
        float [] front = this.front;
        this.translate(-1*x*front[0],0,-1*x*front[2]);
    }

    public void moveLeft(float x){
        float [] rightDistance = Model.vectorNormal(
                Model.getCrossProduct(
                        up[0],
                        up[1],
                        up[2],
                        front[0],
                        front[1],
                        front[2]
                ));

        translate(
                x*rightDistance[0],
                0,
                x*rightDistance[2]
        );
    }

    public void rotateRight(float angle){
        float [] right = Model.vectorNormal(
                Model.getCrossProduct(
                        up[0],
                        up[1],
                        up[2],
                        front[0],
                        front[1],
                        front[2]
                ));
        rotate(angle, right[0], right[1], right[2]);
    }

    public void rotateUp(float angle){
        float radians = (float) Math.toRadians(angle);
        rotate(angle, 0, 1, 0);
    }


    public void scale(float x,float y,float z){
        Matrix.scaleM(currMatrix,0,x,y,z);
    }

    public void rotate(float angle,float x,float y,float z){//设置绕xyz轴移动
        Matrix.rotateM(currMatrix,0,angle,x,y,z);
        float[] rotateMatrix = new float[16];
        float[] rotateFront =  new float[]{front[0],front[1],front[2],1f};
        float[] rotateUp = new float[]{up[0],up[1],up[2],1.0f};
        Matrix.setRotateM(rotateMatrix,0,angle,x,y,z);
        Matrix.multiplyMV(
                rotateFront,
                0,
                rotateMatrix,
                0,
                rotateFront,
                0
        );
        float[] rotateFrontNormalized = Model.vectorNormal(rotateFront);
        front[0]=rotateFrontNormalized[0];
        front[1]=rotateFrontNormalized[1];
        front[2]=rotateFrontNormalized[2];

        Matrix.multiplyMV(
                rotateUp,
                0,
                rotateMatrix,
                0,
                rotateUp,
                0
        );
        float[] rotateUpNormalized = Model.vectorNormal(rotateUp);
        up[0]=rotateUpNormalized[0];
        up[1]=rotateUpNormalized[1];
        up[2]=rotateUpNormalized[2];
    }
}
