package demo3;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/*
 * ģ���ֻ�������
 */
public class TestClient {
	private static Logger logger = Logger.getLogger(TestClient.class);
	private static String HOST = "127.0.0.1";
	private static int PORT = 3005;

	public static void main(String[] args) {
		// ����һ���������Ŀͻ��˳���
		IoConnector connector = new NioSocketConnector();
		// �������ӳ�ʱʱ��
		connector.setConnectTimeout(30000);
		// ���ù�����
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		// ���ҵ���߼���������
		connector.setHandler(new Demo1ClientHandler());
		IoSession session = null;
		try {
			ConnectFuture future = connector.connect(new InetSocketAddress(
					HOST, PORT));// ��������
			future.awaitUninterruptibly();// �ȴ����Ӵ������
			session = future.getSession();// ���session

			PhoneMessageDto sendMes = new PhoneMessageDto();
			sendMes.setSendPhone("13681803609"); // ��ǰ�����˵��ֻ�����
			sendMes.setReceivePhone("13721427169"); // �������ֻ�����
			sendMes.setMessage("���Է��Ͷ��ţ�����Ƕ�����ϢŶ����Ȼ�����������Ƶ�Ŷ....");

			session.write(sendMes);// ���͸��ƶ������
		} catch (Exception e) {
			logger.error("�ͻ��������쳣...", e);
		}
		session.getCloseFuture().awaitUninterruptibly();// �ȴ����ӶϿ�
		connector.dispose();
	}
}
