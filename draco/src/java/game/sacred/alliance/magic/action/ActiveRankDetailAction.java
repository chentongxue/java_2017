//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C2319_ActiveRankDetailReqMessage;
//
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class ActiveRankDetailAction extends BaseAction<C2319_ActiveRankDetailReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C2319_ActiveRankDetailReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		if(null == role){
//			return null;
//		}
//		return GameContext.getActiveRankApp().createRankDetailMsg(role, reqMsg.getActiveId());
//	}
//
//}
