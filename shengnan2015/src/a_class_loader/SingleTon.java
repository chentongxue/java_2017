package a_class_loader;

public class SingleTon {
	private static final SingleTon INSTANCE = new SingleTon();
	static{
		System.out.println("��̬");
	}
	private SingleTon(){
		System.out.println("���캯��");
	}
	public static SingleTon getInstance(){
		return INSTANCE;
	}
}
