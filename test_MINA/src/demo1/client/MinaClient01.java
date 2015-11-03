package demo1.client;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class MinaClient01 {
	private static Logger logger = Logger.getLogger(MinaClient01.class);

	private static String HOST = "127.0.0.1";

	private static int PORT = 3005;

	public static void main(String[] args) {
		// ����һ���������Ŀͻ��˳���
		IoConnector connector = new NioSocketConnector(); // ��������
		// �������ӳ�ʱʱ��
		connector.setConnectTimeout(30000);
		// ��ӹ�����
		connector.getFilterChain().addLast(
				// �����Ϣ������
				"codec",
				new ProtocolCodecFilter(
						new TextLineCodecFactory(
								Charset.forName("UTF-8"), 
								LineDelimiter.WINDOWS.getValue(),
								LineDelimiter.WINDOWS.getValue())));
		// ���ҵ���߼���������
		connector.setHandler(new Demo1ClientHandler());// ���ҵ����
		IoSession session = null;
		try {
			ConnectFuture future = connector.connect(new InetSocketAddress(
					HOST, PORT));// ��������
			future.awaitUninterruptibly();// �ȴ����Ӵ������
			session = future.getSession();// ���session
			session.write("hello mina");// ������Ϣ
		} catch (Exception e) {
			logger.error("�ͻ��������쳣...", e);
		}

		session.getCloseFuture().awaitUninterruptibly();// �ȴ����ӶϿ�
		System.err.println("52------------MinaClient01");
//		connector.dispose();//�Ȳ��ر�
	}
}
