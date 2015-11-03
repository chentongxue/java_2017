package stream;
import java.io.*;
/*
 * 标准输入输出
 */
public class KeyboardInput 
{
	public static void main(String args[]) throws IOException
	{
		byte buffer[] = new byte[512];       //以字节数组作为缓冲区
		
		int count = System.in.read(buffer);  
		     //从标准输入流中读取若干字节 到缓冲区buffer，返回实际字节数
		
		System.out.println("Count = " + count);
		for(int i = 0; i < count; i++)
			System.out.print(" "+buffer[i]);      //按字节方式输出buffer元素值
		for(int i = 0; i < count; i++)
			System.out.print(" "+(char)buffer[i]);//按字符方式输出buffer元素值			
	}
}
