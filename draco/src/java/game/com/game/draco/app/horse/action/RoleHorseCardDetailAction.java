package com.game.draco.app.horse.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsHorse;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.message.item.GoodsBaseHorseItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2615_RoleHorseCardDetailReqMessage;
import com.game.draco.message.response.C2615_RoleHorseCardDetailRespMessage;

public class RoleHorseCardDetailAction extends BaseAction<C2615_RoleHorseCardDetailReqMessage> {

	@Override
	public Message execute(ActionContext context, C2615_RoleHorseCardDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		
		int horseId = reqMsg.getHorseId();
		HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horseId);
		if(null == horseBase) {
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Param_Error));
		}
		
		C2615_RoleHorseCardDetailRespMessage respMsg = new C2615_RoleHorseCardDetailRespMessage();
		
		GoodsHorse horse = GameContext.getGoodsApp().getGoodsTemplate(GoodsHorse.class, horseId);
		respMsg.setSource(reqMsg.getSource());
		respMsg.setBaseItem((GoodsBaseHorseItem)horse.getGoodsBaseInfo(null));
		return respMsg;
	}

}
