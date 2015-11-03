package demo5;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class TestClient02 {
	private static Logger logger = Logger.getLogger(TestClient02.class);

	private static String HOST = "127.0.0.1";

	private static int PORT = 3005;

	public static void main(String[] args) {
		// ����һ���������Ŀͻ��˳���
		IoConnector connector = new NioSocketConnector();
		// �������ӳ�ʱʱ��
		connector.setConnectTimeout(30000);
		// ��ӹ�����
		connector.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new MyMessageCodecFactory(
						new MyMessageDecoder(Charset.forName("utf-8")),
						new MyMessageEncoder(Charset.forName("utf-8")))));
		// ���ҵ���߼���������
		connector.setHandler(new Demo2ClientHandler());
		IoSession session = null;
		try {
			ConnectFuture future = connector.connect(new InetSocketAddress(
					HOST, PORT));// ��������
			future.awaitUninterruptibly();// �ȴ����Ӵ������
			session = future.getSession();// ���session
			ChannelInfoRequest req = new ChannelInfoRequest(); // ��������
			req.setChannel_id(12345);
			req.setChannel_desc("mina��������ŶŶ....��ѽѽ������");
			session.write(req);// ������Ϣ
		} catch (Exception e) {
			logger.error("�ͻ��������쳣...", e);
		}

		session.getCloseFuture().awaitUninterruptibly();// �ȴ����ӶϿ�
		connector.dispose();
	}
}
