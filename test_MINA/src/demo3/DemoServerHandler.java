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
		logger.info("�������ֻ����룺" + sendPhone);
		logger.info("�������ֻ����룺" + receivePhone);
		logger.info("������Ϣ��" + mes);

		// ������Ϣ�����ƶ���������ݿ�
		// ............

		session.write("���ͳɹ���");
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		session.close();
	}
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("����˷����쳣...", cause);
	}
}
