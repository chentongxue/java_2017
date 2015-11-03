package demo7;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/*
 * Ϊÿ���ͻ��˷���һ���߳�
 * �����������̸߳�����տͻ�������
 * ÿ�ν��յ�һ���ͻ����ӣ��ͻᴴ��һ�������̣߳�����������ͻ���ͨ��
 */
public class EchoServer02 {
	private Logger logger = Logger.getLogger(EchoServer02.class);

	private int PORT = 3015;

	private ServerSocket serverSocket;

	public EchoServer02() throws IOException {
		serverSocket = new ServerSocket(PORT);
		logger.info("������������....   �˿ںţ�" + PORT);
	}

	public void service() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept(); // ���󵽴�
				Thread workThread = new Thread(new Server02Handler(socket)); // �����߳�
				workThread.start(); // �����߳�
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new EchoServer02().service();
	}
}
