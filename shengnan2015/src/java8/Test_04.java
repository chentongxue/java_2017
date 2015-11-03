package java8;


/**
 *	Java 8 ������ʹ�� :: �ؼ��������ݷ������߹��캯������
 */
public class Test_04 {

	public static void main(String[] args) {
		Converter<String, Integer> converter = Integer::valueOf;
		Integer converted = converter.convert("123");
		System.out.println(converted);   // 123
		
		
		/*
		 * ֻ��Ҫʹ�� Person::new ����ȡPerson�๹�캯�������ã�
		 * Java���������Զ�����PersonFactory.create������ǩ����ѡ����ʵĹ��캯����
		 */
		PersonFactory<Person> personFactory = Person::new;
		Person person = personFactory.create("Peter", "Parker");
	}
}
