package demo5;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


public class Demo2ServerHandler extends IoHandlerAdapter {
	public static Logger logger = Logger.getLogger(Demo2ServerHandler.class);

	public void sessionCreated(IoSession session) throws Exception {
		logger.info("�������ͻ��˴�������...");
	}

	public void sessionOpened(IoSession session) throws Exception {
		logger.info("�������ͻ������Ӵ�...");
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if (message instanceof ChannelInfoRequest) {
			ChannelInfoRequest req = (ChannelInfoRequest) message;
			int channel_id = req.getChannel_id();
			String channel_desc = req.getChannel_desc();
			logger.info("����˽��յ�������Ϊ��channel_id=" + channel_id
					+ "   channel_desc=" + channel_desc);
			// ================��������������ѯ���ݿ�ȣ�������....=============
			ChannelInfoResponse res = new ChannelInfoResponse();
			res.setChannelName("CCTV1����Ƶ��");
			EventDto[] events = new EventDto[2];
			for (int i = 0; i < events.length; i++) {
				EventDto edt = new EventDto();
				edt.setBeginTime(10);
				edt.setDayIndex(1);
				edt.setEventName("���Ƶ�һ��" + i);
				edt.setStatus(1);
				edt.setTotalTime(100 + i);
				edt.setUrl("www.baidu.com");
				events[i] = edt;
			}
			res.setEvents(events);
			session.write(res);
		} else {
			logger.info("δ֪����");
		}

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		session.close();
		logger.info("����˷�����Ϣ�ɹ�...");
	}

	@Override
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
