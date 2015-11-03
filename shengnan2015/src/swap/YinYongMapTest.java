package swap;

import java.util.HashMap;
import java.util.Map;

import test.Student;


public class YinYongMapTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String,Student> map = new HashMap<String,Student>();
		Student a = null;
		Student b = new Student("b", 2);
		change(map, b);
		System.out.println("map="+map);

	}
	public static void change(Map map, Student b){
//		map = new HashMap<String,Student>();
		b = new Student("b", 2);
		map.put(b.getName(), b.getAge());
//		Student temp = a;
//		temp.setAge(123);
//		a = b;
//		b = temp;
	}
}
