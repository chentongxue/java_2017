package comparebale.celuejiekou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import test.Student;

public class HostTest {
	private static class StrLenCmp implements Comparator<Student>, Serializable{
		@Override
		public int compare(Student o1, Student o2) {
			return o1.getAge() - o2.getAge();
		}
	}
	public static final Comparator<Student> STRING_LENGTH_COMPARATOR = new StrLenCmp();
	public static void main(String[] args) {
		Student s1 = new Student("a",1);
		Student s2 = new Student("b",2);
		Student s3 = new Student("c",3);
		
		Map<String, Student> map = Maps.newHashMap();
		for(int i=0;i<100001; i++ ){
			Student s = new Student("a"+i, i);
			map.put(s.getName()+i, s);
		}
		map.put(s1.getName(), s1);
		map.put(s2.getName(), s2);
		map.put(s3.getName(), s3);
		System.out.println(map);
		
		ArrayList list = Lists.newArrayList(map.values());
		Collections.sort(list, STRING_LENGTH_COMPARATOR);
		System.out.println(list);
		System.out.println("end");
	}

}
