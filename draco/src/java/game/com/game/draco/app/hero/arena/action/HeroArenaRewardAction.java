package com.game.draco.app.hero.arena.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.forward.logic.ClientLogicForwardType;
import com.game.draco.app.hero.arena.vo.HeroRewardResult;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1348_HeroArenaRewardReqMessage;
import com.game.draco.message.response.C1348_CommonRewardRespMessage;

public class HeroArenaRewardAction extends BaseAction<C1348_HeroArenaRewardReqMessage>{

	@Override
	public Message execute(ActionContext context, C1348_HeroArenaRewardReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			
			C1348_CommonRewardRespMessage respMsg = new C1348_CommonRewardRespMessage();
			
			HeroRewardResult result = GameContext.getHeroArenaApp().reward(role);
			respMsg.setSuccess(result.getResult());
			if(result.isSuccess()){
				respMsg.setAwardAttrList(result.getAwardAttrList());
				respMsg.setAwardGoodsList(result.getAwardGoodsList());
			}else{
				respMsg.setInfo(result.getInfo());
			}
			respMsg.setFunId(ClientLogicForwardType.HeroArenaReward.getType());
			return respMsg;
					
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Error));
		}
	}

}
