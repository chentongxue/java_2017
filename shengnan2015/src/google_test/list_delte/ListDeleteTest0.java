package google_test.list_delte;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * ²âÊÔÉ¾³ılistÖĞµÄÔªËØ
 * Ê§°Ü±¨´í
 *
 */
public class ListDeleteTest0 {
	public static void main(String args[]){
		test1();
	}
	public static void test1(){
		Student s = new Student("a",1);
		Student s2 = new Student("b",2);
		Student s3 = new Student("c",3);
		Student s4 = new Student("c",3);
		List<Student> list = Lists.newArrayList(s,s2,s3,s4,null);
		System.out.println(list);
		list.remove(s4);// one deleted,one left
//		list.remove(null);
		System.out.println(list);
	}
	
}
