package demo8;

import java.util.LinkedList;

import org.apache.log4j.Logger;
/*
 * 自定义线程池
 */
public class ThreadPool extends ThreadGroup {
	private Logger logger = Logger.getLogger(ThreadPool.class);

	private boolean isClosed = false; // 线程池是否关闭

	// 将任务放在LinkedList中，LinkedList不支持同步，
	// 所以在添加任务和获取任务的方法声明中必须使用synchronized关键字
	private LinkedList<Runnable> workQueue;// 表示工作队列

	private static int threadPoolID; // 表示线程池ID

	private int threadID; // 表示工作线程ID

	// 构建一个线程组
	public ThreadPool(int poolSize) { // poolSize是指线程池中工作线程的数目
		super("ThreadPool-" + (threadPoolID++)); // 线程组名
		setDaemon(true);
		workQueue = new LinkedList<Runnable>();// 创建工作队列
		for (int i = 0; i < poolSize; i++)
			new WorkThread().start(); // 创建并启动工作线程（如果工作队列为空，则所有工作线程处于阻塞状态）
	}

	// 向工作队列中添加一个任务，由工作线程去执行该任务
	public synchronized void execute(Runnable task) {
		if (isClosed) { // 线程池关闭则抛出IllegalStateException异常
			throw new IllegalStateException();
		}
		if (task != null) {
			workQueue.add(task);
			notify(); // 唤醒正在getTask()方法中等待任务的工作线程
		}
	}

	// 从工作队列中取出一个任务 ----工作线程会调用此方法
	protected synchronized Runnable getTask() throws InterruptedException {
		while (workQueue.size() == 0) {
			if (isClosed)
				return null;
			wait(); // 如果工作队列没有任务，就等待任务
		}
		return workQueue.removeFirst();
	}

	// 关闭线程池
	public synchronized void close() {
		if (!isClosed) {
			isClosed = true;
			workQueue.clear(); // 清空工作队列
			interrupt();// 中断所有工作线程，该方法继承自ThreadGroup类
		}
	}

	// 等待工作线程把所有任务执行完
	public void join() {
		synchronized (this) {
			isClosed = true;
			notifyAll(); // 唤醒还在getTask()方法中等待任务的工作线程
		}
		// activeCount()方法是ThreadGroup类的，获得线程组中当前所有活着的工作线程数目
		Thread[] threads = new Thread[activeCount()];
		// enumerate方法继承自ThreadGroup类，获得线程组中当前所有活着的工作线程
		int count = enumerate(threads);
		for (int i = 0; i < count; i++) {// 等待所有工作线程运行结束
			try {
				threads[i].join(); // 等待工作线程运行结束
			} catch (InterruptedException ex) {
				logger.error("工作线程出错...", ex);
			}
		}
	}

	// 内部类，工作线程
	private class WorkThread extends Thread {
		public WorkThread() {
			// 加入当前的ThreadPool线程组中
			// Thread(ThreadGroup group, String name)
			super(ThreadPool.this, "WorkThread-" + (threadID++));
		}

		public void run() {
			// isInterrupted()方法继承自ThreadGroup类，判断线程是否中断
			while (!isInterrupted()) {
				Runnable task = null;
				try {
					task = getTask(); // 得到任务
				} catch (InterruptedException ex) {
					logger.error("获得任务异常...", ex);
				}
				// 如果getTask()返回null或者线程执行getTask()时被中断，则结束此线程
				if (task == null)
					return;

				try {
					// 运行任务，捕获异常
					task.run(); // 直接调用task的run方法
				} catch (Throwable t) {
					logger.error("任务执行异常...", t);
				}
			}// #while end
		}// #run end
	}// # WorkThread class end
}
