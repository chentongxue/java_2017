package concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * i++,并不是原子操作，使用AtomicInteger 
 */
public class Test1 {
	private static  int a = 0;
	public static void main(String[] args) throws InterruptedException{
		System.err.println("LOL");
		int n=500000;
		while(n-->0){
			startAdd();
			if(n<10)
				System.out.println(n+","+a);
		}
		System.out.println(a);
		Thread.sleep(2000);
		System.err.println(a);
	}
	public static void startAdd(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				a++;
			}
		}).start();
	}
}
