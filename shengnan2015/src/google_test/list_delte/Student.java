package google_test.list_delte;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


public  class Student {
	private String name;
	private int age;
	
	public Student(){
		super();
	}
	public Student(String name, int age){
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	public static void main(String args[]){
		Student s = new Student("nan", 16);
		Student s1 = new Student("bao", 17);
		Student s2 = new Student("lee", 18);
		System.out.println(s);
		System.out.println(s1);
		System.out.println(s2);
		
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	@Override
	public boolean equals(Object o){
		if (this == o) {
			return true;
		}
		if (o instanceof Student) {
			Student other = (Student) o;
			return this.name.equals(other.getName()) && this.age == other.getAge();
		}
		return false;
	}
}
