package demo1.server;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class Demo1ServerHandler extends IoHandlerAdapter {
	public static Logger logger = Logger.getLogger(Demo1ServerHandler.class);

	
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("服务端与客户端创建连接...");
	}

	
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("服务端与客户端连接打开...");
	}

	
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String msg = message.toString();
		logger.info("服务端接收到的数据为--->>>：\n                      " + msg);
		if ("bye".equals(msg)) { // 服务端断开连接的条件
			session.close();
		}
		Date date = new Date();
		session.write(date);
	}
	
	public void messageSent(IoSession session, Object message) throws Exception {
//		session.close();
		logger.info("bao close!...");
		logger.info("服务端发送信息成功...");
	}
	
	public void sessionClosed(IoSession session) throws Exception {

	}
	
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		logger.info("服务端进入空闲状态...");
	}
	
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("服务端发送异常...", cause);
	}
}
