//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1715_FactionDemiseReqMessage;
//import com.game.draco.message.response.C1715_FactionDemiseRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.core.exception.ServiceException;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionDemiseAction extends BaseAction<C1715_FactionDemiseReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1715_FactionDemiseReqMessage reqMsg) {
//		C1715_FactionDemiseRespMessage resp = new C1715_FactionDemiseRespMessage();
//		resp.setType((byte) 0);
//		try {
//			RoleInstance leader = this.getCurrentRole(context);
//			Result result = GameContext.getFactionApp().demisePresident(leader, reqMsg.getRoleId());
//			if(!result.isSuccess()){
//				resp.setInfo(result.getInfo());
//				return resp;
//			}
//			resp.setType((byte) 1);
//			resp.setInfo(this.getText(TextId.SYSTEM_SUCCESS));
//			return resp;
//		} catch (ServiceException e) {
//			this.logger.error("FactionDemiseAction", e);
//			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
//			return resp;
//		}
//	}
//
//}
