//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1708_FactionRemoveMemberReqMessage;
//import com.game.draco.message.response.C1708_FactionRemoveMemberRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.core.exception.ServiceException;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionRemoveMemberAction extends BaseAction<C1708_FactionRemoveMemberReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1708_FactionRemoveMemberReqMessage reqMsg) {
//		C1708_FactionRemoveMemberRespMessage resp = new C1708_FactionRemoveMemberRespMessage();
//		resp.setType((byte) 0);
//		try {
//			RoleInstance role = this.getCurrentRole(context);
//			Result result = GameContext.getFactionApp().removeFactionRole(role, reqMsg.getRoleId());
//			if(!result.isSuccess()){
//				resp.setInfo(result.getInfo());
//				return resp;
//			}
//			resp.setType((byte) 1);
//			resp.setInfo(this.getText(TextId.SYSTEM_SUCCESS));
//			return resp;
//		} catch (ServiceException e) {
//			this.logger.error("FactionRemoveMemberAction", e);
//			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
//			return resp;
//		}
//	}
//
//}
