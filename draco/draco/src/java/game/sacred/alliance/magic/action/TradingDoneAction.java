package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3906_TradingDoneReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TradingDoneAction extends BaseAction<C3906_TradingDoneReqMessage>{

	@Override
	public Message execute(ActionContext context, C3906_TradingDoneReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		Result result = GameContext.getTradingApp().trading(role);
		if(result.isSuccess()){
			return null ;
		}
		return new C0002_ErrorRespMessage(reqMsg.getCommandId(),result.getInfo());
	}

}
