package ϰ���÷�.listdelete;

import google_test.list_delte.Student;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * ����ɾ��list�е�Ԫ��[��ȫ]
 */

public class ListDeleteTest {
	
	public static void main(String args[]){
		Student s = new Student("a",1);
		Student s2 = new Student("b",2);
		Student s3 = new Student("c",3);
		Student s4 = new Student("d",3);
		Student s5 = new Student("e",3);
		List<Student> list = Lists.newArrayList(s,s2,s3,s4,s5);
//		test1(list);
//		test2(list);
//		test3(list);
//		test4(list);
//		test5(list);
		Student s8 = test8(list);
		System.out.println(s8);

	}
	/**
	 * ������
	 * �ᱨjava.util.ConcurrentModificationException
	 */
	public static void test1(List<Student> list){
		for(Student st: list){
			if(st.getAge() == 3){
				list.remove(st);
			}
		}
	}
	/**
	 * ����
	 */
	public static void test2(List<Student> list){
		for(int i = 0; i< list.size(); i++){
			if(list.get(i).getAge() == 3){
				list.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * ����
	 */
	public static void test3(List<Student> list){
		Iterator<Student> iterator = list.iterator();
		while(iterator.hasNext()){
			Student st = iterator.next();
			if(st.getAge() == 3){
				iterator.remove();
			}
		}
	}
	/**
	 * ����
	 */
	public static void test4(List<Student> list){
		List<Student> delteList = Lists.newArrayList();
		for(Student st: list){
			if(st.getAge() == 3){
				delteList.add(st);
			}
		}
		list.removeAll(delteList);
	}
	
	/**
	 * ����
	 */
	public static void test5(List<Student> list){

		for(Iterator<Student> iterator = list.iterator();iterator.hasNext();){
			Student st = iterator.next();
			if(st.getAge() == 3){
				iterator.remove();
			}
		}
	}
	/**������
	 *���������forѭ������ʹ�õ�����
	 * java.util.ConcurrentModificationException
	 */
	public static void test6(List<Student> list){
		
		for(Student st: list){
			test7(list);
		}
	}
	public static void test7(List<Student> list){
		
		for(Iterator<Student> iterator = list.iterator();iterator.hasNext();){
			Student st = iterator.next();
			if(st.getAge() == 3){
				iterator.remove();
			}
		}
	}
	/**
	 * ���Է���ֵ
	 */
	public static Student test8(List<Student> list){
		Iterator<Student> iterator = list.iterator();
		Student st = null;
		while(iterator.hasNext()){
			st = iterator.next();
			if(st.getAge() == 3){
				iterator.remove();
				return st;
			}
		}
		return st;
	}
}
