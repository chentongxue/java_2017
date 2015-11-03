package demo8;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/*
 * �Զ����̳߳�
 * ���̴߳���ͻ�������
 */
public class EchoServer03 {
	private Logger logger = Logger.getLogger(EchoServer03.class);

	private int PORT = 3015;

	private ServerSocket serverSocket;

	private ThreadPool threadPool; // �̳߳�

	private final int POOL_SIZE = 4; // ����CPUʱ�̳߳��еĹ����̸߳���

	public EchoServer03() throws IOException {
		serverSocket = new ServerSocket(PORT);
		// �����̳߳�
		// Runtime��availableProcessors()�������ص�ǰϵͳ��CPU����
		// ϵͳ��CPUԽ�࣬�̳߳��й����̵߳���ĿҲԽ��
		threadPool = new ThreadPool(Runtime.getRuntime().availableProcessors()
				* POOL_SIZE);
		logger.info("���������....    �˿ںţ�" + PORT);
	}

	public void service() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				// ����ͻ�ͨ�ŵ����񽻸��̳߳�
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
