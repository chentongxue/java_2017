package clone;

public class Person implements Cloneable{
	private String name;
	private int age;
	private String addrdss;
	public Person(String name, int age, String addrdss) {
		super();
		this.name = name;
		this.age = age;
		this.addrdss = addrdss;
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
	public String getAddrdss() {
		return addrdss;
	}
	public void setAddrdss(String addrdss) {
		this.addrdss = addrdss;
	}
	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + ", addrdss=" + addrdss
				+ "]";
	}
	@Override
	public Person clone (){
		try{
			return (Person)super.clone();
		}catch(CloneNotSupportedException e){
			throw new AssertionError();//cat happen
		}
	}
	
}
