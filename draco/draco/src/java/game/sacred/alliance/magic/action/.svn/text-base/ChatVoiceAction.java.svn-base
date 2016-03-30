package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1806_ChatVoiceReqMessage;
import com.game.draco.message.response.C1806_ChatVoiceRespMessage;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class ChatVoiceAction extends BaseAction<C1806_ChatVoiceReqMessage>{

	@Override
	public Message execute(ActionContext context, C1806_ChatVoiceReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C1806_ChatVoiceRespMessage respMsg = new C1806_ChatVoiceRespMessage();
		if(!GameContext.getParasConfig().isOpenChatVoice()){
			respMsg.setInfo(this.getText(TextId.CHAT_VOICE_NOT_OPEN));
			return respMsg ;
		}
		RoleInstance targRole;
		String targetId = reqMsg.getTargetRoleId();
		if(1 == reqMsg.getTargetIdFlag()){
			targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleName(targetId);
		} else {
			targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(targetId);
		}
		Result result = GameContext.getChatApp().sendVoiceMessage(role, ChannelType.getChannelType(reqMsg.getType()), reqMsg.getData(), targRole);
		if(result.isIgnore()){
			return null;
		}
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		return respMsg ;
	}

}
