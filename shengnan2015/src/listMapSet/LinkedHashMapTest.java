package listMapSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapTest {

	public static void main(String[] args) {
		LinkedHashMap<Integer, Student> map = new LinkedHashMap<Integer, Student>();
		map.put(1, new Student("tom",12));
		map.put(2, new Student("jose",13));
		map.put(2, new Student("jack",14));
		for (Student s : map.values()) {
			s.setAge(100);
		}
//		for (Iterator<Map.Entry<Integer, String>> it = map.entrySet()
//				.iterator(); it.hasNext();) {
//			Map.Entry<Integer, String> entry = it.next();
//			String s = entry.getValue();
//			s = "HAHA";
//			
//		}
		System.out.println(map);
		System.out.println("have a think");
		Student [] ss = map.values().toArray(new Student[]{});
		System.out.println(Arrays.toString(ss));
		System.out.println("have a think2");
		Student [] s = (Student[]) map.values().toArray();
		System.out.println(Arrays.toString(s));
	}
}
