package com.game.draco.app.hero.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C1256_HeroEquipOpenReqMessage;
import com.game.draco.message.response.C1256_HeroEquipOpenRespMessage;

public class HeroEquipOpenAction extends BaseAction<C1256_HeroEquipOpenReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1256_HeroEquipOpenReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		byte pos = reqMsg.getPos() ;
		Result result = GameContext.getHeroApp().openEquipPos(role, pos);
		if(result.isIgnore()){
			//快速购买
			return null ;
		}
		C1256_HeroEquipOpenRespMessage respMsg = new C1256_HeroEquipOpenRespMessage();
		respMsg.setInfo(result.getInfo());
		respMsg.setPos(pos);
		respMsg.setStatus(result.isSuccess()?RespTypeStatus.SUCCESS:RespTypeStatus.FAILURE);
		return respMsg ;
	}

}
