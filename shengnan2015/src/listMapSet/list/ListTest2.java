package listMapSet.list;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
// effective of java
public class ListTest2 {

	/**
	 * 参数化类型优先于原始类型
	 */
	public static void main(String[] args) {
		List<String> strings = new ArrayList<String>();
		unsafeAdd(strings, new Integer(3));
		String s = strings.get(0);
		System.out.println(s);
	}
	//wrong
	private static void unsafeAdd(List list, Object o){
		list.add(o);
	}
	//right
//	private static void unsafeAdd(List<Object> list, Object o){
//		list.add(o);
//	}
}
