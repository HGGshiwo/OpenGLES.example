package com.example.myapplication.Camera;

import com.example.myapplication.Shader.Shader;

public class Light extends Camera{
    private String name;

    public Light(
            String cameraName,
            float left,		//near���left
            float right,    //near���right
            float bottom,   //near���bottom
            float top,      //near���top
            float near,		//near�����
            float far,       //far�����
            float x,
            float y,
            float z,
            float a,
            float rx,
            float ry,
            float rz
    ){
        super(left, right, bottom, top, near, far, x, y, z, a, rx, ry, rz);
        name=cameraName;
    }
}
