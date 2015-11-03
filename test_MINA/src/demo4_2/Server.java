package demo4_2;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class Server {
	private static Logger logger = Logger.getLogger(Server.class);

	private static int PORT = 3005;

	public static void main(String[] args) {
		IoAcceptor acceptor = null;   // ��������
		try {
			// ����һ����������server�˵�Socket
			acceptor = new NioSocketAcceptor();
			
			
			// ���ù�������ʹ��Mina�ṩ���ı����з����������
			acceptor.getFilterChain().addLast( //�����Ϣ������
					"codec",
					new ProtocolCodecFilter(new MyTextLineCodecFactory()));
			// ���ö�ȡ���ݵĻ�������С
			acceptor.getSessionConfig().setReadBufferSize(2048);
			// ��дͨ��10�����޲����������״̬
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
			// ���߼�������
			acceptor.setHandler(new ServerHandler()); // ���ҵ����
			// �󶨶˿�
			acceptor.bind(new InetSocketAddress(PORT));
			logger.info("����������ɹ�...     �˿ں�Ϊ��" + PORT);
		} catch (Exception e) {
			logger.error("����������쳣....", e);
			e.printStackTrace();
		}
	}
}
