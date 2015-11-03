package test;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogManager {
	// 日志缓存队列
	private final static BlockingQueue<String> LOG_QUEUE = new LinkedBlockingQueue<String>();

	public static void put(String log) {
		try {
			LOG_QUEUE.put(log);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String get() {
		try {
			return LOG_QUEUE.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}