package demo5;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class TestServer02 {
	private static Logger logger = Logger.getLogger(TestServer02.class);

	private static int PORT = 3005;

	public static void main(String[] args) {
		IoAcceptor acceptor = null;
		try {
			// ����һ����������server�˵�Socket
			acceptor = new NioSocketAcceptor();

			// ���ù�����������Դ��ı��������
			acceptor.getFilterChain().addLast(
					"codec",
					new ProtocolCodecFilter(new MyMessageCodecFactory(
							new MyMessageDecoder(Charset.forName("utf-8")),
							new MyMessageEncoder(Charset.forName("utf-8")))));
			// ������־������
			LoggingFilter lf = new LoggingFilter();
			lf.setMessageReceivedLogLevel(LogLevel.DEBUG);
			acceptor.getFilterChain().addLast("logger", lf);
			// ���IoSessionConfig����
			IoSessionConfig cfg = acceptor.getSessionConfig();
			// ��дͨ��10�����޲����������״̬
			cfg.setIdleTime(IdleStatus.BOTH_IDLE, 100);

			// ���߼�������
			acceptor.setHandler(new Demo2ServerHandler());
			// �󶨶˿�
			acceptor.bind(new InetSocketAddress(PORT));
			logger.info("����������ɹ�...     �˿ں�Ϊ��" + PORT);
		} catch (Exception e) {
			logger.error("����������쳣....", e);
			e.printStackTrace();
		}
	}
}
