//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.push.C0003_TipNotifyMessage;
//import com.game.draco.message.request.C1743_FactionWarEnterReqMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionWarEnterAction extends BaseAction<C1743_FactionWarEnterReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1743_FactionWarEnterReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		if(null == role){
//			return null;
//		}
//		Result result =	GameContext.getFactionWarApp().enterFactionWar(role);
//		if(!result.isSuccess()){
//			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage(result.getInfo());
//			role.getBehavior().sendMessage(message);
//		}
//		return null;
//	}
//}
