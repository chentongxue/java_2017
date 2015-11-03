import java.io.File;
import java.io.FilenameFilter;

/**
* 带过滤器的文件名列表，实现文件过滤方法
*/
public class DirFilter implements FilenameFilter 
{
	private String prefix = "",extension = "";//文件前缀 文件扩展名
	public DirFilter(String filterstr, File dir)
	{
		filterstr = filterstr.toLowerCase();       //把字符串转换为小写
		int i = filterstr.indexOf('*');            //寻找通配符
		if(i>0)
			this.prefix = filterstr.substring(0,i);//注意
		int j = filterstr.indexOf('.');  
		if(j>0)
		{
			this.extension = filterstr.substring(j+1);
			String[] filenames = dir.list(this);   //获得指定目录中带过滤器的文件名列表
			for(i = 0; i < filenames.length; i++)
				System.out.println(filenames[i]);
		}
	}
	public DirFilter()
	{
		this("*.*",new File(".",""));
	}
	@Override
	public boolean accept(File dir, String filename)//实现过滤操作
	{
		filename = filename.toLowerCase();
		return filename.startsWith(prefix)&&filename.endsWith(extension);
	}

	public static void main(String[] args)    //filterstr指定过滤串，dir指定目录
	{
		new DirFilter("*.java", new File(".","src"));//当前目录的src子目录
	}

}
