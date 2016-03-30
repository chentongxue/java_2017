//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1707_FactionManageJoinReqMessage;
//import com.game.draco.message.response.C1707_FactionManageJoinRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.core.exception.ServiceException;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionManageJoinAction extends BaseAction<C1707_FactionManageJoinReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1707_FactionManageJoinReqMessage reqMsg) {
//		C1707_FactionManageJoinRespMessage resp = new C1707_FactionManageJoinRespMessage();
//		try {
//			RoleInstance role = this.getCurrentRole(context);
//			Result result = null;
//			if(1 == reqMsg.getType()){
//				result = GameContext.getFactionApp().acceptApplyJoin(role, reqMsg.getRoleId());
//			} else {
//				result = GameContext.getFactionApp().refuseApplyJoin(role, reqMsg.getRoleId());
//			}
//			if(!result.isSuccess()){
//				resp.setType((byte) 0);
//				resp.setInfo(result.getInfo());
//				return resp;
//			}
//			resp.setType((byte) 1);
//			resp.setInfo(this.getText(TextId.SYSTEM_SUCCESS));
//			return resp;
//		} catch (ServiceException e) {
//			this.logger.error("FactionManageJoinAction", e);
//			resp.setType((byte) 0);
//			resp.setInfo(this.getText(TextId.SYSTEM_ERROR));
//			return resp;
//		}
//	}
//
//}
