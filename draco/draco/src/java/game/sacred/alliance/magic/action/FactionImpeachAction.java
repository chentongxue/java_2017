//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1725_FactionImpeachReqMessage;
//import com.game.draco.message.response.C1725_FactionImpeachRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionImpeachAction extends BaseAction<C1725_FactionImpeachReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1725_FactionImpeachReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		C1725_FactionImpeachRespMessage resp = new C1725_FactionImpeachRespMessage();
//		Result result = GameContext.getFactionApp().impeach(role);
//		if(result.isIgnore()){
//			return null;
//		}
//		if(!result.isSuccess()){
//			resp.setType((byte) 0);
//			resp.setInfo(result.getInfo());
//			return resp;
//		}
//		resp.setType((byte) 1);
//		resp.setInfo(this.getText(TextId.SYSTEM_SUCCESS));
//		return resp;
//	}
//
//}
