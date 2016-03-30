package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0570_ContainerExpandExecReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ContainerExpandExecAction extends BaseAction<C0570_ContainerExpandExecReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C0570_ContainerExpandExecReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		GameContext.getGoodsContainerApp().expandExec(role,reqMsg.getInfo());
		return null;
	}

}
