package com.game.draco.app.giftcode.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2400_GiftCodeVerificationReqMessage;
import com.game.draco.message.response.C2400_GiftCodeVerificationRespMessage;

public class GiftCodeVerificationAction extends BaseAction<C2400_GiftCodeVerificationReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C2400_GiftCodeVerificationReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C2400_GiftCodeVerificationRespMessage respMsg = new C2400_GiftCodeVerificationRespMessage();
		respMsg.setStatus(RespTypeStatus.FAILURE) ;
		
		String codeNumber = req.getCodeNumber();
		Result result = GameContext.getGiftCodeApp().takeCdkey(role,codeNumber);
		if(!result.isSuccess()){
			respMsg.setInfo(result.getInfo());
			return respMsg;
		}
		respMsg.setInfo(Status.Sys_Act_Code_Success_Mail_Reward.getTips());
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg;
	}
	
}
