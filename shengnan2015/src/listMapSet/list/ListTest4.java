package listMapSet.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import listMapSet.Student;

public class ListTest4 {

	/**
	 * ¸´ÖÆ
	 */
	public static void main(String[] args) {
		Student a = new Student("a", 1);
		Student b = new Student("b", 2);
		List<Student> list = new ArrayList<Student>();
		list.add(a);
		list.add(b);
		
		System.out.println("--" + Arrays.toString(list.toArray()));
		List<Student> list2 = new ArrayList<Student>(list);
		list2.get(0).setAge(123);
		System.out.println("--" + Arrays.toString(list.toArray()));
		System.out.println("--" + Arrays.toString(list2.toArray()));
		System.out.println("++" + list2);
	}
	private static void unsafeAdd(List list, Object o){
		list.add(o);
	}
}
