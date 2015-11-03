package queue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.common.collect.Queues;

public class ConcurrentLinkedQueueTest {
	private static ConcurrentLinkedQueue<Integer> queue = Queues.newConcurrentLinkedQueue();
	private static int count = 2;//线程个数
	/*
	 * CountDownLatch，同步辅助类， 
	 */
	private static CountDownLatch latch = new CountDownLatch(count);
	/*
	 * 生产
	 */
	public static void offer(){
		for(int i = 0; i < 100000; i++){
			queue.offer(i);
		}
	}
	/**
	 * 消费
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
        latch.await(); //使得主线程(main)阻塞直到latch.countDown()为零才继续执行
        System.out.println("cost time " + (System.currentTimeMillis() - timeStart) + "ms");
        es.shutdown();
	}
}
