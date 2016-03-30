//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1717_FactionModifyDescReqMessage;
//import com.game.draco.message.response.C1717_FactionModifyDescRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionModifyDescAction extends BaseAction<C1717_FactionModifyDescReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1717_FactionModifyDescReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		Result result = GameContext.getFactionApp().modifyFactionDesc(role, reqMsg.getFactionDesc());
//		C1717_FactionModifyDescRespMessage resp = new C1717_FactionModifyDescRespMessage();
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
//
//}
