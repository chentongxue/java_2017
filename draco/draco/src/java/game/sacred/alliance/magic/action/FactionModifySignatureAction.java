//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1718_FactionModifySignatureReqMessage;
//import com.game.draco.message.response.C1718_FactionModifySignatureRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionModifySignatureAction extends BaseAction<C1718_FactionModifySignatureReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1718_FactionModifySignatureReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		Result result = GameContext.getFactionApp().modifySignature(role, reqMsg.getSignature());
//		C1718_FactionModifySignatureRespMessage resp = new C1718_FactionModifySignatureRespMessage();
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
