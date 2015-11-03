package single_ton;

public class SingleTon {
	private SingleTon(){
	}
	private final static SingleTon instance = new SingleTon();
	public static SingleTon getInstance(){
		return instance;
	}
	public static void main(String[] args) {
		System.out.println("Main方法");
	}

}
/* 
或者将
	private final static SingleTon instance = new SingleTon();
	public static SingleTon getInstance(){
		return instance;
	}
替换为
 	public final static SingleTon instance = new SingleTon();
 

 */