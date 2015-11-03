package stream;
import java.io.*;
/**
 * 使用字节流读写文件，
 * 使用文杰字节输入流实现文件输入操作，使用文件字节输出流实现文件输出操作
 */
public class ByteFile 
{
	private String filename;                                 //文件名
	public ByteFile(String filename)                         //指定文件名的构造方法
	{
		this.filename = filename;                           
	}
	public void writeToFile(byte[] buffer) throws IOException//将缓冲区数据写入指定文件
	{                                                      
		FileOutputStream fout = new FileOutputStream(this.filename);//创建文件输出流对象
		fout.write(buffer);                                  //将缓冲区数据写入输出流
		fout.write('c');     
		fout.close();                                        //关闭输出流
	}
	public void readFromFile() throws IOException
	{
		FileInputStream fin = new FileInputStream(this.filename);//创建文件输出流对象
		System.out.print(this.filename+":");                        
		
		byte[] buffer = new byte[512];                               //字节缓冲区
		int count = 0;
		do{
			count = fin.read(buffer);                                //读取输入流到缓冲区
			for(int i = 0; i < count; i++)
				System.out.print(buffer[i]+"  ");
			System.out.print("count=" + count);
		}while(count!=-1);                                           //输入流未结束时
		fin.close();                                                 //关闭输入流
	}
	public void copyFile(String targetfilename) throws IOException
	{
		FileInputStream fin = new FileInputStream(this.filename);    //创建文件输入流对象
		FileOutputStream fout = new FileOutputStream(targetfilename); //创建文件输出流对象
		byte[] buffer = new byte[512];                               //字节缓冲区
		int count = fin.read(buffer);                                //读取输入流
		
		while(count!=-1)
		{
			fout.write(buffer, 0, count); //写入buffer数组 0~count个元素
			count = fin.read(buffer);     //读取输入流
		}
		System.out.println("CopyFile from "+this.filename+" to "+targetfilename);
	}
	public static void main(String args[]) throws IOException
	{
		//byte[] buffer = {97, 98, 99, 100, 101, 102, 103, 104, 105};
       byte buffer[] = new byte[5];       //以字节数组作为缓冲区
	   System.in.read(buffer);    //从标准输入流中读取若干字节 到缓冲区buffer，返回实际字节数
		ByteFile afile = new ByteFile("ByteFile1.txt");
		afile.writeToFile(buffer);
		afile.readFromFile();
		afile.copyFile("ByteFile2.txt");
	}
}
