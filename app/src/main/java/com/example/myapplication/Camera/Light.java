package com.example.myapplication.Camera;

import com.example.myapplication.Shader.Shader;

public class Light extends Camera{
    private String name;

    public Light(
            String cameraName,
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
        super(left, right, bottom, top, near, far, x, y, z, a, rx, ry, rz);
        name=cameraName;
    }
}
