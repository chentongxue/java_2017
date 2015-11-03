package queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.common.collect.Queues;

public class ConcurrentLinkedQueueTest {
	private static ConcurrentLinkedQueue<Integer> queue = Queues.newConcurrentLinkedQueue();
	private static int count = 2;//�̸߳���
	/*
	 * CountDownLatch��ͬ�������࣬ 
	 */
	private static CountDownLatch latch = new CountDownLatch(count);
	/*
	 * ����
	 */
	public static void offer(){
		for(int i = 0; i < 100000; i++){
			queue.offer(i);
		}
	}
	/**
	 * ����
	 */
	static class Poll implements Runnable{
		@Override
		public void run() {
			 while (!queue.isEmpty()) {
	                System.out.println(queue.poll());
	            }
			 latch.countDown();
		}
	}
	public static void main(String[] args) throws InterruptedException {
		long timeStart = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(4);
        ConcurrentLinkedQueueTest.offer();
        for (int i = 0; i < count; i++) {//count = 2
            es.submit(new Poll());
        }
        latch.await(); //ʹ�����߳�(main)����ֱ��latch.countDown()Ϊ��ż���ִ��
        System.out.println("cost time " + (System.currentTimeMillis() - timeStart) + "ms");
        es.shutdown();
	}
}
