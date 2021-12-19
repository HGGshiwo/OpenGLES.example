package com.example.myapplication.Camera;

public class Light extends Camera{

    public String name;
    public float ambient;
    public float diffuse;
    public float specular;

    public Light(
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
        super(left, right, bottom, top, near, far, x, y, z);
        name = "uLight";
        ambient = 0.15f;
        diffuse = 0.9f;
        specular = 0.4f;
    }

    public void setAmbient(float ambient) {
        this.ambient = ambient;
    }

    public void setDiffuse(float diffuse) {
        this.diffuse = diffuse;
    }

    public void setSpecular(float specular) {
        this.specular = specular;
    }
}
