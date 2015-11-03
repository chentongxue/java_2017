package google_test.list_delte;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * ²âÊÔÉ¾³ılistÖĞµÄÔªËØ
 * Ê§°Ü±¨´í
 *
 */
public class ListDeleteTest2 {
	public static void main(String args[]){
		test1();
//		test2();
	}
	public static void test1(){
		
		Student s = new Student("a",1);
		Student s2 = new Student("b",2);
		Student s3 = new Student("c",3);
		Student s4 = new Student("d",4);
		Map<String,Student> map = Maps.newConcurrentMap();
		map.put(s.getName(), s);
		map.put(s2.getName(), s2);
		map.put(s3.getName(), s3);
		map.put(s4.getName(), s4);
//		List<Student> list = Lists.newArrayList(s,s2,s3);//ERR
//		List<Student> list = Lists.newArrayList(s,s3,s2);// ewibaocuo
//		List<Student> list = Lists.newArrayList(s3,s,s2);//err
		
		System.err.println(map);
		
		for(Iterator<Student> iterator = map.values().iterator();iterator.hasNext();){
			Student st = iterator.next();
			if(st.getAge() == 3){
				iterator.remove();
//				System.err.println(list);

			}
		}
		List<Student> list = Lists.newArrayList(map.values());
		System.out.println(list);
		System.err.println(map);
	}
	public static void test2(){
		Student s = new Student("a",1);
		Student s2 = new Student("b",2);
		Student s3 = new Student("c",3);
		Student s4 = new Student("d",3);
		Student s5 = new Student("e",3);
		List<Student> list = Lists.newArrayList(s,s2,s3,s4,s5);
		System.out.println(list);
		
		for(Student st: list){
			if(st.getAge() == 3){
				list.remove(st);
			}
			
			System.err.println(list);
		}
	}
}
