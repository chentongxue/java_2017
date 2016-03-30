//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1724_FactionModifyNameReqMessage;
//import com.game.draco.message.response.C1724_FactionModifyNameRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionModifyNameAction extends BaseAction<C1724_FactionModifyNameReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1724_FactionModifyNameReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		Result result = GameContext.getFactionApp().modifyFactionName(role, reqMsg.getFactionName());
//		C1724_FactionModifyNameRespMessage resp = new C1724_FactionModifyNameRespMessage();
//		if(!result.isSuccess()){
//			resp.setType((byte) 0);
//			resp.setInfo(result.getInfo());
//			return resp;
//		}
//		resp.setType((byte) 1);
//		resp.setInfo(this.getText(TextId.SYSTEM_SUCCESS));
//		resp.setFactionName(reqMsg.getFactionName().trim());
//		return resp;
//	}
//
//
//}
