//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1701_FactionCreateReqMessage;
//import com.game.draco.message.response.C1701_FactionCreateRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.core.exception.ServiceException;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionCreateAction extends BaseAction<C1701_FactionCreateReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1701_FactionCreateReqMessage reqMsg) {
//		C1701_FactionCreateRespMessage resp = new C1701_FactionCreateRespMessage();
//		try {
//			RoleInstance role = this.getCurrentRole(context);
//			Result result = GameContext.getFactionApp().createFaction(role, reqMsg.getFactionName(), reqMsg.getFactionDesc());
//			if(!result.isSuccess()){
//				resp.setType((byte) 0);
//				resp.setInfo(result.getInfo());
//				return resp;
//			}
//			resp.setType((byte) 1);
//			resp.setInfo(this.getText(TextId.FACTION_CREATE_SUCCESS));
//			return resp;
//		} catch (ServiceException e) {
//			this.logger.error("FactionCreateAction", e);
//			resp.setInfo(this.getText(TextId.FACTION_CREATE_FAIL));
//			return resp;
//		}
//	}
//
//}
