package string;

public class StringBuilerTest {

	private String a = "a";
	public static void main(String[] args) {
		StringBuilerTest st =  new StringBuilerTest();
		st.getS();
		System.out.println(st.a);
	}
	private String getS(){
		a = "hello";
		a.replace("o", "a");
		return a;
	}
}
