package java8;


/**
 *	函数式接口
 */
public class Test_03 {

	public static void main(String[] args) {
		Converter<String, Integer> converter = (from)->Integer.valueOf(from);
		Integer converted = converter.convert("123");
		System.out.println(converted);   // 123
	}
}
