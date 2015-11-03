package google_test.list_delte;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 测试删除list中的元素[大全]
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
		test5(list);

	}
	/**
	 * ava.util.ConcurrentModificationException
	 */
	public static void test1(List<Student> list){
		System.out.println(list);
		
		for(Student st: list){
			if(st.getAge() == 3){
				list.remove(st);
			}
			System.err.println(list);
		}
	}
	//可以
	public static void test2(List<Student> list){
		System.err.println(list);

		for(int i = 0; i< list.size(); i++){
			if(list.get(i).getAge() == 3){
				list.remove(i);
				i--;
				System.err.println(list);
			}
		}
	}
	
	/**
	 * 可以
	 */
	public static void test3(List<Student> list){
		System.err.println(list);

		Iterator<Student> iterator = list.iterator();
		while(iterator.hasNext()){
			Student st = iterator.next();
			if(st.getAge() == 3){
				iterator.remove();
				System.err.println(list);

			}
		}
	}
	//也可以
	public static void test4(List<Student> list){

		System.out.println(list);
		List<Student> delteList = Lists.newArrayList();
		for(Student st: list){
			if(st.getAge() == 3){
				delteList.add(st);
			}
		}
		list.removeAll(delteList);
		System.err.println(list);
	}
	
	/**
	 * 可以，略有逼格
	 */
	public static void test5(List<Student> list){
		System.err.println(list);

		for(Iterator<Student> iterator = list.iterator();iterator.hasNext();){
			Student st = iterator.next();
			if(st.getAge() == 3){
				iterator.remove();
				System.err.println(list);

			}
		}
	}
}
