package thread;

public class ThreadDead3 {
	private int j;
   
	public static void main(String args[]) {
		new Thread().start();
	}

	//
	private synchronized void inc() {
		j++;
		System.out.println(Thread.currentThread().getName() + "Ôö£º" + j);
	}

	private synchronized void dec() {
		j--;
		System.out.println(Thread.currentThread().getName() + "¼õ£º" + j);
	}

	// Ôö
	class Inc implements Runnable {
		public void run() {
			for (int i = 0; i < 100; i++) {
				inc();
			}
		}
	}

	class Dec implements Runnable {
		public void run() {
			for (int i = 0; i < 100; i++) {
				dec();
			}
		}
	}
}
