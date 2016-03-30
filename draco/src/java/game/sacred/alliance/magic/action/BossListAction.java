package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0611_BossListReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class BossListAction extends BaseAction<C0611_BossListReqMessage> {

	
	@Override
	public Message execute(ActionContext ct, C0611_BossListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(ct);
		if(role == null){
			return null;
		}
		return GameContext.getNpcRefreshApp().getBossListRespMessage(role) ;
	}

}
