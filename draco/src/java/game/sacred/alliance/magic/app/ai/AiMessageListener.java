package sacred.alliance.magic.app.ai;

import sacred.alliance.magic.vo.AbstractRole;

public interface AiMessageListener {

	public Object onEvent(MessageType messageType,AbstractRole who) ;
}
