package demo3;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
public class DemoServerHandler extends IoHandlerAdapter {
	public static Logger logger = Logger.getLogger(DemoServerHandler.class);

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		PhoneMessageDto phoneMes = (PhoneMessageDto) message;
		String sendPhone = phoneMes.getSendPhone();
		String receivePhone = phoneMes.getReceivePhone();
		String mes = phoneMes.getMessage();
		logger.info("发送人手机号码：" + sendPhone);
		logger.info("接受人手机号码：" + receivePhone);
		logger.info("发送信息：" + mes);

		// 短信信息存入移动服务端数据库
		// ............

		session.write("发送成功！");
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		session.close();
	}
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("服务端发送异常...", cause);
	}
}
