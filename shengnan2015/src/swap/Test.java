package swap;

import test.Student;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//no
//		String a = "ha";
//		String b = "wa";
		//no
//		Integer a = 1;
//		Integer b = 2;
		Student a = new Student("a", 1);
		Student b = new Student("b", 2);
		swap(a, b);
		System.out.println("a="+a+",b="+b);

	}

	public static void swap(Student a, Student b){
		Student temp = a;
		a = b;
		b = temp;
	}
	
	
	public static <T> void swap(T a, T b){
		T temp = a;
		a = b;
		b = temp;
	}
}
