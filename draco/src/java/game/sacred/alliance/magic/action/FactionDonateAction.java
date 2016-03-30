//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1728_FactionDonateReqMessage;
//import com.game.draco.message.response.C1728_FactionDonateRespMessage;
//
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionDonateAction extends BaseAction<C1728_FactionDonateReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1728_FactionDonateReqMessage reqMsg) {
//		C1728_FactionDonateRespMessage resp = new C1728_FactionDonateRespMessage();
//		resp.setType((byte) 0);
//		try {
//			RoleInstance role = this.getCurrentRole(context);
//			resp = GameContext.getFactionFuncApp().factionDonate(reqMsg.getId(), role);
//			return resp;
//		} catch (Exception e) {
//			this.logger.error("FactionDemiseAction", e);
//			resp.setInfo(Status.Faction_FAILURE.getTips());
//			return resp;
//		}
//	}
//}
