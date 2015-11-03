package mapvalue;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapTest2 {
	public static void main(String[] args) {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		map.put("tom",12);
		map.put("jose",13);
		map.put("jack",14);
//		for (Student s : map.values()) {
//			s.setAge(100);
//		}
		
		  for (Map.Entry<String, Integer> entry : map.entrySet()) {
//			  entry.getValue().setAge(123);
//			  entry.getValue().setName("wefe");
		  }
		  Object c = map.remove("cc");
		  int d = map.remove("tom");
		  Object a = map.get("sdfd");
		  System.out.println(map);
		System.out.println(a);
		System.out.println(c);
		System.out.println(d);
	}
}
