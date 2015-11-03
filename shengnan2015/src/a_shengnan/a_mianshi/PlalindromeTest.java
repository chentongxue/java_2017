package a_shengnan.a_mianshi;

public class PlalindromeTest {

	/**
	 * 2.����ж�һ���ַ����ǲ��ǶԳƵġ��Գ��ַ������ӣ�aba,1221,79897��
	 */
	public static void main(String[] args) {
		System.out.print(isPlalindrome("a"));
		System.out.print(isPlalindrome(null));
		System.out.print(isPlalindrome("abc"));
		System.out.print(isPlalindrome("aba"));
		System.out.print(isPlalindrome("12321"));
		StringBuilder sb = new StringBuilder("��ʤ��");
		sb.reverse();
		System.out.println(reverse("�Ǳ���"));
		System.out.println(reverse2("������"));
		System.out.println(sb);
	}
	/**
	 * ��ת�ַ��� 1
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
	 * ��ת�ַ��� 2
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
	 * ��ת�ַ��� 3
	 */
	public static String reverse3(String s){
		return new StringBuilder(s).reverse().toString();
	}
	
	public static boolean isPlalindrome2(String s){
		StringBuilder sb = new StringBuilder(s);
		return s.equals(sb.toString());
	}
	public static boolean isPlalindrome(String s){
		if(s == null){
			return false;
		}
		if(s.length() == 1){
			return true;
		}
		int len = s.length();
		char[] arr = s.toCharArray();
		for(int i = 0; i < len/2; i++ ){
			if(arr[i] != arr[len-i-1]){
				return false;
			}
		}
		return true;
	}
}
