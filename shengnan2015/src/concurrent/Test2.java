package concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * i++,������ԭ�Ӳ�����ʹ��AtomicInteger 
 */
public class Test2 {
	private static  AtomicInteger a = new AtomicInteger(0);
	public static void main(String[] args) throws InterruptedException{
		System.err.println("LOL");
		int n=500000;
		while(n-->0){
			startAdd();
//			if(n<10)
//				System.out.println(a);
		}
		Thread.sleep(2000);
		System.err.println(a);
	}
	public static void startAdd(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				a.getAndIncrement();
			}
		}).start();
	}
}
