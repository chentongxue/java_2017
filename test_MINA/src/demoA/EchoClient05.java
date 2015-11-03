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
 * 使用NIO的SocketChannel创建阻塞的客户端
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
		// socketChannel.connect()与远程主机建立连接
		// 默认采用阻塞模式
		socketChannel.connect(isa);
	}

	public void talk() throws IOException {
		try {
			// 通过socketChannel.socket()方法获得与SocketChannel关联的Socket对象，
			// 然后从这个Socket中获得输出流与输入流，再一行行的发送和接受数据。
			// 获得服务端响应信息的输入流
			InputStream socketIn = socketChannel.socket().getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socketIn));
			// 给服务端发送信息的输出流
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
