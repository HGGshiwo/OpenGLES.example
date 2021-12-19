#version 300 es
precision mediump float;
uniform sampler2D sTexture;//������������
uniform int uSide;//0����ߣ�1���ұ�
//���մӶ�����ɫ�������Ĳ���
in vec4 ambient;
in vec4 diffuse;
in vec4 specular;
in vec2 vTextureCoord;

out vec4 fragColor;//�������ƬԪ��ɫ
void main()                         
{    
   //�����������ɫ����ƬԪ
   vec4 finalColor=texture(sTexture, vTextureCoord);
   //����ƬԪ��ɫֵ
   bool isdraw = ((gl_FragCoord.x < 1116.0) && (uSide == 0)) || ((gl_FragCoord.x > 1116.0) && (uSide == 1));
   if(!isdraw) discard;
   fragColor = finalColor*ambient+finalColor*specular+finalColor*diffuse;
}   