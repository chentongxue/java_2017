package swap;

import test.Student;


public class YinYongTe2st {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//no
		Student b = new Student("b", 2);
		change(b);
		System.out.println("b="+b);

	}
	public static void change(Student b){
		b.setAge(112);
		b = null;
	}
}
