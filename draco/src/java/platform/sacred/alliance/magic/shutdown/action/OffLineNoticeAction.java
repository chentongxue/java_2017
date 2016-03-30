package sacred.alliance.magic.shutdown.action;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.debug.message.request.C10070_ShutdownOffLineNoticeReqMessage;
import com.game.draco.debug.message.response.C10070_ShutdownOffLineNoticeRespMessage;
import com.game.draco.message.response.C0107_UserLogoutRespMessage;


public class OffLineNoticeAction extends
		ActionSupport<C10070_ShutdownOffLineNoticeReqMessage> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Message execute(ActionContext context,
			C10070_ShutdownOffLineNoticeReqMessage reqMsg) {
		C10070_ShutdownOffLineNoticeRespMessage respMsg = new C10070_ShutdownOffLineNoticeRespMessage();
		respMsg.setType((byte) 0);
		int minutes = reqMsg.getMinutes();
		try {
			if (minutes == 1) {
				Collection<RoleInstance> roles = GameContext.getOnlineCenter()
						.getAllOnlineRole();
				for (RoleInstance role : roles) {
					C0107_UserLogoutRespMessage resp = new C0107_UserLogoutRespMessage();
					resp.setType((byte) RespTypeStatus.KICK_ROLE);
					role.getBehavior().sendMessage(resp);
					// GameContext.getOnlineCenter().offline(role);
					role.getBehavior().closeNetLink();
				}
				respMsg.setType((byte) 1);
				return respMsg;
			}
			String content = "";
			if (1 == minutes - 1) {
				content = GameContext.getI18n().messageFormat(
						TextId.SHUT_DOWN_REJECT_TIPS,
						String.valueOf(minutes - 1));
			} else {
				content = GameContext.getI18n().messageFormat(
						TextId.SHUT_DOWN_TIPS, String.valueOf(minutes - 1));
			}
			Result result = GameContext.getChatApp().sendSysMessage(
					ChatSysName.System, ChannelType.World, content, null, null);
			respMsg.setType(result.getResult());
		} catch (Exception e) {
			logger.error("", e);
			respMsg.setType((byte) 0);
		}
		return respMsg;
	}

}
