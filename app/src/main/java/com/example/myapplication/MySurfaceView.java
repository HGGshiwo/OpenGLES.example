package com.example.myapplication;//声明包
import java.io.IOException;
import java.io.InputStream;

import android.opengl.GLSurfaceView;
import android.opengl.GLES30;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;

import com.example.myapplication.Camera.Camera;
import com.example.myapplication.Camera.Light;
import com.example.myapplication.Object.Model;
import com.example.myapplication.Shader.Shader;

class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 9.0f/320;//角度缩放比例
    private SceneRenderer mRenderer;//场景渲染器    
    
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标
    
    private Shader shader;
    private Model model;
    private Model table;
    private Camera rCamera;
    private Camera lCamera;
    private Light light;
    private float lastDistance;

	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //设置使用OPENGL ES3.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
	    lastDistance = 0.5f;
	}
	
	//触摸事件回调方法
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
            float dy = y - mPreviousY;//计算触控笔Y位移
            float dx = x - mPreviousX;//计算触控笔X位移
            float yAngle = dx * TOUCH_SCALE_FACTOR;//设置沿y轴旋转角度
            float xAngle = dy * TOUCH_SCALE_FACTOR;//设置沿x轴旋转角度

            MainActivity mainActivity = (MainActivity)getContext();
            switch (mainActivity.mode){
                case OBJECT:
                    rotateObjectUp(yAngle);
                    break;
                case CAMERA:
                    rotateCameraRight(-xAngle);
                    rotateCameraUp(yAngle);
                    break;
            }
            requestRender();//重绘画面
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

    public void moveObjectLeft(){
	    model.translate(-1,0,0);
    }

    public void moveObjectRight(){
        model.translate(1,0,0);
    }

    public void moveObjectForward(){
        model.translate(0,0,1);
    }

    public void moveObjectBack(){
        model.translate(0,0,-1);
    }

    public void rotateObjectUp(float angle){
        model.rotate(angle,0,1,0);
    }

    public void moveCameraLeft(){
        float [] rightDistance = Model.vectorNormal(
                Model.getCrossProduct(
                        lCamera.up[0],
                        lCamera.up[1],
                        lCamera.up[2],
                        lCamera.front[0],
                        lCamera.front[1],
                        lCamera.front[2]
                ));

        lCamera.translate(
                -0.5f*rightDistance[0],
                0,
                -0.5f*rightDistance[2]
        );

        rightDistance = Model.vectorNormal(
                Model.getCrossProduct(
                        rCamera.up[0],
                        rCamera.up[1],
                        rCamera.up[2],
                        rCamera.front[0],
                        rCamera.front[1],
                        rCamera.front[2]
                ));

        rCamera.translate(
                -0.5f*rightDistance[0],
                0,
                -0.5f*rightDistance[2]
        );
    }

    public void moveCameraRight(){
	    float [] rightDistance = Model.vectorNormal(
	            Model.getCrossProduct(
	                    lCamera.up[0],
                        lCamera.up[1],
                        lCamera.up[2],
                        lCamera.front[0],
                        lCamera.front[1],
                        lCamera.front[2]
                ));

	    lCamera.translate(
	            0.5f*rightDistance[0],
                0,
                0.5f*rightDistance[2]
        );

        rightDistance = Model.vectorNormal(
                Model.getCrossProduct(
                        rCamera.up[0],
                        rCamera.up[1],
                        rCamera.up[2],
                        rCamera.front[0],
                        rCamera.front[1],
                        rCamera.front[2]
                ));

        rCamera.translate(
                0.5f*rightDistance[0],
                0,
                0.5f*rightDistance[2]
        );
	}

	public void setDistance(float distance){
	    float deltaDistance = distance-lastDistance;
        float [] rightDistance = Model.vectorNormal(
                Model.getCrossProduct(
                        lCamera.up[0],
                        lCamera.up[1],
                        lCamera.up[2],
                        lCamera.front[0],
                        lCamera.front[1],
                        lCamera.front[2]
                ));

        lCamera.translate(
                -deltaDistance*rightDistance[0],
                0,
                -deltaDistance*rightDistance[2]
        );

        rightDistance = Model.vectorNormal(
                Model.getCrossProduct(
                        rCamera.up[0],
                        rCamera.up[1],
                        rCamera.up[2],
                        rCamera.front[0],
                        rCamera.front[1],
                        rCamera.front[2]
                ));

        rCamera.translate(
                deltaDistance*rightDistance[0],
                0,
                deltaDistance*rightDistance[2]
        );
        lastDistance = distance;
    }

    public void moveCameraForward(){
	    float [] front = lCamera.front;
	    lCamera.translate(0.5f*front[0],0,0.5f*front[2]);
        front = rCamera.front;
        rCamera.translate(0.5f*front[0],0,0.5f*front[2]);
    }

    public void moveCameraBack(){
        float [] front = lCamera.front;
	    lCamera.translate(-0.5f*front[0],0,-0.5f*front[2]);
        front = rCamera.front;
        rCamera.translate(-0.5f*front[0],0,-0.5f*front[2]);
    }

    public void rotateCameraRight(float angle){

        float [] right = Model.vectorNormal(
                Model.getCrossProduct(
                        lCamera.up[0],
                        lCamera.up[1],
                        lCamera.up[2],
                        lCamera.front[0],
                        lCamera.front[1],
                        lCamera.front[2]
                ));
        lCamera.rotate(angle, right[0], right[1], right[2]);
        right = Model.vectorNormal(
                Model.getCrossProduct(
                        rCamera.up[0],
                        rCamera.up[1],
                        rCamera.up[2],
                        rCamera.front[0],
                        rCamera.front[1],
                        rCamera.front[2]
                ));
        rCamera.rotate(angle, right[0], right[1], right[2]);
    }

    public void rotateCameraUp(float angle){
	    float radians = (float) Math.toRadians(angle);
        lCamera.rotate(angle, 0, 1, 0);
        lCamera.translate(
                lastDistance-lastDistance*(float)Math.cos(radians),
                0,
                lastDistance*(float)Math.sin(radians)
        );
        rCamera.rotate(angle, 0, 1, 0);
        rCamera.translate(
                -lastDistance+lastDistance*(float)Math.cos(radians),
                0,
                -lastDistance*(float)Math.sin(radians)
        );
    }

    public void moveLightLeft(){
        float [] rightDistance = Model.vectorNormal(
                Model.getCrossProduct(
                        light.up[0],
                        light.up[1],
                        light.up[2],
                        light.front[0],
                        light.front[1],
                        light.front[2]
                ));

        light.translate(
                0.5f*rightDistance[0],
                0,
                0.5f*rightDistance[2]
        );
    }

    public void moveLightRight(){
        float [] rightDistance = Model.vectorNormal(
                Model.getCrossProduct(
                        light.up[0],
                        light.up[1],
                        light.up[2],
                        light.front[0],
                        light.front[1],
                        light.front[2]
                ));

        light.translate(
                -0.5f*rightDistance[0],
                0,
                -0.5f*rightDistance[2]
        );
    }

    public void moveLightBack(){
        float [] front = light.front;
        light.translate(-0.5f*front[0],0,-0.5f*front[2]);
    }

    public void moveLightForward(){
        float [] front = light.front;
        light.translate(0.5f*front[0],0,0.5f*front[2]);
    }

    public void setAmbient(int value){
	    light.setAmbient(value*0.003f);
    }

    public void setDiffuse(int value){
	    light.setDiffuse(value*0.018f);
    }

    public void setSecular(int value){
	    light.setSpecular(value*0.008f);
    }

    public void setShininess(int value){
	    model.shininess = value*1f;
    }

	private class SceneRenderer implements GLSurfaceView.Renderer {

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES30.glClearColor(0.0f,0.0f,1.0f,1.0f);
            //打开深度检测
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //打开背面剪裁
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            //加载着色器
            shader = new Shader("vertex.sh","frag.sh", getResources());
            //加载要绘制的物体
            model = new Model("ch_t.obj", R.drawable.qhc, MySurfaceView.this.getResources());
            model.translate(0, -2f, -5f);
            model.scale(0.05f,0.05f,0.05f);

            table = new Model("ganbox.obj", R.drawable.ganbox, MySurfaceView.this.getResources());
            table.translate(0,-3,-5);
            table.scale(3,3,3);

            //设置照相机
            rCamera = new Camera(-1, 1, -1, 1, 2, 100,
                    -0.5f,0,0);
            lCamera = new Camera(-1, 1, -1, 1, 2, 100,
                    0.5f,0,0);
            //初始化光源位置
            light = new Light(-1, 1, -1, 1, 2, 100,
                    40,10,20);
        }

        public void onDrawFrame(GL10 gl) 
        { 
        	//清除深度缓冲与颜色缓冲
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            //指定使用某套着色器程序
            shader.use();
            //将摄像机位置传入着色器程序
            shader.setVec3f("uCamera", lCamera.positionBuffer);
            //将光源位置传入着色器程序
            shader.setVec3f("uLightLocation", light.positionBuffer);
            //将环境光强度传入着色器程序
            shader.setFloat("uLightAmbient", light.ambient);
            //将散射光强度传入着色器程序
            shader.setFloat("uLightDiffuse", light.diffuse);
            //将镜面光强度传入着色器程序
            shader.setFloat("uLightSpecular", light.specular);
            //绘制模型
            model.draw(shader, lCamera, 0);
            model.draw(shader, rCamera, 1);
            table.draw(shader, lCamera, 0);
            table.draw(shader, rCamera, 1);
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES30.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            rCamera.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
            lCamera.setProjectFrustum(-ratio, ratio, -1, 1, 2, 100);
        }
    }
}
