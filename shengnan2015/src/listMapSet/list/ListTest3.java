package listMapSet.list;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class ListTest3 {

	/**
	 * �б�����������
	 */
	public static void main(String[] args) {
		//���벻����
		Object[] objectArray = new Long[1];
		objectArray[0] = "I dont fit in";
		//���뱨��
//		List<Object> ol = new ArrayList<Long>();
//		ol.add("hello boy");
		
	}
	private static void unsafeAdd(List list, Object o){
		list.add(o);
	}
}
