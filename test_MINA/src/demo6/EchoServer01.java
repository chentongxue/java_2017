package demo6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import org.apache.log4j.Logger;

public class EchoServer01 {
	private Logger logger = Logger.getLogger(EchoServer01.class);

	private int PORT = 3015;

	private ServerSocket serverSocket;

	public EchoServer01() throws IOException {
		// ���������󳤶�Ϊ5
		serverSocket = new ServerSocket(PORT,5);
		logger.info("���������...   �˿ںţ�" + PORT);
	}

	public void service() {
		while (true) { 
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				logger.info("һ���µ����ӵ����ַΪ��" + socket.getInetAddress() + ":"
						+ socket.getPort());
				// ��ÿͻ��˷�����Ϣ��������
				InputStream socketIn = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						socketIn));
				// ���ͻ�����Ӧ��Ϣ�������
				OutputStream socketOut = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(socketOut, true);

				String msg = null;
				while ((msg = br.readLine()) != null) {
					logger.info("����˽��ܵ�����ϢΪ��" + msg);
					pw.println("��Ӧ��Ϣ��" + new Date().toString());// ���ͻ���һ�������ַ���
					if (msg.equals("bye")) {
						logger.info("�ͻ�������Ͽ�");
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (socket != null)
						socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new EchoServer01().service();
	}
}
