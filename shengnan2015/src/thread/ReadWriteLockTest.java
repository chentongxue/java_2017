package thread;

import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁，读和写是互斥的,写和写也是互斥的，但是读和读不互斥
 * 思考一下，用sychronized如何实现
 * 
 * @author Administrator
 * 
 */
public class ReadWriteLockTest {

	public static void main(String[] args) {
		final Data data = new Data();
		for(int i = 0;i<3; i++){
			new Thread(new Runnable(){

				@Override
				public void run() {
					for(int j = 0;j<5; j++){
						data.set(new Random().nextInt(30));
					}
				}
				
			}).start();
		}//for
		for(int i = 0;i<3; i++){
			new Thread(new Runnable(){
				
				@Override
				public void run() {
					for(int j = 0;j<5; j++){
						data.get();
					}
				}
				
			}).start();
		}//for
	}

}

class Data {
	private int data;// 共享数据
	private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	public void set(int data) {
		rwl.writeLock().lock();// 取到写锁
		try {
			System.out.println(Thread.currentThread().getName()
					+ "准备写入数据  20毫秒");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.data = data;
			System.out.println(Thread.currentThread().getName() + "20毫秒 后写入"
					+ this.data);
		} finally {
			rwl.writeLock().unlock();// 释放写锁
		}
	}

	public void get() {
		rwl.readLock().lock();// 读取到锁
		try {
			System.out.println(Thread.currentThread().getName() + "准备20毫秒后读数据");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + "读数据"
					+ this.data);
		} finally {
			rwl.readLock().unlock();// 释放读锁
		}
	}
}