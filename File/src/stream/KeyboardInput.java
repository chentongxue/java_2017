package stream;
import java.io.*;
/*
 * ��׼�������
 */
public class KeyboardInput 
{
	public static void main(String args[]) throws IOException
	{
		byte buffer[] = new byte[512];       //���ֽ�������Ϊ������
		
		int count = System.in.read(buffer);  
		     //�ӱ�׼�������ж�ȡ�����ֽ� ��������buffer������ʵ���ֽ���
		
		System.out.println("Count = " + count);
		for(int i = 0; i < count; i++)
			System.out.print(" "+buffer[i]);      //���ֽڷ�ʽ���bufferԪ��ֵ
		for(int i = 0; i < count; i++)
			System.out.print(" "+(char)buffer[i]);//���ַ���ʽ���bufferԪ��ֵ			
	}
}
