package Ï°¹ßÓÃ·¨.stringreverse;

public class Test {
	/**
	 * ·­×ª×Ö·û´® 1
	 */
	public static String reverse(String s){
		StringBuilder sb = new StringBuilder();
		char[] arr = s.toCharArray();
		int len = s.length();
		for(int i = 0; i < len; i++){
			sb.append(arr[len - 1 -i]);
		}
		return sb.toString();
	}
	/**
	 * ·­×ª×Ö·û´® 2
	 */
	public static String reverse2(String s){
		char[] arr = s.toCharArray();
		char[] rt = new char[s.length()];
		int len = s.length();
		for(int i = 0; i < len; i++){
			rt[i] = arr[len - 1 - i];
		}
		return new String(rt);
	}
	/**
	 * ·­×ª×Ö·û´® 3
	 */
	public static String reverse3(String s){
		return new StringBuilder(s).reverse().toString();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
