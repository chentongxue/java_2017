package 习惯用法;

import java.util.ArrayList;
import java.util.List;

import a_serial.data.Person;


/**
 * 递归与临时变量
 * @author admin
 *
 */
public class Test2 {

	public static void main(String[] args) {
		List<Person> persons = new ArrayList<>();
		System.out.println(persons);
		hello(persons);
		System.out.println(persons);
	}
	public static List<Person> hello(List<Person> persons){
//		persons = new ArrayList<>();
		persons.add(new Person("aaa",123));
//		persons.clear();
//		persons = null;
		return null;
	}
	public static List<Person> getPersons(){
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("a",1));
		persons.add(new Person("b",2));
		persons.add(new Person("b",3));
		persons.add(new Person("b",33));
		persons.add(new Person("d",4));
		return persons;
	}

}
