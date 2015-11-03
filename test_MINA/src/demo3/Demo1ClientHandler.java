package demo3;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class Demo1ClientHandler extends IoHandlerAdapter {
	private static Logger logger = Logger.getLogger(Demo1ClientHandler.class);

	
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String msg = message.toString();
		logger.info("�ͻ��˽��յ�����ϢΪ��" + msg);
	}

	
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("�ͻ��˷����쳣...", cause);
	}
}
