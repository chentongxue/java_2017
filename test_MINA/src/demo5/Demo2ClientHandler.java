package demo5;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class Demo2ClientHandler extends IoHandlerAdapter {
	private static Logger logger = Logger.getLogger(Demo2ClientHandler.class);

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if (message instanceof ChannelInfoResponse) {
			ChannelInfoResponse res = (ChannelInfoResponse) message;
			String channelName = res.getChannelName();
			EventDto[] events = res.getEvents();
			logger.info("�ͻ��˽��յ�����ϢΪ��channelName=" + channelName);
			if(events!=null && events.length>0){
				for (int i = 0; i < events.length; i++) {
					EventDto edt = events[i];
					logger.info("�ͻ��˽��յ�����ϢΪ��BeginTime=" + edt.getBeginTime());
					logger.info("�ͻ��˽��յ�����ϢΪ��DayIndex=" + edt.getDayIndex());
					logger.info("�ͻ��˽��յ�����ϢΪ��EventName=" + edt.getEventName());
					logger.info("�ͻ��˽��յ�����ϢΪ��Status=" + edt.getStatus());
					logger.info("�ͻ��˽��յ�����ϢΪ��TotalTime=" + edt.getTotalTime());
					logger.info("�ͻ��˽��յ�����ϢΪ��url=" + edt.getUrl());
				}
			}
		}else{
			logger.info("δ֪���ͣ�");
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		logger.error("�ͻ��˷����쳣...", cause);
	}
}
