package a_shengnan.a_mianshi;

import java.util.ArrayList;

/**
 * int和Integer的区别
 */
public class IntegerAndInt {
	public static void main(String args[]){
		System.out.println("haha");
		ArrayList<Integer>  list = new ArrayList<>();
		int n = 123;
		int m = 124;
		list.add(n);
		list.add(m);
		System.out.println(list);
		list.remove(0);
		list.remove(new Integer(0));
		System.out.println(list);
		System.out.println(list);
		System.out.println(list.get(1));
	}
}
