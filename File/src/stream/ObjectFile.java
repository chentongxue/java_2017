package stream;
import java.io.*;

/*
 * 用对象流读写记录式文件//未完转至reset项目
 */
public class ObjectFile 
{
	private String filename;
	public ObjectFile(String filename)
	{
		this.filename = filename;
	}
	public void writeToFile(Object...objs)throws IOException
	{
		FileOutputStream fout = new FileOutputStream(this.filename);
		ObjectOutputStream objout = new ObjectOutputStream(fout);
		for(int i = 0; i < objs.length; i++)
			objout.writeObject(objs[i]);
		objout.close();
		fout.close();
	}
	public String readFromFile() throws IOException
	{
		FileInputStream fin = new FileInputStream(this.filename);
		ObjectInputStream objin = new ObjectInputStream(fin);
		System.out.println(this.filename + ": ");
		String str = "";
		while(true)
		{
			try
			{
				str += objin.readObject().toString()+"\n";
			}
			catch(Exception e)
			{
				break;
			}
			objin.close();
			fin.close();
		}return str;
	}
	public static void main(String args[])
	{
//		person
	}
}
