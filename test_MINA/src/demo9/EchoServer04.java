package demo9;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/*
 * 使用JDK自带的线程池ExecutorService
 * 多线程处理客户端请求
 */
public class EchoServer04 {
	private Logger logger = Logger.getLogger(EchoServer04.class);

	private int PORT = 3015;

	private ServerSocket serverSocket;

	private ExecutorService executorService; // 线程池

	private final int POOL_SIZE = 4; // 单个CPU时线程池中的工作线程个数

	public EchoServer04() throws IOException {
		serverSocket = new ServerSocket(PORT);
		// 创建线程池
		// Runtime的availableProcessors()方法返回当前系统的CPU个数
		// 系统的CPU越多，线程池中工作线程的数目也越多
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors()
				* POOL_SIZE);
		logger.info("服务端启动....    端口号：" + PORT);
	}

	public void service() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				executorService.execute(new Server02Handler(socket));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new EchoServer04().service();
	}
}
