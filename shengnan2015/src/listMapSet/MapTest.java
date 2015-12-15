package listMapSet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapTest {

	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1",12);
//		System.out.println(map);
//		Object a = map.containsKey("a");
		System.out.println(map.get(null));
		System.out.println(map.get(1+""));
//		
//		
//		String s = "12:";
//		String [] ss = s.split(":");
//		System.err.println(".."+ss[0]);
//		System.err.println(".."+ss[1]);
		
		
		Student st = null;
		if(st==null || st.age==0){
			System.out.println(st);
		}
	}
}
