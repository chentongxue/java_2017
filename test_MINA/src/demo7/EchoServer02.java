package demo7;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

/*
 * 为每个客户端分配一个线程
 * 服务器的主线程负责接收客户的连接
 * 每次接收到一个客户连接，就会创建一个工作线程，由它负责与客户的通信
 */
public class EchoServer02 {
	private Logger logger = Logger.getLogger(EchoServer02.class);

	private int PORT = 3015;

	private ServerSocket serverSocket;

	public EchoServer02() throws IOException {
		serverSocket = new ServerSocket(PORT);
		logger.info("服务器端启动....   端口号：" + PORT);
	}

	public void service() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept(); // 请求到达
				Thread workThread = new Thread(new Server02Handler(socket)); // 创建线程
				workThread.start(); // 启动线程
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new EchoServer02().service();
	}
}
