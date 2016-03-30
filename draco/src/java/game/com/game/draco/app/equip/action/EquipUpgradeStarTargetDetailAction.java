package com.game.draco.app.equip.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0562_EquipUpgradeStarTargetDetailReqMessage;

public class EquipUpgradeStarTargetDetailAction extends BaseAction<C0562_EquipUpgradeStarTargetDetailReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C0562_EquipUpgradeStarTargetDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		return GameContext.getEquipApp().getNextStarEquipDetail(role, 
				reqMsg.getBagType(), reqMsg.getGoodsInstanceId(), reqMsg.getTargetId());
	}

}
