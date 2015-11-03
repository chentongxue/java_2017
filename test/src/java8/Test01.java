package java8;

public class Test01 {

	public static void main(String[] args) {
		
		
		Converter<String, Integer> converter = Integer::valueOf;
		Integer converted = converter.convert("123");
		System.out.println(converted);   // 123
		

	}

}
