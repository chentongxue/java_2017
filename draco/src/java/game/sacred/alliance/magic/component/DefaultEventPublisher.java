package sacred.alliance.magic.component;

import sacred.alliance.magic.channel.ChannelEvent;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.channel.ChannelHandler;
import sacred.alliance.magic.core.channel.ChannelSession;

public  class DefaultEventPublisher implements EventPublisher{

	private ChannelHandler channelHandler ;
	
	@Override
	public void publish(String eventKey, Message message, 
			ChannelSession session) {
		this.publish(eventKey, message, session, false);
	}

	public ChannelHandler getChannelHandler() {
		return channelHandler;
	}

	public void setChannelHandler(ChannelHandler channelHandler) {
		this.channelHandler = channelHandler;
	}

	@Override
	public void publish(String eventKey, Message message,
			ChannelSession session, boolean cumulate) {
		ChannelEvent event = new ChannelEvent();
		event.setEventKey(eventKey);
		event.setMessage(message);
		event.setSession(session);
		event.setCumulate(cumulate);
		this.channelHandler.eventReceived(event);
	}

	
}
