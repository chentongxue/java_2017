package java8_2;
/**
 	1.使用() -> {} 替代匿名类
 	//Before Java 8:
	newThread(newRunnable() {
    @Override
    public void run() {
        System.out.println("Before Java8 ");
    }
	}).start();
 */
public class Test1 {
	public static void main(String[] args) {
		//Java 8 way:
		new Thread(() -> System.out.println("In Java8!")).start();
		System.out.println("end");
	}

}
