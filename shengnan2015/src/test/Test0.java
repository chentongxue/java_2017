package test;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Test0 {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int[] dim = new int[] { 5, 10, 15 };
		Object array = Array.newInstance(Integer.TYPE, dim);
		Object arrayObj = Array.get(array, 3);
		Class<?> cls = arrayObj.getClass().getComponentType();
		System.out.println(cls);// 输出是“class [I”。
		System.out.println(cls.getSimpleName());// 输出是“class [I”。

		testreflect();
		testreflect2();
//		testreflect3();
	}

	// public Object[] toArray() {
	// Estimate size of array; be prepared to see more or fewer elements
	// Object[] r = new Object[size()];
	// Iterator<E> it = iterator();
	// for (int i = 0; i < r.length; i++) {
	// if (! it.hasNext()) // fewer elements than expected
	// return Arrays.copyOf(r, i);
	// r[i] = it.next();
	// }
	// return it.hasNext() ? finishToArray(r, it) : r;
	// }
	public static void testreflect() {
		String[] ss = new String[2];
		Object o = java.lang.reflect.Array.newInstance(ss.getClass()
				.getComponentType(), 6);
		System.out.println(o.getClass().getSimpleName());
	}
	/**
	 * 疑问为什么第五行会报错？
	 * @throws Exception
	 */
	public static void testreflect2() throws Exception {
		Set s = new HashSet();
		Iterator it = s.iterator();
		s.getClass().getMethod("add", Object.class).invoke(s, "abc");//Set.class.getMethod("add", Object.class).invoke(s, "abc");也行
		Iterator.class.getMethod("hasNext", new Class[0]).invoke(it, new Object[0]);//true
		it.getClass().getMethod("hasNext", new Class[0]).invoke(it, new Object[0]);//exception
	}

	public static void testreflect3() throws Exception {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.getClass().getMethod("add", Object.class).invoke(list, "abc");
		System.out.println(list);
	}
}
