package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10027_ClientTargetReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class ClientTargetAction extends ActionSupport<C10027_ClientTargetReqMessage>{

	@Override
	public Message execute(ActionContext arg0, C10027_ClientTargetReqMessage req) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		Result result = GameContext.getClientTargetApp().gmCollect(req.getRoleSize());
		resp.setType(result.getResult());
		resp.setInfo(result.getInfo());
		return resp;
	}

}
