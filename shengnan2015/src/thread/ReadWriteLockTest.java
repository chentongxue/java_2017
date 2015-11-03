package thread;

import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ��д��������д�ǻ����,д��дҲ�ǻ���ģ����Ƕ��Ͷ�������
 * ˼��һ�£���sychronized���ʵ��
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
	private int data;// ��������
	private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	public void set(int data) {
		rwl.writeLock().lock();// ȡ��д��
		try {
			System.out.println(Thread.currentThread().getName()
					+ "׼��д������  20����");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.data = data;
			System.out.println(Thread.currentThread().getName() + "20���� ��д��"
					+ this.data);
		} finally {
			rwl.writeLock().unlock();// �ͷ�д��
		}
	}

	public void get() {
		rwl.readLock().lock();// ��ȡ����
		try {
			System.out.println(Thread.currentThread().getName() + "׼��20����������");
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + "������"
					+ this.data);
		} finally {
			rwl.readLock().unlock();// �ͷŶ���
		}
	}
}