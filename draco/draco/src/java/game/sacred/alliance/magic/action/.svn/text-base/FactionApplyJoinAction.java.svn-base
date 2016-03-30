//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.push.C0003_TipNotifyMessage;
//import com.game.draco.message.request.C1703_FactionApplyJoinReqMessage;
//import com.game.draco.message.response.C0002_ErrorRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionApplyJoinAction extends BaseAction<C1703_FactionApplyJoinReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1703_FactionApplyJoinReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		Result result = GameContext.getFactionApp().applyJoinFaction(role, reqMsg.getFactionId());
//		if(result.isSuccess()){
//			return new C0003_TipNotifyMessage(this.getText(TextId.FACTION_APPLY_JOIN_SEND_SUCCESS));
//		}
//		return new C0002_ErrorRespMessage(reqMsg.getCommandId(), result.getInfo());
//	}
//
//}
