package com.example.myapplication.Shader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;

import android.content.res.Resources;
import android.opengl.GLES30;
import android.util.Log;

//���ض���Shader��ƬԪShader�Ĺ�����
public class Shader
{
    private final int id;

    public Shader(String vertexSource, String fragmentSource) {
        id =  createProgram(vertexSource, fragmentSource);
   }

   public Shader(
           String mVertexShaderFile,//������ɫ������ű�
           String mFragmentShaderFile,//ƬԪ��ɫ������ű�
           Resources resources
   ) {
        String mVertexShader = loadFromAssetsFile(mVertexShaderFile, resources);
        String mFragmentShader = loadFromAssetsFile(mFragmentShaderFile, resources);
        id = createProgram(mVertexShader, mFragmentShader);
   }

    //�����ƶ�shader�ķ���
    public static int loadShader(
            int shaderType, //shader������  GLES30.GL_VERTEX_SHADER   GLES30.GL_FRAGMENT_SHADER
            String source   //shader�Ľű��ַ���
    ) {
        //����һ����shader
        int shader = GLES30.glCreateShader(shaderType);
        //�������ɹ������shader
        if (shader != 0)
        {
            //����shader��Դ����
            GLES30.glShaderSource(shader, source);
            //����shader
            GLES30.glCompileShader(shader);
            //��ű���ɹ�shader����������
            int[] compiled = new int[1];
            //��ȡShader�ı������
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0)
            {//������ʧ������ʾ������־��ɾ����shader
                Log.e("ES30_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES30_ERROR", GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

   //����shader����ķ���
   public int createProgram(String vertexSource, String fragmentSource) {
	    //���ض�����ɫ��
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) 
        {
            return 0;
        }
        
        //����ƬԪ��ɫ��
        int pixelShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) 
        {
            return 0;
        }

        //��������
        int program = GLES30.glCreateProgram();
        //�����򴴽��ɹ���������м��붥����ɫ����ƬԪ��ɫ��
        if (program != 0) 
        {
        	//������м��붥����ɫ��
            GLES30.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            //������м���ƬԪ��ɫ��
            GLES30.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            //���ӳ���
            GLES30.glLinkProgram(program);
            //������ӳɹ�program����������
            int[] linkStatus = new int[1];
            //��ȡprogram���������
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
            //������ʧ���򱨴�ɾ������
            if (linkStatus[0] != GLES30.GL_TRUE) 
            {
                Log.e("ES30_ERROR", "Could not link program: ");
                Log.e("ES30_ERROR", GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
    
   //���ÿһ�������Ƿ��д���ķ��� 
   public static void checkGlError(String op) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) 
        {
            Log.e("ES30_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
   }
   
   //��sh�ű��м���shader���ݵķ���
   public static String loadFromAssetsFile(String fname,Resources r) {
   	String result=null;    	
   	try
   	{
   		InputStream in=r.getAssets().open(fname);
			int ch=0;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while((ch=in.read())!=-1)
		    {
		      	baos.write(ch);
		    }      
		    byte[] buff=baos.toByteArray();
		    baos.close();
		    in.close();
   		result=new String(buff,"UTF-8"); 
   		result=result.replaceAll("\\r\\n","\n");
   	}
   	catch(Exception e)
   	{
   		e.printStackTrace();
   	}    	
   	return result;
   }

   public void use(){
       GLES30.glUseProgram(id);
   }

   public void setFloat(String name, float v){
       int location = GLES30.glGetUniformLocation(id, name);
       GLES30.glUniform1f(location, v);
   }

   public void setMat4f(String name, float[] mat4){
       int location = GLES30.glGetUniformLocation(id, name);
       GLES30.glUniformMatrix4fv(location, 1, false, mat4, 0);
   }

   public void setVec3f(String name, FloatBuffer buffer){
       int location = GLES30.glGetUniformLocation(id, name);
       GLES30.glUniform3fv(location, 1, buffer);
   }

   public void setPointer3f(String name, boolean normalized, FloatBuffer buffer){
       int location = GLES30.glGetAttribLocation(id, name);
       GLES30.glVertexAttribPointer
               (
                       location,
                       3,
                       GLES30.GL_FLOAT,
                       normalized,
                       3*4,
                       buffer
               );
       GLES30.glEnableVertexAttribArray(location);
   }

   public void setPointer2f(String name, boolean normalized, FloatBuffer buffer){
       int location = GLES30.glGetAttribLocation(id, name);
       GLES30.glVertexAttribPointer
               (
                       location,
                       2,
                       GLES30.GL_FLOAT,
                       normalized,
                       2*4,
                       buffer
               );
       GLES30.glEnableVertexAttribArray(location);
   }
}
