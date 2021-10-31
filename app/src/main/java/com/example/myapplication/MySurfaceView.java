package com.example.myapplication;//������
import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.cardview.widget.CardView;

import com.example.myapplication.Camera.Camera;
import com.example.myapplication.Camera.Light;
import com.example.myapplication.Object.Model;
import com.example.myapplication.Shader.Shader;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
    private SceneRenderer mRenderer;//������Ⱦ��    
    
    private float mPreviousY;//�ϴεĴ���λ��Y����
    private float mPreviousX;//�ϴεĴ���λ��X����
    
    private Shader shader;
    private Model model;
    private Camera camera;
    private Light light;

	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //����ʹ��OPENGL ES3.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ
	}
	
	//�����¼��ص�����
    @Override 
    public boolean onTouchEvent(MotionEvent e)
    {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//���㴥�ر�Yλ��
            float dx = x - mPreviousX;//���㴥�ر�Xλ��
            mRenderer.yAngle += dx * TOUCH_SCALE_FACTOR;//������y����ת�Ƕ�
            mRenderer.xAngle+= dy * TOUCH_SCALE_FACTOR;//������x����ת�Ƕ�
            requestRender();//�ػ滭��
        }
        mPreviousY = y;//��¼���ر�λ��
        mPreviousX = x;//��¼���ر�λ��
        return true;
    }

    public void moveObjectLeft(){
	    model.translate(-1,0,0);
    }

    public void moveObjectRight(){
        model.translate(1,0,0);
    }

    public void moveObjectUp(){
        model.translate(0,1,0);
    }

    public void moveObjectDown(){
        model.translate(0,-1,0);
    }

    public void moveCameraLeft(){
	    camera.translate(-0.1f, 0,0);
    }

    public void moveCameraRight(){
	    camera.translate(0.1f, 0, 0);
    }

    public void moveCameraUp(){
	    camera.translate(0,0.1f,0);
    }

    public void moveCameraDown(){
	    camera.translate(0,-0.1f,0);
    }

	private class SceneRenderer implements GLSurfaceView.Renderer
    {
		float yAngle;//��Y����ת�ĽǶ�
    	float xAngle; //��X����ת�ĽǶ�

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //������Ļ����ɫRGBA
            GLES30.glClearColor(0.0f,0.0f,1.0f,1.0f);
            //����ȼ��
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //�򿪱������
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //������ɫ��
            shader = new Shader("vertex.sh","frag.sh", getResources());
            //����Ҫ���Ƶ�����
            model = new Model("ch_t.obj", R.drawable.qhc, MySurfaceView.this.getResources());
            model.translate(0, -16f, -60f);
            //���������
            camera = new Camera(-1, 1, -1, 1, 2, 100,
                    0,0,0,0f,0f,-1f,0f);
            //��ʼ����Դλ��
            light = new Light("uLightLocation",-1, 1, -1, 1, 2, 100,
                    40,10,20,0f,0f,-1f,0f);
        }

        public void onDrawFrame(GL10 gl) 
        { 
        	//�����Ȼ�������ɫ����
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

//            //����ϵ��Զ
//            model.translate(0, -16f, -60f);
//
//            //��Y�ᡢX����ת
//            model.rotate(yAngle, 0, 1, 0);
//            model.rotate(xAngle, 1, 0, 0);

            //ָ��ʹ��ĳ����ɫ������
            shader.use();
            //�����ձ任��������ɫ������
            float[] mMVPMatrix=new float[16];
            Matrix.multiplyMM(mMVPMatrix, 0, camera.mVMatrix, 0, model.currMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, camera.mProjMatrix, 0, mMVPMatrix, 0);
            shader.setMat4f("uMVPMatrix", mMVPMatrix);
            //��λ�á���ת�任��������ɫ������
            shader.setMat4f("uMMatrix", model.currMatrix);
            // ������λ�����ݴ�����Ⱦ����
            shader.setPointer3f("aPosition",false, model.mVertexBuffer);
            //�����㷨�������ݴ�����Ⱦ����
            shader.setPointer3f("aNormal",false, model.mNormalBuffer);
            //�����������������ݴ�����Ⱦ����
            shader.setPointer2f("aTexCoor", false, model.mTexCoorBuffer);
            //�������λ�ô�����ɫ������
            shader.setVec3f("uCamera", camera.cameraFB);
            //����Դλ�ô�����ɫ������
            shader.setVec3f("uLightLocation", light.cameraFB);
            model.draw();
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ�� 
        	GLES30.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            camera.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
        }
    }


}
