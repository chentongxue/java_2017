package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CopyFile {
	//递归复制文件，文件夹
	public static void CopyDir(String src, String targ)
	{
		File dir = new File(src);//文件
		if(dir.isDirectory())//是文件夹
		{
			new File(targ + "\\", dir.getName()).mkdir();//创建目录
			File[] files = dir.listFiles();
			for(int i = 0; i<files.length; i++)
			{
				if(files[i].isDirectory())//是文件夹
				{
					CopyDir(files[i].getAbsolutePath(), targ + "\\" + dir.getName() + "\\");//递归创建子目录
				}
				if(files[i].isFile())//复制文件
				{
					copyFile (files[i].getAbsolutePath(),targ  + dir.getName() + "\\" + files[i].getName());
				}
			}
		}	
	}
	public static void copyFile (String src, String targ)
	{
		FileInputStream fin;
		try {
			fin = new FileInputStream(src);
			FileOutputStream fout = new FileOutputStream(targ); //创建文件输出流对象
			byte[] buffer = new byte[512];                      //字节缓冲区
			int count = fin.read(buffer);                       //读取输入流
			while(count!=-1)
			{
				fout.write(buffer, 0, count); //写入buffer数组 0~count个元素
				count = fin.read(buffer);     //读取输入流
			}
		} catch (Exception e) {
			e.printStackTrace();
		}   	
	}

}
