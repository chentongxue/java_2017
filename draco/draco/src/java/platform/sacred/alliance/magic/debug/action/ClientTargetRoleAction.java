package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10036_ClientTargetRoleReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

public class ClientTargetRoleAction extends ActionSupport<C10036_ClientTargetRoleReqMessage>{

	@Override
	public Message execute(ActionContext arg0, C10036_ClientTargetRoleReqMessage req) {
		String roleInfo = req.getRoleInfo();
		RoleInstance role = null;
		if(1 == req.getType()){
			role = GameContext.getOnlineCenter().getRoleInstanceByRoleName(roleInfo);
		}else{
			role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleInfo);
		}
		Result result = GameContext.getClientTargetApp().collectRole(role);
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
