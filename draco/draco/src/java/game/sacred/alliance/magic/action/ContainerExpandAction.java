package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0569_ContainerExpandReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ContainerExpandAction extends BaseAction<C0569_ContainerExpandReqMessage>{

	@Override
	public Message execute(ActionContext context, C0569_ContainerExpandReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		GameContext.getGoodsContainerApp().expand(role);
		return null;
	}

}
