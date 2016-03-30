//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1741_FactionWarReqMessage;
//import com.game.draco.message.response.C1741_FactionWarRespMessage;
//
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionWarAction extends BaseAction<C1741_FactionWarReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1741_FactionWarReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		if(null == role){
//			return null;
//		}
//		C1741_FactionWarRespMessage respMsg = GameContext.getFactionWarApp().getFactionWarRespMessage(role);
//		return respMsg;
//	}
//}
