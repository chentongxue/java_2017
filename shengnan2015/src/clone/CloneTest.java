package clone;

public class CloneTest {
	public static void main(String args[]){
		Person p = new Person("name", 123, "adress");
		Person b = p.clone();
		System.out.println(p);
		System.out.println(b);
		b.setAddrdss("lisa");
		System.out.println(p);
		System.out.println(b);
	}
}
