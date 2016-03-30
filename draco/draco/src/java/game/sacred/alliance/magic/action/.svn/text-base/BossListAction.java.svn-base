package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0611_BossListReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

public class BossListAction extends BaseAction<C0611_BossListReqMessage> {

	
	@Override
	public Message execute(ActionContext context, C0611_BossListReqMessage reqMsg) {
		return GameContext.getNpcRefreshApp().getBossListRespMessage() ;
	}

}
