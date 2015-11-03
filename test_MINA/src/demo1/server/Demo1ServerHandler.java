package demo1.server;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class Demo1ServerHandler extends IoHandlerAdapter {
	public static Logger logger = Logger.getLogger(Demo1ServerHandler.class);

	
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("�������ͻ��˴�������...");
	}

	
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("�������ͻ������Ӵ�...");
	}

	
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String msg = message.toString();
		logger.info("����˽��յ�������Ϊ--->>>��\n                      " + msg);
		if ("bye".equals(msg)) { // ����˶Ͽ����ӵ�����
			session.close();
		}
		Date date = new Date();
		session.write(date);
	}
	
	public void messageSent(IoSession session, Object message) throws Exception {
//		session.close();
		logger.info("bao close!...");
		logger.info("����˷�����Ϣ�ɹ�...");
	}
	
	public void sessionClosed(IoSession session) throws Exception {

	}
	
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		logger.info("����˽������״̬...");
	}
	
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("����˷����쳣...", cause);
	}
}
