import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class FileList 
{
	int count_dirs = 0, count_files;                    //Ŀ¼�� ���ļ���
	long byte_files = 0; 
	public FileList()
	{
		count(new File("."));                           //������ǰĿ¼
		System.out.println( "����" + count_files + "���ļ������ֽ�Ϊ " + byte_files );
		System.out.println( "����" + count_dirs + "��Ŀ¼ " );
	}
	private void count(File dir)                        //dirĿ¼�����ļ��б��ݹ��㷨
	{
		System.out.println( dir.getAbsolutePath() );    //��ʾdir�ľ���·��
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        File[] files = dir.listFiles();                 //���ص�ǰĿ¼�����ļ�
        for( int i = 0; i < files.length; i ++ )
        {
        	System.out.print(files[i].getName());         //��ʾ�ļ���
        	if(files[i].isFile())                         //�ж��Ƿ����ļ�
        	{
        		System.out.print(files[i].length()+"B\t");//��ʾ�ļ�����
        		count_files++;
        		byte_files += files[i].length();          //�ļ����ֽ���
        	}
        	else
        	{
        		System.out.print("<DIR>\t");
        		count_dirs++;
        	}
        	System.out.println(   sdf.format(  new Date( files[i].lastModified() )  )   );
        }
        for(int i = 0; i < files.length; i++) //������Ŀ¼���ļ��б��ȼ���
        	if(files[i].isDirectory())        //�ж϶����Ƿ�ΪĿ¼
        		count(files[i]);              //��Ŀ¼���ļ��б�ͳ��ȼ��㣬 �ݹ����
	}
	public static void main(String args[])throws IOException
	{
		new FileList();
	}
}
