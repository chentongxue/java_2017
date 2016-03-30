package sacred.alliance.magic.component;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.channel.ChannelSession;

public interface EventPublisher {

	public void publish(String eventKey,Message message,
			ChannelSession session);
	
	public void publish(String eventKey,Message message,
			ChannelSession session,boolean cumulate) ;
	
}
