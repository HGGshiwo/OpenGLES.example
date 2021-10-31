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

    public FloatBuffer mVertexBuffer;//�����������ݻ���
    public FloatBuffer   mNormalBuffer;//���㷨�������ݻ���
    public FloatBuffer   mTexCoorBuffer;//���������������ݻ���

    int texId;//����

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
        //ԭʼ���������б�--ֱ�Ӵ�obj�ļ��м���
        ArrayList<Float> alv=new ArrayList<Float>();
        //������װ�������б�--���������Ϣ���ļ��м���
        ArrayList<Integer> alFaceIndex=new ArrayList<Integer>();
        //������������б�--������֯��
        ArrayList<Float> alvResult=new ArrayList<Float>();
        //ƽ��ǰ����������Ӧ�ĵ�ķ���������Map
        //��HashMap��keyΪ��������� valueΪ�����ڵĸ�����ķ������ļ���
        HashMap<Integer, HashSet<Normal>> hmn=new HashMap<Integer,HashSet<Normal>>();
        //ԭʼ���������б�
        ArrayList<Float> alt=new ArrayList<Float>();
        //������������б�
        ArrayList<Float> altResult=new ArrayList<Float>();

        try {
            InputStream in = r.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String temps = null;

            //ɨ���ļ������������͵Ĳ�ִͬ�в�ͬ�Ĵ����߼�
            while ((temps = br.readLine()) != null) {//��ȡһ���ı�

                String[] tempsa = temps.split("[ ]+");//���ı����ÿո���з�
                if (tempsa[0].trim().equals("v")) {//����������
                    //��Ϊ��������������ȡ���˶����XYZ������ӵ�ԭʼ���������б���
                    alv.add(Float.parseFloat(tempsa[1]));
                    alv.add(Float.parseFloat(tempsa[2]));
                    alv.add(Float.parseFloat(tempsa[3]));
                } else if (tempsa[0].trim().equals("vt")) {//����������
                    //��Ϊ��������������ȡST���겢��ӽ�ԭʼ���������б���
                    alt.add(Float.parseFloat(tempsa[1]));//��ȡ��S��������
                    alt.add(1 - Float.parseFloat(tempsa[2]));    //��ȡ��T��������
                } else if (tempsa[0].trim().equals("f")) {//��������
                    /*
                     *��Ϊ��������������� �����Ķ����������ԭʼ���������б���
                     *��ȡ��Ӧ�Ķ�������ֵ��ӵ�������������б��У�ͬʱ��������
                     *�����������������ķ���������ӵ�ƽ��ǰ����������Ӧ�ĵ�
                     *�ķ�����������ɵ�Map��
                     */

                    int[] index = new int[3];//������������ֵ������

                    //�����0�����������������ȡ�˶����XYZ��������
                    index[0] = Integer.parseInt(tempsa[1].split("/")[0]) - 1;
                    float x0 = alv.get(3 * index[0]);
                    float y0 = alv.get(3 * index[0] + 1);
                    float z0 = alv.get(3 * index[0] + 2);
                    alvResult.add(x0);
                    alvResult.add(y0);
                    alvResult.add(z0);

                    //�����1�����������������ȡ�˶����XYZ��������
                    index[1] = Integer.parseInt(tempsa[2].split("/")[0]) - 1;
                    float x1 = alv.get(3 * index[1]);
                    float y1 = alv.get(3 * index[1] + 1);
                    float z1 = alv.get(3 * index[1] + 2);
                    alvResult.add(x1);
                    alvResult.add(y1);
                    alvResult.add(z1);

                    //�����2�����������������ȡ�˶����XYZ��������
                    index[2] = Integer.parseInt(tempsa[3].split("/")[0]) - 1;
                    float x2 = alv.get(3 * index[2]);
                    float y2 = alv.get(3 * index[2] + 1);
                    float z2 = alv.get(3 * index[2] + 2);
                    alvResult.add(x2);
                    alvResult.add(y2);
                    alvResult.add(z2);

                    //��¼����Ķ�������
                    alFaceIndex.add(index[0]);
                    alFaceIndex.add(index[1]);
                    alFaceIndex.add(index[2]);

                    //ͨ��������������������0-1��0-2�����õ�����ķ�����
                    //��0�ŵ㵽1�ŵ������
                    float vxa = x1 - x0;
                    float vya = y1 - y0;
                    float vza = z1 - z0;
                    //��0�ŵ㵽2�ŵ������
                    float vxb = x2 - x0;
                    float vyb = y2 - y0;
                    float vzb = z2 - z0;
                    //ͨ�������������Ĳ�����㷨����
                    float[] vNormal = vectorNormal(getCrossProduct
                            (
                                    vxa, vya, vza, vxb, vyb, vzb
                            ));
                    for (int tempInxex : index) {//��¼ÿ��������ķ�������ƽ��ǰ����������Ӧ�ĵ�ķ�����������ɵ�Map��
                        //��ȡ��ǰ������Ӧ��ķ���������
                        HashSet<Normal> hsn = hmn.get(tempInxex);
                        if (hsn == null) {//�����ϲ������򴴽�
                            hsn = new HashSet<Normal>();
                        }
                        //���˵�ķ�������ӵ�������
                        //����Normal����д��equals���������ͬ���ķ����������ظ������ڴ˵�
                        //��Ӧ�ķ�����������
                        hsn.add(new Normal(vNormal[0], vNormal[1], vNormal[2]));
                        //�����ϷŽ�HsahMap��
                        hmn.put(tempInxex, hsn);
                    }

                    //��������3���������������������֯��������������б���
                    int indexTex = Integer.parseInt(tempsa[1].split("/")[1]) - 1;//��ȡ����������
                    //��0���������������
                    altResult.add(alt.get(indexTex * 2));
                    altResult.add(alt.get(indexTex * 2 + 1));

                    indexTex = Integer.parseInt(tempsa[2].split("/")[1]) - 1;//��ȡ����������
                    //��1���������������
                    altResult.add(alt.get(indexTex * 2));
                    altResult.add(alt.get(indexTex * 2 + 1));

                    indexTex = Integer.parseInt(tempsa[3].split("/")[1]) - 1;//��ȡ����������
                    //��2���������������
                    altResult.add(alt.get(indexTex * 2));
                    altResult.add(alt.get(indexTex * 2 + 1));
                }
            }

            //���ɶ�������
            int size = alvResult.size();
            float[] vXYZ = new float[size];
            for (int i = 0; i < size; i++) {
                vXYZ[i] = alvResult.get(i);
            }

            //���ɷ���������
            float[] nXYZ = new float[alFaceIndex.size() * 3];
            int c = 0;
            for (Integer i : alFaceIndex) {
                //���ݵ�ǰ���������Map��ȡ��һ���������ļ���
                HashSet<Normal> hsn = hmn.get(i);
                //���ƽ��������
                float[] tn = Normal.getAverage(hsn);
                //���������ƽ����������ŵ�������������
                nXYZ[c++] = tn[0];
                nXYZ[c++] = tn[1];
                nXYZ[c++] = tn[2];
            }

            //������������
            size = altResult.size();
            float[] tST = new float[size];//���ڴ�Ž�������������ݵ�����
            for (int i = 0; i < size; i++) {//�������������ݴ�������
                tST[i] = altResult.get(i);
            }
            initVertexData(vXYZ,nXYZ,tST);
            initTexture(drawableId, r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initVertexData(float[] vertices,float[] normals,float texCoors[]) {
        //�����������ݵĳ�ʼ��
        vCount=vertices.length/3;
        //���������������ݻ���
        //vertices.length*4����Ϊһ�������ĸ��ֽ�
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVertexBuffer = vbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mVertexBuffer.put(vertices);//�򻺳����з��붥����������
        mVertexBuffer.position(0);//���û�������ʼλ��

        //���㷨�������ݵĳ�ʼ��
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);
        cbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mNormalBuffer = cbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mNormalBuffer.put(normals);//�򻺳����з��붥�㷨��������
        mNormalBuffer.position(0);//���û�������ʼλ��

        //���������������ݵĳ�ʼ��
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mTexCoorBuffer = tbb.asFloatBuffer();//ת��ΪFloat�ͻ���
        mTexCoorBuffer.put(texCoors);//�򻺳����з��붥��������������
        mTexCoorBuffer.position(0);//���û�������ʼλ��
    }

    public void initTexture(int drawableId, Resources resources){
        //��������ID
        int[] textures = new int[1];
        GLES30.glGenTextures
                (
                        1,          //����������id������
                        textures,   //����id������
                        0           //ƫ����
                );
        int textureId=textures[0];
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_REPEAT);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_REPEAT);

        //ͨ������������ͼƬ===============begin===================
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
        //ͨ������������ͼƬ===============end=====================
        GLUtils.texImage2D
                (
                        GLES30.GL_TEXTURE_2D, //��������
                        0,
                        GLUtils.getInternalFormat(bitmapTmp),
                        bitmapTmp, //����ͼ��
                        GLUtils.getType(bitmapTmp),
                        0 //����߿�ߴ�
                );
        bitmapTmp.recycle(); 		  //������سɹ����ͷ�ͼƬ
        texId = textureId;
    }

    public void draw() {
        //������
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);//����0������
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texId);//������
        //���Ƽ��ص�����
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount);
    }

    //�����������Ĳ��
    public static float[] getCrossProduct(float x1,float y1,float z1,float x2,float y2,float z2)
    {
        //�������ʸ�����ʸ����XYZ��ķ���ABC
        float A=y1*z2-y2*z1;
        float B=z1*x2-z2*x1;
        float C=x1*y2-x2*y1;

        return new float[]{A,B,C};
    }

    //�������
    public static float[] vectorNormal(float[] vector)
    {
        //��������ģ
        float module=(float)Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]+vector[2]*vector[2]);
        return new float[]{vector[0]/module,vector[1]/module,vector[2]/module};
    }
}
