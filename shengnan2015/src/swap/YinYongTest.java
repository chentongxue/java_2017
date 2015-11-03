package swap;

import test.Student;


public class YinYongTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//no
		Student a = null;
		Student b = new Student("b", 2);
		change(a, b);
		System.out.println("a="+a+",b="+b);

	}
	public static void change(Student a, Student b){
		a = new Student("a", 1);
		b = a;
	}
}
