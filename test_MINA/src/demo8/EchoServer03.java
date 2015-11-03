package demo8;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/*
 * 自定义线程池
 * 多线程处理客户端请求
 */
public class EchoServer03 {
	private Logger logger = Logger.getLogger(EchoServer03.class);

	private int PORT = 3015;

	private ServerSocket serverSocket;

	private ThreadPool threadPool; // 线程池

	private final int POOL_SIZE = 4; // 单个CPU时线程池中的工作线程个数

	public EchoServer03() throws IOException {
		serverSocket = new ServerSocket(PORT);
		// 创建线程池
		// Runtime的availableProcessors()方法返回当前系统的CPU个数
		// 系统的CPU越多，线程池中工作线程的数目也越多
		threadPool = new ThreadPool(Runtime.getRuntime().availableProcessors()
				* POOL_SIZE);
		logger.info("服务端启动....    端口号：" + PORT);
	}

	public void service() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				// 把与客户通信的任务交给线程池
				threadPool.execute(new Server02Handler(socket));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new EchoServer03().service();
	}
}
