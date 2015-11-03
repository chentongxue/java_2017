package game2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Test2 {
	public static void main(String args[]) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(7);
		list.add(0);
		list.add(6);
		list.add(0);
		list.add(5);
		list.add(3);
		list.add(2);
		list.add(0);
		
		System.out.println(list.toString());
		System.out.println(sortWell(list).toString());
		
	}
	/**
	 * ����Ϊlen�����飬Ԫ��������0���Լ����ظ���1����8��ɣ�����{ 7, 0, 6, 0, 5, 3, 2, 0 }
	 * @param itemlist { 7, 0, 6, 0, 5, 3, 2, 0 }
	 * @return         { 0, 2, 3, 0, 5, 6, 7, 0 }
	 */
	public static  List<Integer> sortWell( List<Integer> itemlist) {
		Object arr[] = itemlist.toArray();//����
		Integer bakArr[] = new Integer[itemlist.size()];//�µ�����
		List<Integer> stack = new ArrayList<Integer>();
		for (Object o : arr) {
			Integer item = (Integer) o;
			if(item == 0)//
			{
				stack.add(item);
			}else{
				bakArr[item-1] = item;
			}
		}
//		for (int i = 0; i<bakArr.length ;i++){
//			if(bakArr[i]==null){
//				bakArr[i] = stack.remove(0);
//			}
//		}
		for (int i = 0; i<bakArr.length ;i++){
			if(bakArr[i]==null){
				bakArr[i] = stack.remove(0);
			}
		}
		List<Integer> rtList = new ArrayList<Integer>();
		Collections.addAll(rtList, bakArr);

		return rtList;
	}

}
