package demoA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

/*
 * ʹ��NIO��SocketChannel���������Ŀͻ���
 */
public class EchoClient05 {
	private Logger logger = Logger.getLogger(EchoClient05.class);

	private String HOST = "localhost";

	private int PORT = 3015;

	private SocketChannel socketChannel;

	public EchoClient05() throws IOException {
		socketChannel = SocketChannel.open();
		// InetAddress ia = InetAddress.getLocalHost();
		InetSocketAddress isa = new InetSocketAddress(HOST, PORT);
		// socketChannel.connect()��Զ��������������
		// Ĭ�ϲ�������ģʽ
		socketChannel.connect(isa);
	}

	public void talk() throws IOException {
		try {
			// ͨ��socketChannel.socket()���������SocketChannel������Socket����
			// Ȼ������Socket�л�������������������һ���еķ��ͺͽ������ݡ�
			// ��÷������Ӧ��Ϣ��������
			InputStream socketIn = socketChannel.socket().getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socketIn));
			// ������˷�����Ϣ�������
			OutputStream socketOut = socketChannel.socket().getOutputStream();
			PrintWriter pw = new PrintWriter(socketOut, true);
			BufferedReader localReader = new BufferedReader(
					new InputStreamReader(System.in));
			String msg = null;
			while ((msg = localReader.readLine()) != null) {
				pw.println(msg);
				logger.info(br.readLine());
				if (msg.equals("bye"))
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new EchoClient05().talk();
	}
}
