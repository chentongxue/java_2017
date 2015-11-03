package file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CountFile 
{
	public static long maxlines = 0;
	public static long minlines = 0;
	public static long filecounts = 0;
	public static void main(String[] args) 
	{
//		String filepath = "D:\\Workspaces\\MyEclipse 8.5\\��ѧ����\\src\\��ѧ����05��������propertyName��ֵ����Ϊvalue\\ChangeValue.java";
		countDirLines("file");
		System.out.print("java��������Ϊ"+maxlines);
		System.out.print("\n���ع���Ϊ"+minlines);
	}
	public static void initLinesCount()
	{
		maxlines = 0;
		minlines = 0;
		filecounts = 0;
	}
	public static void countDirLines(String filepath)
	{
		File dir = new File(filepath);//�ļ�
		if(dir.isDirectory())//���ļ���
		{
			File[] files = dir.listFiles();
			for(int i = 0; i<files.length; i++)
			{
				if(files[i].isDirectory())//���ļ���
				{
					countDirLines(files[i].getAbsolutePath());
				}
				if(files[i].isFile())//�����ļ�
				{
					if(files[i].getName().endsWith("txt"))
					{
						countFileLines(files[i].getAbsolutePath());
					}
				}
			}
		}	
	}
	public static void countFileLines(String filepath)
	{
		if(!filepath.endsWith("txt"))
			return;
		filecounts++;
		BufferedReader buf;
		try {
			buf = new BufferedReader(new FileReader(filepath));
			String line = null;
			while((line = buf.readLine())!=null)
			{
//				System.err.println(line);
				if(line.contains("兑换")){
					System.err.println(filepath);
				};
			}
		} catch (Exception e) {e.printStackTrace();}	
	}
}
