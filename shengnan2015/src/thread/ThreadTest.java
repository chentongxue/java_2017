package thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTest {

	private int j;
	private Lock lock = new ReentrantLock();

	public static void main(String[] args) {
		ThreadTest tt = new ThreadTest();
		for (int i = 0; i < 2; i++) {
			new Thread(tt.new Adder()).start();
			new Thread(tt.new Subtractor()).start();
		}
	}
	//¼Ó
	private class Adder implements Runnable {

		@Override
		public void run() {
			while (true) {
				lock.lock();// -----------
				try {
					System.out.println("++++||" + j--);
				} finally {
					lock.unlock();
				}
			}
		}

	}
	//¼õ
	private class Subtractor implements Runnable {
		@Override
		public void run() {
			while (true) {
				lock.lock();// -----------
				try {
					System.out.println("----||" + j--);
				} finally {
					lock.unlock();
				}
			}
		}
	}

}
