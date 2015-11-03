package file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CopyFile {
	//�ݹ鸴���ļ����ļ���
	public static void CopyDir(String src, String targ)
	{
		File dir = new File(src);//�ļ�
		if(dir.isDirectory())//���ļ���
		{
			new File(targ + "\\", dir.getName()).mkdir();//����Ŀ¼
			File[] files = dir.listFiles();
			for(int i = 0; i<files.length; i++)
			{
				if(files[i].isDirectory())//���ļ���
				{
					CopyDir(files[i].getAbsolutePath(), targ + "\\" + dir.getName() + "\\");//�ݹ鴴����Ŀ¼
				}
				if(files[i].isFile())//�����ļ�
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
			FileOutputStream fout = new FileOutputStream(targ); //�����ļ����������
			byte[] buffer = new byte[512];                      //�ֽڻ�����
			int count = fin.read(buffer);                       //��ȡ������
			while(count!=-1)
			{
				fout.write(buffer, 0, count); //д��buffer���� 0~count��Ԫ��
				count = fin.read(buffer);     //��ȡ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}   	
	}

}
