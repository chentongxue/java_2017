//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1709_FactionExitReqMessage;
//import com.game.draco.message.response.C1709_FactionExitRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.core.exception.ServiceException;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionExitAction extends BaseAction<C1709_FactionExitReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1709_FactionExitReqMessage reqMsg) {
//		C1709_FactionExitRespMessage resp = new C1709_FactionExitRespMessage();
//		resp.setType((byte) 0);
//		try {
//			RoleInstance role = this.getCurrentRole(context);
//			Result result = GameContext.getFactionApp().exitFaction(role);
//			if(!result.isSuccess()){
//				resp.setInfo(result.getInfo());
//				return resp;
//			}
//			resp.setType((byte) 1);
//			return resp;
//		} catch (ServiceException e) {
//			this.logger.error("FactionExitAction", e);
//			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
//			return resp;
//		}
//	}
//
//}
