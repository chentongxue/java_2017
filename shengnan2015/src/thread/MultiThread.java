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
				System.out.println("线程 1   。。。");
				System.out.println("线程 1 waiting");

				try {
					MultiThread.class.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("线程 1 goning on");
				System.out.println("线程 1 being over");
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
				System.out.println("线程  2  。。。");
				System.out.println("线程  2  唤醒其他线程！！！");
				MultiThread.class.notify();
				System.out.println("线程  2  睡10秒");
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("线程 2 goning on");
				System.out.println("线程 2 being over");
			}
		}

	}

}
