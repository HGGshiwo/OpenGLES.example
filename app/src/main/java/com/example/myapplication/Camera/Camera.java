package com.example.myapplication.Camera;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.example.myapplication.Object.Object3D;
import com.example.myapplication.Shader.Shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Camera extends Object3D {

    public float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵
    public float[] mProjMatrix = new float[16];//4x4矩阵 投影用
    public float[] position = new float[]{0,0,0};
    public float[] front = new float[]{0,0,-1};
    public float[] up = new float[]{0,1,0};
    public FloatBuffer cameraFB;

    public Camera(
            float left,		//near面的left
            float right,    //near面的right
            float bottom,   //near面的bottom
            float top,      //near面的top
            float near,		//near面距离
            float far,       //far面距离
            float x,
            float y,
            float z,
            float a,
            float rx,
            float ry,
            float rz
    ){
        super(x,y,z,a,rx,ry,rz);
        setProjectFrustum(left,right,bottom,top,near,far);
        setCamera();
    }

    public void setCamera() {

        Matrix.setLookAtM
                (
                        mVMatrix,
                        0,
                        position[0],
                        position[1],
                        position[2],
                        front[0],
                        front[1],
                        front[2],
                        up[0],
                        up[1],
                        up[2]
                );

        ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        cameraFB=llbb.asFloatBuffer();
        cameraFB.put(position);
        cameraFB.position(0);
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
        setCamera();
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

    public void translate(float x, float y, float z){
        position[0] += x;
        position[1] += y;
        position[2] += z;

        front[0] += x;
        front[1] += y;
        front[2] += z;

        front[0] += x;
        front[1] += y;
        front[2] += z;

        setCamera();
    }
}
