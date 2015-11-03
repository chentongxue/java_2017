package demo2;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class Demo3ServerHandler extends IoHandlerAdapter {
	public static Logger logger = Logger.getLogger(Demo3ServerHandler.class);

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String phoneMes = message.toString();
		String[] megs = phoneMes.split(";");
		String sendPhone = megs[0];
		String receivePhone = megs[1];
		String mes = megs[2];
		logger.info("�������ֻ�����--->>>" + sendPhone);
		logger.info("�������ֻ�����--->>>" + receivePhone);
		logger.info("������Ϣ--->>>" + mes);

		// ������Ϣ�����ƶ���������ݿ����д���ֻ�����ת������
		// ............

		session.write("���ͳɹ���"); // �����ֻ�������Ϣ�ɹ���
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		session.close();
	}

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("����˷����쳣...", cause);
	}
}
