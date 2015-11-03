package a_class_loader;

public class SingleTon {
	private static final SingleTon INSTANCE = new SingleTon();
	static{
		System.out.println("¾²Ì¬");
	}
	private SingleTon(){
		System.out.println("¹¹Ôìº¯Êý");
	}
	public static SingleTon getInstance(){
		return INSTANCE;
	}
}
