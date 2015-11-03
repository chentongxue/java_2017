package a_shengnan.a_mianshi;


public class HexaDecimal {
	/**
	 * 将16进制的字符串按照整数返回。
	 */
	public static void main(String[] args) {
		int a = get16HexaDecimalValue("22");
		int b = Integer.parseInt("22", 16);
		System.out.println(a);
		System.out.println(b);
	}
	public static int get16HexaDecimalValue(String s){
		int len = s.length();
		int sum = 0;
		for(int i = 0; i < len; i++){
			char c = s.charAt(len - 1 -i);
			int n = Character.digit(c, 16);
			sum += n*(1<<(4*i));
		}
		return sum;
	}
}
