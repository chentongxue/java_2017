package fanshe;

import java.util.ArrayList;

/**
 * 	ArrayList<Integer> list = new ArrayList<Integer>(); 
 *	���������ΪInteger��ArrayList�д��һ��String���͵Ķ���
 * */
public class IntegerStoreString 
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("ʵ����һ������ΪInteger��ArrayList����Ϊlist");
		ArrayList<Integer>list = new ArrayList<Integer>();
		list.getClass().getMethod("add", Object.class).invoke(list, "abc");
		list.getClass().getMethod("add", Object.class).invoke(list, "���");
		list.getClass().getMethod("add", Object.class).invoke(list, "����");
		System.out.println("��\"abc\"�ַ�������list\n������list��Ԫ��");
		System.out.println(list);	
	}
}