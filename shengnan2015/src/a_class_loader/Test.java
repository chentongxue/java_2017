package a_class_loader;

public class Test {

	public static void main(String[] args) {
		try {
			Class<?> clazz = Class.forName("a_class_loader.SingleTon");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
