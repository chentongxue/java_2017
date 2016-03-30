import java.util.UUID;


public class Test {

	public static void main(String[] args) {
		String s = "";
		String rd = UUID.randomUUID().toString();
		System.out.println(rd);
		System.out.println(rd.length());
		String s1 = "";
		try {
			s1 = rd.replaceAll("-", "").substring(0, 15);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(s1);
		System.out.println(rd.length());
	}

}
