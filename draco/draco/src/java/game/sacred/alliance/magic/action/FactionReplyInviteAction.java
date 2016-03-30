//package sacred.alliance.magic.action;
//
//import java.text.MessageFormat;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.push.C0003_TipNotifyMessage;
//import com.game.draco.message.request.C1711_FactionReplyInviteReqMessage;
//import com.game.draco.message.response.C0002_ErrorRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.core.exception.ServiceException;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionReplyInviteAction extends BaseAction<C1711_FactionReplyInviteReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1711_FactionReplyInviteReqMessage reqMsg) {
//		try {
//			String factionId = reqMsg.getFactionId();
//			RoleInstance role = this.getCurrentRole(context);
//			if(Util.isEmpty(factionId) || null == role ){
//				return null;
//			}
//			role.setFactionBeInviteTime(0);
//			if(1 != reqMsg.getType()){
//				//拒绝
//				C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
//				String str = this.messageFormat(TextId.Faction_Invite_Refuse, role.getRoleName());
//				message.setMsgContext(str);
//				GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(reqMsg.getRoleId()), message);
//			    return null ;
//			}
//			Result result = GameContext.getFactionApp().acceptInvitation(role, reqMsg.getFactionId());
//			if(!result.isSuccess()){
//				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), result.getInfo());
//			}
//			return null;
//		} catch (ServiceException e) {
//			this.logger.error("FactionReplyInviteAction", e);
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.SYSTEM_ERROR));
//		}
//	}
//	
//}
