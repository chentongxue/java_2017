package demo9;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import org.apache.log4j.Logger;

public class Server02Handler implements Runnable {
	private Logger logger = Logger.getLogger(Server02Handler.class);

	private Socket socket;

	public Server02Handler(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			logger.info("һ���µ�����ﵽ������  " + socket.getInetAddress() + ":"
					+ socket.getPort());
			InputStream socketIn = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					socketIn));
			OutputStream socketOut = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(socketOut, true);

			String msg = null;
			while ((msg = br.readLine()) != null) {
				logger.info("������ܵ�����ϢΪ��" + msg);
				pw.println(new Date()); // ���ͻ�����Ӧ�����ַ���
				if (msg.equals("bye"))
					break;
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
