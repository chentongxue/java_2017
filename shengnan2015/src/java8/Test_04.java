package java8;


/**
 *	Java 8 允许你使用 :: 关键字来传递方法或者构造函数引用
 */
public class Test_04 {

	public static void main(String[] args) {
		Converter<String, Integer> converter = Integer::valueOf;
		Integer converted = converter.convert("123");
		System.out.println(converted);   // 123
		
		
		/*
		 * 只需要使用 Person::new 来获取Person类构造函数的引用，
		 * Java编译器会自动根据PersonFactory.create方法的签名来选择合适的构造函数。
		 */
		PersonFactory<Person> personFactory = Person::new;
		Person person = personFactory.create("Peter", "Parker");
	}
}
