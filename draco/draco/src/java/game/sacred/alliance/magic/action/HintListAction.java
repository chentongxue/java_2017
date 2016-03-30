package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1170_HintListReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class HintListAction extends BaseAction<C1170_HintListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1170_HintListReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		GameContext.getHintApp().pushHintListMessage(role);
		return null;
	}
	
}
