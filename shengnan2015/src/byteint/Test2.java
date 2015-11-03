package byteint;

import java.util.Currency;


public class Test2 {
	public static void main(String args[]){
		System.out.println("haha");
		
		int value = 0;
		value = addValue(value, 1);
		value = addValue(value, 2);
		value = addValue(value, 4);
		System.out.println(Integer.toBinaryString(value));

		System.out.println(Integer.toBinaryString(Integer.MAX_VALUE));
		System.out.println(Integer.toBinaryString(Integer.MIN_VALUE));
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.MIN_VALUE);
		System.out.println(Integer.toBinaryString(1)+":1");
		System.out.println(Integer.toBinaryString(-1)+":-1");
		System.out.println(Integer.toBinaryString(-2)+":-2");
		System.out.println(Integer.toBinaryString(-3)+":-3");
		System.out.println(Integer.toBinaryString(-4)+":-4");
		System.out.println(Integer.toBinaryString(-4>>2));
		System.out.println(Integer.toBinaryString(-4>>>2));
		System.out.println(-2>>2);
		System.out.println(-2>>>2);
//		System.out.println(Boolean.SIZE); ±àÒë²»¹ý
		System.out.println(Byte.SIZE);		//8
		System.out.println(Short.SIZE);		//16
		System.out.println(Character.SIZE);	//16
		System.out.println(Float.SIZE);		//32
		System.out.println(Integer.SIZE);	//32
		System.out.println(Double.SIZE);	//64
		System.out.println(Long.SIZE);		//64
		
		System.out.printf("%f",Math.pow(2, 31)-1);		//64
	}
	/**
	 * @param value
	 * @param n
	 * @return
	 */
	public static int addValue(int value, int n){
		return value|1<<(n-1);
	}
	public static int removeValue(int value, int n){
		return value|1<<(n-1);
	}
}
