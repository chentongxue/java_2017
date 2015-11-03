package demoA;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/*
 * 使用NIO的ServerSocketChannel创建阻塞的Socket服务端
 * 使用JDK自带的线程池ExecutorService，多线程处理客户端请求
 */
public class EchoServer05 {
	private Logger logger = Logger.getLogger(EchoServer05.class);

	private int PORT = 3015;

	private ServerSocketChannel serverSocketChannel = null;

	private ExecutorService executorService; // 线程池

	private static final int POOL_MULTIPLE = 4; // 单个CPU时线程池中的工作线程个数

	public EchoServer05() throws IOException {
		// 创建线程池
		// Runtime的availableProcessors()方法返回当前系统的CPU格式
		// 系统的CPU越多，线程池中工作线程的数目也越多
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors()
				* POOL_MULTIPLE);
		// ServerSocketChannel并没有public类型的构造方法，
		// 必须通过它的静态方法open()来创建ServerSocketChannel对象
		// 默认是阻塞模式的，通过configureBlocking(false)设置为非阻塞模式
		serverSocketChannel = ServerSocketChannel.open();
		// 使得在同一个主机上关闭了服务器程序，紧接着再启动该服务器程序时，
		// 可以顺利绑定到相同的端口
		serverSocketChannel.socket().setReuseAddress(true);
		// 每个ServerSocketChannel对象都与一个ServerSocket对象关联
		// ServerSocketChannel的socket()方法返回与它关联的ServerSocket对象
		serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
		logger.info("服务端启动....   端口号：" + PORT);
	}

	public void service() {
		while (true) { // 阻塞
			SocketChannel socketChannel = null;
			try {
				socketChannel = serverSocketChannel.accept(); // 等待连接
				// 多线程处理
				executorService.execute(new Server05Handler(socketChannel));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new EchoServer05().service();
	}
}
