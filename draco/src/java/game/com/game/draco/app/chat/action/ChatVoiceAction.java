package com.game.draco.app.chat.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.message.request.C1803_ChatVoiceReqMessage;
import com.game.draco.message.response.C1803_ChatVoiceRespMessage;

public class ChatVoiceAction extends BaseAction<C1803_ChatVoiceReqMessage>{

	@Override
	public Message execute(ActionContext context, C1803_ChatVoiceReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C1803_ChatVoiceRespMessage respMsg = new C1803_ChatVoiceRespMessage();
		if(!GameContext.getParasConfig().isOpenChatVoice()){
			respMsg.setInfo(this.getText(TextId.CHAT_VOICE_NOT_OPEN));
			return respMsg ;
		}
		ChannelType channelType = ChannelType.getChannelType(reqMsg.getType());
		if(null == channelType){
			return null;
		}
		boolean canDo = channelType.isCanVoice() ;
		if(!canDo){
			MapInstance map = role.getMapInstance() ;
			if(null != map && map.getMap().getMapConfig().getVoiceChannelType()
					== channelType.getType()){
				canDo = true ;
			}
		}
		if(!canDo){
			respMsg.setInfo(this.getText(TextId.MAP_CANOT_DO_THIS_THING));
			return respMsg ;
		}
		RoleInstance targRole;
		String targetId = reqMsg.getTargetRoleId();
		if(1 == reqMsg.getTargetIdFlag()){
			targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleName(targetId);
		} else {
			targRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(targetId);
		}
		Result result = GameContext.getChatApp().sendVoiceMessage(role, channelType, reqMsg.getData(), targRole);
		if(result.isIgnore()){
			return null;
		}
		respMsg.setStatus(result.getResult());
		respMsg.setInfo(result.getInfo());
		return respMsg ;
	}

}

