package listMapSet.list;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.common.collect.Lists;
public class ListTestCap {

	/**
	 * ÈÝÁ¿
	 */
	public static void main(String[] args) {
//		vTest();
		vTest2();
	}

	private static void vTest() {
		try {
			List<String> s = new ArrayList<String>();
			int a=100;
			for(;a-->0;){
				s.add("a");
				
				Field f = s.getClass().getDeclaredField("elementData");
				f.setAccessible(true);
				Object[] elementData = (Object[]) f.get(s);
				System.out.println(s.size()+":"+elementData.length);

			}
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void vTest2() {
		try {
			Vector <String> s = new Vector<String>();
			s.setSize(newSize);
			int a=100;
			for(;a-->0;){
				s.add("a");
				
				Field f = s.getClass().getDeclaredField("elementData");
				f.setAccessible(true);
				Object[] elementData = (Object[]) f.get(s);
				System.out.println(s.size()+":"+elementData.length);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
