//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.push.C0003_TipNotifyMessage;
//import com.game.draco.message.request.C1746_FactionWarConfirmReqMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionWarConfirmAction extends BaseAction<C1746_FactionWarConfirmReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1746_FactionWarConfirmReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		Result result = GameContext.getFactionWarApp().enterFactionWar(role);
//		if(!result.isSuccess()){
//			return new C0003_TipNotifyMessage(result.getInfo());
//		}
//		return null;
//	}
//}