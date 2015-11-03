package thread;

public class MultiThread {

	public static void main(String[] args) {
		new Thread(new Thread1()).start();
		try{
			Thread.sleep(10);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Thread(new Thread2()).start();
	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private static class Thread1 implements Runnable {
		@Override
		public void run() {
			synchronized (MultiThread.class) {
				System.out.println("�߳� 1   ������");
				System.out.println("�߳� 1 waiting");

				try {
					MultiThread.class.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("�߳� 1 goning on");
				System.out.println("�߳� 1 being over");
			}
		}
	}

	/**
	 * 
	 */
	private static class Thread2 implements Runnable {

		@Override
		public void run() {
			synchronized (MultiThread.class) {
				System.out.println("�߳�  2  ������");
				System.out.println("�߳�  2  ���������̣߳�����");
				MultiThread.class.notify();
				System.out.println("�߳�  2  ˯10��");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("�߳� 2 goning on");
				System.out.println("�߳� 2 being over");
			}
		}

	}

}
