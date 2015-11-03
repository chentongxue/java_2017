package fanshe;

import java.util.ArrayList;

/**
 * 	ArrayList<Integer> list = new ArrayList<Integer>(); 
 *	在这个泛型为Integer的ArrayList中存放一个String类型的对象。
 * */
public class IntegerStoreString 
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("实例化一个泛型为Integer的ArrayList名称为list");
		ArrayList<Integer>list = new ArrayList<Integer>();
		list.getClass().getMethod("add", Object.class).invoke(list, "abc");
		list.getClass().getMethod("add", Object.class).invoke(list, "你好");
		list.getClass().getMethod("add", Object.class).invoke(list, "世界");
		System.out.println("将\"abc\"字符串存入list\n下面是list的元素");
		System.out.println(list);	
	}
}