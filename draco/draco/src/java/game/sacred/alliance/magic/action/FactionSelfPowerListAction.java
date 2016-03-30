//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1721_FactionSelfPowerListReqMessage;
//import com.game.draco.message.response.C1721_FactionSelfPowerListRespMessage;
//
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionSelfPowerListAction extends BaseAction<C1721_FactionSelfPowerListReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1721_FactionSelfPowerListReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		C1721_FactionSelfPowerListRespMessage resp = new C1721_FactionSelfPowerListRespMessage();
//		int power = GameContext.getFactionApp().getFactionPosition(role);
//		resp.setPower(power);
//		return resp;
//	}
//}
