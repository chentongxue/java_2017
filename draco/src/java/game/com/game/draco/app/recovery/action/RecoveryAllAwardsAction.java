package com.game.draco.app.recovery.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.recovery.vo.RecoveryResult;
import com.game.draco.message.request.C1927_RecoveryAllReqMessage;
import com.game.draco.message.response.C1927_RecoveryAllRespMessage;
/**
 * "一键追回"昨天未得到的“所有”奖励 
 */
public class RecoveryAllAwardsAction  extends BaseAction<C1927_RecoveryAllReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1927_RecoveryAllReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		C1927_RecoveryAllRespMessage msg = new C1927_RecoveryAllRespMessage();
		if(Util.isEmpty(req.getParam())){
			msg.setInfo(GameContext.getI18n().getText(TextId.RECOVERY_CONSUME_PARAM_ERROR));
			msg.setType(Result.FAIL);
			return msg;
		}
		String [] param = req.getParam().split(",");
		byte confirm = 0;
		if(param.length>1){
			confirm = 1;
		}
		byte recoveryType;
		try{
			recoveryType = Byte.parseByte(param[0]);
		}catch (Exception e) {
			msg.setInfo(GameContext.getI18n().getText(TextId.RECOVERY_CONSUME_PARAM_ERROR));
			msg.setType(Result.FAIL);
			return msg;
		}

		RecoveryResult rt = GameContext.getRecoveryApp().recoveryAllAwards(role, recoveryType, confirm);
		if(rt.isIgnore()){
			return null;
		}
		msg.setType(rt.getResult());
		msg.setInfo(rt.getInfo());
		return msg;
	}

	
}
