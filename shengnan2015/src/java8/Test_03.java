package java8;


/**
 *	����ʽ�ӿ�
 */
public class Test_03 {

	public static void main(String[] args) {
		Converter<String, Integer> converter = (from)->Integer.valueOf(from);
		Integer converted = converter.convert("123");
		System.out.println(converted);   // 123
	}
}
