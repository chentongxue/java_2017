package demo3;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class TestServer {
	private static Logger logger = Logger.getLogger(TestServer.class);
	private static int PORT = 3005;
	public static void main(String[] args) {
		IoAcceptor acceptor = null;
		try {
			// ����һ����������server�˵�Socket
			acceptor = new NioSocketAcceptor();
			// ֱ�ӷ��Ͷ���		
			acceptor.getFilterChain().addLast(
					"codec",
					new ProtocolCodecFilter(
							new ObjectSerializationCodecFactory()));

			// ���IoSessionConfig����
			IoSessionConfig cfg = acceptor.getSessionConfig();
			// ��дͨ��10�����޲����������״̬
			cfg.setIdleTime(IdleStatus.BOTH_IDLE, 100);

			// ���߼�������
			acceptor.setHandler(new DemoServerHandler());
			// �󶨶˿�
			acceptor.bind(new InetSocketAddress(PORT));
			logger.info("����������ɹ�...     �˿ں�Ϊ��" + PORT);
		} catch (Exception e) {
			logger.error("����������쳣....", e);
			e.printStackTrace();
		}
	}
}
