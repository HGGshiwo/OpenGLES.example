package com.example.myapplication.Object;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import com.example.myapplication.Shader.Shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Model extends Object3D{
    int vCount=0;

    public FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    public FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    public FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲

    int texId;//纹理

    public Model(
            float[] vertices,
            float[] normals,
            float texCoors[],
            int drawableId,
            Resources resources
    ){
        super();
        initTexture(drawableId, resources);
        initVertexData(vertices, normals, texCoors);
    }


    public Model(String fname, int drawableId, Resources r){
        super();
        //原始顶点坐标列表--直接从obj文件中加载
        ArrayList<Float> alv=new ArrayList<Float>();
        //顶点组装面索引列表--根据面的信息从文件中加载
        ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();
        //结果顶点坐标列表--按面组织好
        ArrayList<Float> alvResult=new ArrayList<Float>();
        //平均前各个索引对应的点的法向量集合Map
        //此HashMap的key为点的索引， value为点所在的各个面的法向量的集合
        HashMap<Integer, HashSet<Normal>> hmn=new HashMap<Integer,HashSet<Normal>>();
        //原始纹理坐标列表
        ArrayList<Float> alt=new ArrayList<Float>();
        //结果纹理坐标列表
        ArrayList<Float> altResult=new ArrayList<Float>();

        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps = null;

            //扫描文件，根据行类型的不同执行不同的处理逻辑
            while ((temps = br.readLine()) != null) {//读取一行文本

                String[] tempsa = temps.split("[ ]+");//将文本行用空格符切分
                if (tempsa[0].trim().equals("v")) {//顶点坐标行
                    //若为顶点坐标行则提取出此顶点的XYZ坐标添加到原始顶点坐标列表中
                    alv.add(Float.parseFloat(tempsa[1]));
                    alv.add(Float.parseFloat(tempsa[2]));
                    alv.add(Float.parseFloat(tempsa[3]));
                } else if (tempsa[0].trim().equals("vt")) {//纹理坐标行
                    //若为纹理坐标行则提取ST坐标并添加进原始纹理坐标列表中
                    alt.add(Float.parseFloat(tempsa[1]));//提取出S纹理坐标
                    alt.add(1 - Float.parseFloat(tempsa[2]));    //提取出T纹理坐标
                } else if (tempsa[0].trim().equals("f")) {//面数据行
                    /*
                     *若为三角形面行则根据 组成面的顶点的索引从原始顶点坐标列表中
                     *提取相应的顶点坐标值添加到结果顶点坐标列表中，同时根据三个
                     *顶点的坐标计算出此面的法向量并添加到平均前各个索引对应的点
                     *的法向量集合组成的Map中
                     */

                    int[] index = new int[3];//三个顶点索引值的数组

                    //计算第0个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[0] = Integer.parseInt(tempsa[1].split("/")[0]) - 1;
                    float x0 = alv.get(3 * index[0]);
                    float y0 = alv.get(3 * index[0] + 1);
                    float z0 = alv.get(3 * index[0] + 2);
                    alvResult.add(x0);
                    alvResult.add(y0);
                    alvResult.add(z0);

                    //计算第1个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[1] = Integer.parseInt(tempsa[2].split("/")[0]) - 1;
                    float x1 = alv.get(3 * index[1]);
                    float y1 = alv.get(3 * index[1] + 1);
                    float z1 = alv.get(3 * index[1] + 2);
                    alvResult.add(x1);
                    alvResult.add(y1);
                    alvResult.add(z1);

                    //计算第2个顶点的索引，并获取此顶点的XYZ三个坐标
                    index[2] = Integer.parseInt(tempsa[3].split("/")[0]) - 1;
                    float x2 = alv.get(3 * index[2]);
                    float y2 = alv.get(3 * index[2] + 1);
                    float z2 = alv.get(3 * index[2] + 2);
                    alvResult.add(x2);
                    alvResult.add(y2);
                    alvResult.add(z2);

                    //记录此面的顶点索引
                    alFaceIndex.add(index[0]);
                    alFaceIndex.add(index[1]);
                    alFaceIndex.add(index[2]);

                    //通过三角形面两个边向量0-1，0-2求叉积得到此面的法向量
                    //求0号点到1号点的向量
                    float vxa = x1 - x0;
                    float vya = y1 - y0;
                    float vza = z1 - z0;
                    //求0号点到2号点的向量
                    float vxb = x2 - x0;
                    float vyb = y2 - y0;
                    float vzb = z2 - z0;
                    //通过求两个向量的叉积计算法向量
                    float[] vNormal = vectorNormal(getCrossProduct
                            (
                                    vxa, vya, vza, vxb, vyb, vzb
                            ));
                    for (int tempInxex : index) {//记录每个索引点的法向量到平均前各个索引对应的点的法向量集合组成的Map中
                        //获取当前索引对应点的法向量集合
                        HashSet<Normal> hsn = hmn.get(tempInxex);
                        if (hsn == null) {//若集合不存在则创建
                            hsn = new HashSet<Normal>();
                        }
                        //将此点的法向量添加到集合中
                        //由于Normal类重写了equals方法，因此同样的法向量不会重复出现在此点
                        //对应的法向量集合中
                        hsn.add(new Normal(vNormal[0], vNormal[1], vNormal[2]));
                        //将集合放进HsahMap中
                        hmn.put(tempInxex, hsn);
                    }

                    //将三角形3个顶点的纹理坐标数据组织到结果纹理坐标列表中
                    int indexTex = Integer.parseInt(tempsa[1].split("/")[1]) - 1;//获取纹理坐标编号
                    //第0个顶点的纹理坐标
                    altResult.add(alt.get(indexTex * 2));
                    altResult.add(alt.get(indexTex * 2 + 1));

                    indexTex = Integer.parseInt(tempsa[2].split("/")[1]) - 1;//获取纹理坐标编号
                    //第1个顶点的纹理坐标
                    altResult.add(alt.get(indexTex * 2));
                    altResult.add(alt.get(indexTex * 2 + 1));

                    indexTex = Integer.parseInt(tempsa[3].split("/")[1]) - 1;//获取纹理坐标编号
                    //第2个顶点的纹理坐标
                    altResult.add(alt.get(indexTex * 2));
                    altResult.add(alt.get(indexTex * 2 + 1));
                }
            }

            //生成顶点数组
            int size = alvResult.size();
            float[] vXYZ = new float[size];
            for (int i = 0; i < size; i++) {
                vXYZ[i] = alvResult.get(i);
            }

            //生成法向量数组
            float[] nXYZ = new float[alFaceIndex.size() * 3];
            int c = 0;
            for (Integer i : alFaceIndex) {
                //根据当前点的索引从Map中取出一个法向量的集合
                HashSet<Normal> hsn = hmn.get(i);
                //求出平均法向量
                float[] tn = Normal.getAverage(hsn);
                //将计算出的平均法向量存放到法向量数组中
                nXYZ[c++] = tn[0];
                nXYZ[c++] = tn[1];
                nXYZ[c++] = tn[2];
            }

            //生成纹理数组
            size = altResult.size();
            float[] tST = new float[size];//用于存放结果纹理坐标数据的数组
            for (int i = 0; i < size; i++) {//将纹理坐标数据存入数组
                tST[i] = altResult.get(i);
            }
            initVertexData(vXYZ,nXYZ,tST);
            initTexture(drawableId, r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initVertexData(float[] vertices,float[] normals,float texCoors[]) {
        //顶点坐标数据的初始化
        vCount=vertices.length/3;
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        //顶点法向量数据的初始化
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置

        //顶点纹理坐标数据的初始化
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTexCoorBuffer = tbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoors);//向缓冲区中放入顶点纹理坐标数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }

    public void initTexture(int drawableId, Resources resources){
        //生成纹理ID
        int[] textures = new int[1];
        GLES30.glGenTextures
                (
                        1,          //产生的纹理id的数量
                        textures,   //纹理id的数组
                        0           //偏移量
                );
        int textureId=textures[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);

        //通过输入流加载图片===============begin===================
        InputStream is = resources.openRawResource(drawableId);
        Bitmap bitmapTmp;
        try
        {
            bitmapTmp = BitmapFactory.decodeStream(is);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        //通过输入流加载图片===============end=====================
        GLUtils.texImage2D
                (
                        GLES30.GL_TEXTURE_2D, //纹理类型
                        0,
                        GLUtils.getInternalFormat(bitmapTmp),
                        bitmapTmp, //纹理图像
                        GLUtils.getType(bitmapTmp),
                        0 //纹理边框尺寸
                );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        texId = textureId;
    }

    public void draw() {
        //绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);//启用0号纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);//绑定纹理
        //绘制加载的物体
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }

    //求两个向量的叉积
    public static float[] getCrossProduct(float x1,float y1,float z1,float x2,float y2,float z2)
    {
        //求出两个矢量叉积矢量在XYZ轴的分量ABC
        float A=y1*z2-y2*z1;
        float B=z1*x2-z2*x1;
        float C=x1*y2-x2*y1;

        return new float[]{A,B,C};
    }

    //向量规格化
    public static float[] vectorNormal(float[] vector)
    {
        //求向量的模
        float module=(float)Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]);
        return new float[]{vector[0]/module,vector[1]/module,vector[2]/module};
    }
}
