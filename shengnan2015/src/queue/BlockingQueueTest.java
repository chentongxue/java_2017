package queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import com.google.common.collect.Queues;

public class BlockingQueueTest {
	
	public class Basket{
		BlockingQueue<String> basket = Queues.newLinkedBlockingQueue(3);
		public void produce() throws InterruptedException{
			basket.put("an apple");
		}
		public String consume() throws InterruptedException{
			return basket.take();
		}
	}
	class Producer implements Runnable {
		private String instance;
		private Basket basket;
		public Producer(String instance, Basket basket){
			this.instance = instance;
			this.basket = basket;
		}
		@Override
		public void run() {
			try {
				while(true){
					System.out.println("produce ready:" + instance);
					basket.produce();
					System.out.println("produce end:" + instance);
					Thread.sleep(300);
				}
			} catch (InterruptedException e) {
				System.out.println("produce Exception:" + e.toString());
			}
		}
		
	}
	class Consumer implements Runnable {
		private String instance;
		private Basket basket;
		public Consumer(String instance, Basket basket){
			this.instance = instance;
			this.basket = basket;
		}
		@Override
		public void run() {
			try {
				while(true){
					System.out.println("consume ready: " + instance);
					System.out.println(basket.consume());
					System.out.println("consume: end" + instance);
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				System.out.println("consume: Exception" + e.toString());
				e.printStackTrace();
			}
		}
	} 
	public static void main(String[] args) {
		BlockingQueueTest t = new BlockingQueueTest();
		
		Basket basket = t.new Basket();
		ExecutorService service = Executors.newCachedThreadPool();
		Producer producer = t.new Producer("producer 1", basket);
		Producer producer2 = t.new Producer("producer 2", basket);
		Consumer consumer = t.new Consumer("consume 1", basket);
		service.submit(producer);
		service.submit(producer2);
		service.submit(consumer);
		
		try {
			Thread.sleep(1000 * 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		service.shutdownNow();
//		service.shutdown();
	}
}
