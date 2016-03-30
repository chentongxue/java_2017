package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2002_RoleRebornConfirmReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C2001_RoleRebornRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleRebornConfirmAction extends BaseAction<C2002_RoleRebornConfirmReqMessage> {

	@Override
	public Message execute(ActionContext context, C2002_RoleRebornConfirmReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		String param = reqMsg.getParam() ;
		Result result = GameContext.getRoleRebornApp().rebornConfirm(role, param);
		if(result.isIgnore()){
			return null;
		}
		if(!result.isSuccess()){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),result.getInfo());
		}
		C2001_RoleRebornRespMessage resp = new C2001_RoleRebornRespMessage();
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}
}
