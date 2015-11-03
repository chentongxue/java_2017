package mapvalue;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapTest {
	public static void main(String[] args) {
		LinkedHashMap<Integer, Student> map = new LinkedHashMap<Integer, Student>();
		map.put(null, null);
		map.put(2, new Student("jose",13));
		map.put(2, new Student("jack",14));
		map.put(null, null);
//		for (Student s : map.values()) {
//			s.setAge(100);
//		}
		System.out.println(map);
		  for (Map.Entry<Integer, Student> entry : map.entrySet()) {
			  entry.getValue().setAge(123);
			  entry.getValue().setName("wefe");
		  }

		System.out.println(map);
	}
}
