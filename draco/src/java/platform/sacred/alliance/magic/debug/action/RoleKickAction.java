package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10013_RoleKickReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;
import com.game.draco.message.response.C0107_UserLogoutRespMessage;

import sacred.alliance.magic.channel.mina.MinaChannelSession;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleKickAction extends ActionSupport<C10013_RoleKickReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10013_RoleKickReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try {
			String[] infos = reqMsg.getInfos();
			C0107_UserLogoutRespMessage logoutResp = new C0107_UserLogoutRespMessage();
			logoutResp.setType((byte)RespTypeStatus.KICK_ROLE);
			List<String> failedList = new ArrayList<String>();
			int type = reqMsg.getInfoType();
			for(String info : infos){
				if(Util.isEmpty(info)){
					continue;
				}
				RoleInstance role = null;
				try {
					if(type == 0){
						role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(info);
					}else if(type == 1){
						role = GameContext.getOnlineCenter().getRoleInstanceByRoleName(info);
					}
				} catch (RuntimeException e) {
					this.logger.error("", e);
				}
				if(role == null){
					failedList.add(info);
					continue;
				}
				try {
					role.getBehavior().sendMessage(logoutResp);
				} catch (Exception ex) {
				}
				role.getBehavior().closeNetLink();
			}
			if(failedList.size() > 0){
				String info = GameContext.getI18n().getText(TextId.ROLE_KICK_ROLE_TIP);
				String cat = "";
				for(String item : failedList){
					info += cat + item;
					cat = ",";
				}
				resp.setInfo(info);
				return resp;
			}
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		} catch (RuntimeException e) {
			this.logger.error("RoleKickAction error: ", e);
			return resp;
		}
	}

}
