package observer.test;

import java.util.Vector;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Vector<Student> students = new Vector<Student>();         
		Teacher t = new Teacher();         
		for(int i= 0 ;i<10;i++){
			Student st = new Student("lili"+i,t);
			students.add(st);
			t.attach(st);         
		}         
		t.setPhone("110");         
		for(int i=0;i<10;i++)                
		students.get(i).show();         
		t.setPhone("10086");         
		for(int i=0;i<10;i++)                
		students.get(i).show();      
	}  
}
