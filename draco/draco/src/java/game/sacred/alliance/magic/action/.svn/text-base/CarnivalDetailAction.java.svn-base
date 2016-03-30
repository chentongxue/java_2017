package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1187_CarnivalDetailReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class CarnivalDetailAction extends BaseAction<C1187_CarnivalDetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C1187_CarnivalDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		
		return GameContext.getCarnivalApp().getActiveCarnivalDetail(reqMsg.getItemId(), 
				role.getCareer());
	}
}
