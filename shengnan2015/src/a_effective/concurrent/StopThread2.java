package a_effective.concurrent;

import java.util.concurrent.TimeUnit;
//231
/**
 * �����ķ�ʽ��ͬ������stopRequested�����������ڴ�Լһ������ֹ
 * StopThread�б�ͬ�������Ķ�����ʹû��ͬ��Ҳ��ԭ�ӵġ����仰˵����Щ������ͬ��ֻ��Ϊ������ͨ��Ч����������Ϊ��
 * ������ʡ���Ȼѭ����ÿ�������е�ͬ��������С����������������ȷ����������������Ӽ�࣬����Ҳ���ܸ��á�
 * ���stopRequested������Ϊvolatile���ڶ����汾��StopThread�е����Ϳ���ʡ�ԡ���Ȼvolatile���η�
 * ��ִ�л�����ʣ��������Ա�֤�κ�һ���߳��ڶ�ȡ�����ʱ�򶼽���������ոձ�д���ֵ
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
