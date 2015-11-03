package single_ton;

public class SingleTon2 {
	private SingleTon2() {
		System.out.println("构造方法");
	}

	private static SingleTon2 instance = null;

	public static synchronized SingleTon2 getInstance() {
		if (instance == null) {
			instance = new SingleTon2();
		}
		return instance;
	}

	public static void main(String[] args) {
		System.out.println("Main方法");
		SingleTon2.getInstance();
		SingleTon2.getInstance();
		SingleTon2.getInstance();
	}

}
