package demo7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

/*
 * 使用Socket创建客户端请求
 */
public class EchoClient01 {
	private Logger logger = Logger.getLogger(EchoClient01.class);

	private String HOST = "localhost";

	private int PORT = 3015;

	private Socket socket;

	public EchoClient01() throws IOException {
		socket = new Socket(HOST, PORT);
	}

	public void talk() throws IOException {
		try {
			// 获得服务端响应信息的输入流
			InputStream socketIn = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socketIn));
			// 给服务端发送信息的输出流
			OutputStream socketOut = socket.getOutputStream();
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
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {
		new EchoClient01().talk();
	}
}
