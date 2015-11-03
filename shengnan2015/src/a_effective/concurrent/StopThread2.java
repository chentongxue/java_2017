package a_effective.concurrent;

import java.util.concurrent.TimeUnit;
//231
/**
 * 修正的方式是同步访问stopRequested域。这个程序会在大约一秒内终止
 * StopThread中被同步方法的动作即使没有同步也是原子的。换句话说，这些方法的同步只是为了它的通信效果，而不是为了
 * 互斥访问。虽然循环的每个迭代中的同步开销很小，还是有其他更正确的替代方法，它更加简洁，性能也可能更好。
 * 如果stopRequested被声明为volatile，第二个版本的StopThread中的锁就可以省略。虽然volatile修饰符
 * 不执行互斥访问，但它可以保证任何一个线程在读取该域的时候都将看到最近刚刚被写入的值
 */
public class StopThread2 {
	private static boolean stopRequested;
//	private static volatile boolean stopRequested;
	private static synchronized void requestStop(){
		stopRequested = true;
	}
	private static synchronized boolean stopRequested(){
		return stopRequested;
	}
	public static void main(String args[]) throws InterruptedException{
		Thread backgroudThread = new Thread(new Runnable(){
			public void run(){
				int i = 0;
				while(!stopRequested()){
					i++;
					System.out.println(i);
				}
			}
		});
		backgroudThread.start();
		TimeUnit.SECONDS.sleep(1);
		requestStop();
	}
}
