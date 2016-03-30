package com.game.draco.action.internal;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0074_FactionWarehouseInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class FactionWarehouseInternalAction extends BaseAction<C0074_FactionWarehouseInternalMessage> {

	@Override
	public Message execute(ActionContext context,C0074_FactionWarehouseInternalMessage reqMsg) {
		String roleId = reqMsg.getRoleId();
		try{
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
			if(null == role){
				return null;
			}
			role.getWarehousePack().expansionWarehouse(reqMsg.getCapacity());
		}catch (Exception e) {
			logger.error("FactionWarehouseInternalAction error:", e);
		}
		return null;
	}
}
