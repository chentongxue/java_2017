package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3907_TradingCancelReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TradingCancelAction extends BaseAction<C3907_TradingCancelReqMessage>{

	@Override
	public Message execute(ActionContext context, C3907_TradingCancelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		Result result = GameContext.getTradingApp().cancel(role);
		if(result.isSuccess()){
			return null ;
		}
		return new C0002_ErrorRespMessage(reqMsg.getCommandId(),result.getInfo());
	}

}
