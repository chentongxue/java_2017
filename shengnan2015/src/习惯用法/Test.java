package 习惯用法;

import java.util.ArrayList;
import java.util.List;

import a_serial.data.Person;


/**
 * 递归与临时变量
 * @author admin
 *
 */
public class Test {

	public static void main(String[] args) {
		List<Person> persons = getPersons();
		System.out.println(persons);

	}
	public List<Person> hello(List<Person> persons, List<String> ss){
		if(persons == null){
			persons = getPersons();
		}
		for (Person p : persons) {
			for (String s : ss) {
//				if(p.)
			}
		}
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
