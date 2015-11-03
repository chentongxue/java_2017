package demoA;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/*
 * ʹ��NIO��ServerSocketChannel����������Socket�����
 * ʹ��JDK�Դ����̳߳�ExecutorService�����̴߳���ͻ�������
 */
public class EchoServer05 {
	private Logger logger = Logger.getLogger(EchoServer05.class);

	private int PORT = 3015;

	private ServerSocketChannel serverSocketChannel = null;

	private ExecutorService executorService; // �̳߳�

	private static final int POOL_MULTIPLE = 4; // ����CPUʱ�̳߳��еĹ����̸߳���

	public EchoServer05() throws IOException {
		// �����̳߳�
		// Runtime��availableProcessors()�������ص�ǰϵͳ��CPU��ʽ
		// ϵͳ��CPUԽ�࣬�̳߳��й����̵߳���ĿҲԽ��
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors()
				* POOL_MULTIPLE);
		// ServerSocketChannel��û��public���͵Ĺ��췽����
		// ����ͨ�����ľ�̬����open()������ServerSocketChannel����
		// Ĭ��������ģʽ�ģ�ͨ��configureBlocking(false)����Ϊ������ģʽ
		serverSocketChannel = ServerSocketChannel.open();
		// ʹ����ͬһ�������Ϲر��˷��������򣬽������������÷���������ʱ��
		// ����˳���󶨵���ͬ�Ķ˿�
		serverSocketChannel.socket().setReuseAddress(true);
		// ÿ��ServerSocketChannel������һ��ServerSocket�������
		// ServerSocketChannel��socket()������������������ServerSocket����
		serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
		logger.info("���������....   �˿ںţ�" + PORT);
	}

	public void service() {
		while (true) { // ����
			SocketChannel socketChannel = null;
			try {
				socketChannel = serverSocketChannel.accept(); // �ȴ�����
				// ���̴߳���
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
