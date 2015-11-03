package stream;
import java.io.*;
/**
 * �����ֽ�����д�����ļ�//�ļ�����
 * ����������д��һ�����������ļ�
 */
public class IntFile 
{
	private String filename;
	public IntFile(String filename)
	{
		this.filename = filename;
	}
	public void writeToFile(int[] array) throws IOException
	{
		FileOutputStream fout = new FileOutputStream(this.filename);
		DataOutputStream dout = new DataOutputStream(fout);
		
		short i = 0,j = 1;
		do{
			dout.writeInt(i);
			dout.writeInt(j);
			i = (short)(i+j);
			j = (short)(i+j);
		}while(i > 0);
	
		
		//for(int i = 0; i < array.length; i++)
			//  dout.writeInt(array[i]);
		    //dout.write(f);           //�������д��ASC������
		
		dout.close();//�ر�������
		fout.close();//�ر��ļ���
	}
	public void readFromFile()throws IOException
	{
		FileInputStream fin = new FileInputStream(this.filename);
		DataInputStream in = new DataInputStream(fin);
		System.out.println(this.filename+":");
		
		while(true)
			try
		    {
				int i = in.readInt();  //���������ж�ȡһ������
				System.out.print(i+" ");
		    }
		catch(EOFException e)
		{
			break;
		}
		System.out.println();
		in.close();
		fin.close();
	}
	public static void main(String[] args) throws IOException 
	{
		IntFile afile = new IntFile("FibIntFile.txt");
		int[] array = {1, 2, 3, 4, 5};
		afile.writeToFile(array);
		afile.readFromFile();
	}

}
