package listMapSet.list;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class ListTest3 {

	/**
	 * 列表优先于数组
	 */
	public static void main(String[] args) {
		//编译不报错
		Object[] objectArray = new Long[1];
		objectArray[0] = "I dont fit in";
		//编译报错
//		List<Object> ol = new ArrayList<Long>();
//		ol.add("hello boy");
		
	}
	private static void unsafeAdd(List list, Object o){
		list.add(o);
	}
}
