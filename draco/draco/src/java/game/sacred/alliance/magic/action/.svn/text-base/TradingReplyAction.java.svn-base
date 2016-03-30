package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3902_TradingReplyReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TradingReplyAction extends BaseAction<C3902_TradingReplyReqMessage>{

	@Override
	public Message execute(ActionContext context, C3902_TradingReplyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		Result result = GameContext.getTradingApp().reply(role, reqMsg.getRoleId(),reqMsg.getType());
		if(result.isSuccess()){
			return null ;
		}
		return new C0002_ErrorRespMessage(reqMsg.getCommandId(),result.getInfo());
	}

}
