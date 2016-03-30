package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2110_QuickBuyReqMessage;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class QuickBuyAction extends BaseAction<C2110_QuickBuyReqMessage> {

	@Override
	public Message execute(ActionContext context, C2110_QuickBuyReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		//上次触发快速购买的请求消息
		Message message = role.getLastTrigQuickBuyReqMessage();
		String parameter = req.getParameter();
		//快速购买请求的命令参数
		short cmandId = GameContext.getQuickBuyApp().getCommandIdByParameter(parameter);
		if(null == message || cmandId != message.getCommandId()){
			return new C0003_TipNotifyMessage(Status.Quest_Param_Error_Oprate_Fail.getTips());
		}
		//标记为是确认购买
		role.setQuickBuyconfirm(true);
		//将快速购买需要的message放入执行器中,必须可重复(排队执行)
		role.getBehavior().addCumulateEvent(message);
		return null;
	}
	
}
