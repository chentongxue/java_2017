import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//Fibonacci 输入文件
public class TextFile 
{
	private String filename;
	
	public TextFile(String filename)
	{
		this.filename = filename;
	}
	public void writeToText() throws IOException
	{
		FileWriter fout = new FileWriter(this.filename);
		short a = 0,b = 1,count = 0;
		do
		{
			fout.write(a + "  " + b + "  ");
			a = (short)(a + b);
			b = (short)(a + b);
			count += 2;
			if(count%20 == 0)
				fout.write("\r\n");
		}while(a>0 && b>0);
		fout.close();
	}
	public void readFromText() throws IOException
	{
		FileReader fin = new FileReader(this.filename);
		BufferedReader bin = new BufferedReader(fin);
		System.out.println(this.filename + ": ");
		String aline = "";
		do{
			aline = bin.readLine();            //读取一串字符
			if(aline != null)
				System.out.println( aline );
		}while( aline != null );
		bin.close();
		fin.close();
	}
	public static void main(String args[]) throws IOException
	{
		TextFile afile = new TextFile("FibFile.txt");
		afile.writeToText();//throws IOException
		afile.readFromText();
	}
	
}
