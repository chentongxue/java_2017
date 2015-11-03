package demo8;

import java.util.LinkedList;

import org.apache.log4j.Logger;
/*
 * �Զ����̳߳�
 */
public class ThreadPool extends ThreadGroup {
	private Logger logger = Logger.getLogger(ThreadPool.class);

	private boolean isClosed = false; // �̳߳��Ƿ�ر�

	// ���������LinkedList�У�LinkedList��֧��ͬ����
	// �������������ͻ�ȡ����ķ��������б���ʹ��synchronized�ؼ���
	private LinkedList<Runnable> workQueue;// ��ʾ��������

	private static int threadPoolID; // ��ʾ�̳߳�ID

	private int threadID; // ��ʾ�����߳�ID

	// ����һ���߳���
	public ThreadPool(int poolSize) { // poolSize��ָ�̳߳��й����̵߳���Ŀ
		super("ThreadPool-" + (threadPoolID++)); // �߳�����
		setDaemon(true);
		workQueue = new LinkedList<Runnable>();// ������������
		for (int i = 0; i < poolSize; i++)
			new WorkThread().start(); // ���������������̣߳������������Ϊ�գ������й����̴߳�������״̬��
	}

	// �������������һ�������ɹ����߳�ȥִ�и�����
	public synchronized void execute(Runnable task) {
		if (isClosed) { // �̳߳عر����׳�IllegalStateException�쳣
			throw new IllegalStateException();
		}
		if (task != null) {
			workQueue.add(task);
			notify(); // ��������getTask()�����еȴ�����Ĺ����߳�
		}
	}

	// �ӹ���������ȡ��һ������ ----�����̻߳���ô˷���
	protected synchronized Runnable getTask() throws InterruptedException {
		while (workQueue.size() == 0) {
			if (isClosed)
				return null;
			wait(); // �����������û�����񣬾͵ȴ�����
		}
		return workQueue.removeFirst();
	}

	// �ر��̳߳�
	public synchronized void close() {
		if (!isClosed) {
			isClosed = true;
			workQueue.clear(); // ��չ�������
			interrupt();// �ж����й����̣߳��÷����̳���ThreadGroup��
		}
	}

	// �ȴ������̰߳���������ִ����
	public void join() {
		synchronized (this) {
			isClosed = true;
			notifyAll(); // ���ѻ���getTask()�����еȴ�����Ĺ����߳�
		}
		// activeCount()������ThreadGroup��ģ�����߳����е�ǰ���л��ŵĹ����߳���Ŀ
		Thread[] threads = new Thread[activeCount()];
		// enumerate�����̳���ThreadGroup�࣬����߳����е�ǰ���л��ŵĹ����߳�
		int count = enumerate(threads);
		for (int i = 0; i < count; i++) {// �ȴ����й����߳����н���
			try {
				threads[i].join(); // �ȴ������߳����н���
			} catch (InterruptedException ex) {
				logger.error("�����̳߳���...", ex);
			}
		}
	}

	// �ڲ��࣬�����߳�
	private class WorkThread extends Thread {
		public WorkThread() {
			// ���뵱ǰ��ThreadPool�߳�����
			// Thread(ThreadGroup group, String name)
			super(ThreadPool.this, "WorkThread-" + (threadID++));
		}

		public void run() {
			// isInterrupted()�����̳���ThreadGroup�࣬�ж��߳��Ƿ��ж�
			while (!isInterrupted()) {
				Runnable task = null;
				try {
					task = getTask(); // �õ�����
				} catch (InterruptedException ex) {
					logger.error("��������쳣...", ex);
				}
				// ���getTask()����null�����߳�ִ��getTask()ʱ���жϣ���������߳�
				if (task == null)
					return;

				try {
					// �������񣬲����쳣
					task.run(); // ֱ�ӵ���task��run����
				} catch (Throwable t) {
					logger.error("����ִ���쳣...", t);
				}
			}// #while end
		}// #run end
	}// # WorkThread class end
}
