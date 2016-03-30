package sacred.alliance.magic.action;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2301_ActivePanelDetailReqMessage;

public class ActivePanelDetailAction extends BaseAction<C2301_ActivePanelDetailReqMessage> {

	@Override
	public Message execute(ActionContext context, C2301_ActivePanelDetailReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		short activeId = req.getActiveId();
		if(null==role || activeId==0){
			return null;
		}
		return GameContext.getActiveApp().obtainActiveDetail(role, activeId);
	}
	
}
