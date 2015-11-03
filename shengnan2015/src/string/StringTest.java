package string;

public class StringTest {

	private String a = "a";
	public static void main(String[] args) {
		StringTest st =  new StringTest();
		st.getS();
		System.out.println(st.a);
	}
	private String getS(){
		a = "hello";
		a.replace("o", "a");
		return a;
	}
}
