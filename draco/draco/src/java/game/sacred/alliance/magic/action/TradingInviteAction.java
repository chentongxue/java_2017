package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3900_TradingInviteReqMessage;
import com.game.draco.message.response.C3900_TradingInviteRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TradingInviteAction extends BaseAction<C3900_TradingInviteReqMessage>{

	@Override
	public Message execute(ActionContext context, C3900_TradingInviteReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C3900_TradingInviteRespMessage respMsg = new C3900_TradingInviteRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE);
		Result result = GameContext.getTradingApp().invite(role, reqMsg.getToRoleId());
		if(!result.isSuccess()){
			respMsg.setInfo(result.getInfo());
			return respMsg ;
		}
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg;
	}

}
