package stream;
import java.io.*;
/**
 * ʹ���ֽ�����д�ļ���
 * ʹ���Ľ��ֽ�������ʵ���ļ����������ʹ���ļ��ֽ������ʵ���ļ��������
 */
public class ByteFile 
{
	private String filename;                                 //�ļ���
	public ByteFile(String filename)                         //ָ���ļ����Ĺ��췽��
	{
		this.filename = filename;                           
	}
	public void writeToFile(byte[] buffer) throws IOException//������������д��ָ���ļ�
	{                                                      
		FileOutputStream fout = new FileOutputStream(this.filename);//�����ļ����������
		fout.write(buffer);                                  //������������д�������
		fout.write('c');     
		fout.close();                                        //�ر������
	}
	public void readFromFile() throws IOException
	{
		FileInputStream fin = new FileInputStream(this.filename);//�����ļ����������
		System.out.print(this.filename+":");                        
		
		byte[] buffer = new byte[512];                               //�ֽڻ�����
		int count = 0;
		do{
			count = fin.read(buffer);                                //��ȡ��������������
			for(int i = 0; i < count; i++)
				System.out.print(buffer[i]+"  ");
			System.out.print("count=" + count);
		}while(count!=-1);                                           //������δ����ʱ
		fin.close();                                                 //�ر�������
	}
	public void copyFile(String targetfilename) throws IOException
	{
		FileInputStream fin = new FileInputStream(this.filename);    //�����ļ�����������
		FileOutputStream fout = new FileOutputStream(targetfilename); //�����ļ����������
		byte[] buffer = new byte[512];                               //�ֽڻ�����
		int count = fin.read(buffer);                                //��ȡ������
		
		while(count!=-1)
		{
			fout.write(buffer, 0, count); //д��buffer���� 0~count��Ԫ��
			count = fin.read(buffer);     //��ȡ������
		}
		System.out.println("CopyFile from "+this.filename+" to "+targetfilename);
	}
	public static void main(String args[]) throws IOException
	{
		//byte[] buffer = {97, 98, 99, 100, 101, 102, 103, 104, 105};
       byte buffer[] = new byte[5];       //���ֽ�������Ϊ������
	   System.in.read(buffer);    //�ӱ�׼�������ж�ȡ�����ֽ� ��������buffer������ʵ���ֽ���
		ByteFile afile = new ByteFile("ByteFile1.txt");
		afile.writeToFile(buffer);
		afile.readFromFile();
		afile.copyFile("ByteFile2.txt");
	}
}
