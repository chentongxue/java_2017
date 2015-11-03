package stream;
import java.io.*;
/**
 * 数据字节流读写整数文件//文件乱码
 * 将整数数列写入一个整数类型文件
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
		    //dout.write(f);           //向输出流写入ASC会乱码
		
		dout.close();//关闭数据流
		fout.close();//关闭文件流
	}
	public void readFromFile()throws IOException
	{
		FileInputStream fin = new FileInputStream(this.filename);
		DataInputStream in = new DataInputStream(fin);
		System.out.println(this.filename+":");
		
		while(true)
			try
		    {
				int i = in.readInt();  //从数据流中读取一个整数
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
