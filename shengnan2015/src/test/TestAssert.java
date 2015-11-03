package test;
/**
 * 需要日后查看
 * @author Administrator
 *
 */
public class TestAssert {
	public static void main(String[] args) {
		int i = 0;
		assert i==5;
		for (i = 0; i < 50; i++) {
			System.out.println(i);
		}
		--i;
		String b = "24";
		byte a = Byte.parseByte(b);
		System.err.println(b);
		
		int aa = 100;
		aa-=10;
		System.err.println(aa);
	}
	
}
