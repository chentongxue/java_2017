import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class FileList 
{
	int count_dirs = 0, count_files;                    //目录数 ，文件数
	long byte_files = 0; 
	public FileList()
	{
		count(new File("."));                           //创建当前目录
		System.out.println( "共有" + count_files + "个文件，总字节为 " + byte_files );
		System.out.println( "共有" + count_dirs + "个目录 " );
	}
	private void count(File dir)                        //dir目录中文文件列表，递归算法
	{
		System.out.println( dir.getAbsolutePath() );    //显示dir的绝对路径
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        File[] files = dir.listFiles();                 //返回当前目录所有文件
        for( int i = 0; i < files.length; i ++ )
        {
        	System.out.print(files[i].getName());         //显示文件名
        	if(files[i].isFile())                         //判断是否是文件
        	{
        		System.out.print(files[i].length()+"B\t");//显示文件长度
        		count_files++;
        		byte_files += files[i].length();          //文件总字节数
        	}
        	else
        	{
        		System.out.print("<DIR>\t");
        		count_dirs++;
        	}
        	System.out.println(   sdf.format(  new Date( files[i].lastModified() )  )   );
        }
        for(int i = 0; i < files.length; i++) //所有子目录中文件列表长度计算
        	if(files[i].isDirectory())        //判断对象是否为目录
        		count(files[i]);              //子目录的文件列表和长度计算， 递归调用
	}
	public static void main(String args[])throws IOException
	{
		new FileList();
	}
}
