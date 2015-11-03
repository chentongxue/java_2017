package demo9;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/*
 * ʹ��JDK�Դ����̳߳�ExecutorService
 * ���̴߳���ͻ�������
 */
public class EchoServer04 {
	private Logger logger = Logger.getLogger(EchoServer04.class);

	private int PORT = 3015;

	private ServerSocket serverSocket;

	private ExecutorService executorService; // �̳߳�

	private final int POOL_SIZE = 4; // ����CPUʱ�̳߳��еĹ����̸߳���

	public EchoServer04() throws IOException {
		serverSocket = new ServerSocket(PORT);
		// �����̳߳�
		// Runtime��availableProcessors()�������ص�ǰϵͳ��CPU����
		// ϵͳ��CPUԽ�࣬�̳߳��й����̵߳���ĿҲԽ��
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors()
				* POOL_SIZE);
		logger.info("���������....    �˿ںţ�" + PORT);
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
