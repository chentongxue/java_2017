import java.io.File;
import java.io.FilenameFilter;

/**
* �����������ļ����б�ʵ���ļ����˷���
*/
public class DirFilter implements FilenameFilter 
{
	private String prefix = "",extension = "";//�ļ�ǰ׺ �ļ���չ��
	public DirFilter(String filterstr, File dir)
	{
		filterstr = filterstr.toLowerCase();       //���ַ���ת��ΪСд
		int i = filterstr.indexOf('*');            //Ѱ��ͨ���
		if(i>0)
			this.prefix = filterstr.substring(0,i);//ע��
		int j = filterstr.indexOf('.');  
		if(j>0)
		{
			this.extension = filterstr.substring(j+1);
			String[] filenames = dir.list(this);   //���ָ��Ŀ¼�д����������ļ����б�
			for(i = 0; i < filenames.length; i++)
				System.out.println(filenames[i]);
		}
	}
	public DirFilter()
	{
		this("*.*",new File(".",""));
	}
	@Override
	public boolean accept(File dir, String filename)//ʵ�ֹ��˲���
	{
		filename = filename.toLowerCase();
		return filename.startsWith(prefix)&&filename.endsWith(extension);
	}

	public static void main(String[] args)    //filterstrָ�����˴���dirָ��Ŀ¼
	{
		new DirFilter("*.java", new File(".","src"));//��ǰĿ¼��src��Ŀ¼
	}

}
