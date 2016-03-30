package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C1804_ChatVoicePushMessage;
import com.game.draco.message.request.C1803_ChatVoiceOldReqMessage;
import com.game.draco.message.response.C1803_ChatVoiceOldRespMessage;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ChatVoiceOldAction extends BaseAction<C1803_ChatVoiceOldReqMessage>{

	@Override
	public Message execute(ActionContext context, C1803_ChatVoiceOldReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C1803_ChatVoiceOldRespMessage respMsg = new C1803_ChatVoiceOldRespMessage();
		if(!GameContext.getParasConfig().isOpenChatVoice()){
			respMsg.setInfo(this.getText(TextId.CHAT_VOICE_NOT_OPEN));
			return respMsg ;
		}
		Result result = GameContext.getChatApp().canSpeakByForbid(role, ChannelType.Private);
		if(!result.isSuccess()){
			respMsg.setInfo(Status.Chat_Forbid_Part.getTips());
			return respMsg;
		}
		byte[] data = reqMsg.getData() ;
		if(null == data || data.length == 0){
			respMsg.setInfo(this.getText(TextId.ERROR_INPUT));
			return respMsg ;
		}
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(
				String.valueOf(reqMsg.getTargetRoleId()));
		if(null == targetRole){
			respMsg.setInfo(this.getText(TextId.ROLE_NOT_ONLINE));
			return respMsg ;
		}
		//转发给目标
		C1804_ChatVoicePushMessage routeMsg = new C1804_ChatVoicePushMessage();
		routeMsg.setData(data);
		routeMsg.setSendRoleId(role.getIntRoleId());
		routeMsg.setSendRoleName(role.getRoleName());
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		routeMsg.setVipLv(vipLevel);
		routeMsg.setCareer(role.getCareer());
		routeMsg.setSex(role.getSex());
		routeMsg.setRoleLevel((byte)role.getLevel());
		routeMsg.setCampId(role.getCampId());
		//转发
		GameContext.getMessageCenter().send("", targetRole.getUserId(), routeMsg);
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg ;
	}
	
}
