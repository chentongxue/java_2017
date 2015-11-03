package a_effective.concurrent;

import java.util.concurrent.TimeUnit;
//230
/**
 * 别的机器上会无限循环
 */
public class StopThread {
	private static boolean stopRequested;
	
	public static void main(String args[]) throws InterruptedException{
		Thread backgroudThread = new Thread(new Runnable(){
			public void run(){
				int i = 0;
				while(!stopRequested){
					i++;
					System.out.println(i);
				}
			}
		});
		backgroudThread.start();
		TimeUnit.SECONDS.sleep(1);
		stopRequested = true;
	}
}
