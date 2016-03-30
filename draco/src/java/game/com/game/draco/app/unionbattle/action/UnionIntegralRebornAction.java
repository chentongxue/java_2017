package com.game.draco.app.unionbattle.action;

import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.role.RebornMode;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.unionbattle.config.UnionIntegralReborn;
import com.game.draco.message.request.C2544_UnionIntegralRebornMessage;
import com.game.draco.message.response.C2001_RoleRebornRespMessage;

/**
 * 复活
 */
public class UnionIntegralRebornAction extends BaseAction<C2544_UnionIntegralRebornMessage> {

	@Override
	public Message execute(ActionContext context, C2544_UnionIntegralRebornMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		try {
			if(!role.isDeath()){
				return null;
			}
			Map<Byte,UnionIntegralReborn> rebornMap = GameContext.getUnionIntegralBattleDataApp().getIntegralRebornMap();
			if(!rebornMap.containsKey(req.getId())){
				return null;
			}
			
			UnionIntegralReborn reborn = rebornMap.get(req.getId());
			
			Point point = new Point(role.getMapId(),reborn.getMapX(),reborn.getMapY());
			
			RebornMode mode = new RebornMode();
			mode.setHpRate(100);
			GameContext.getRoleRebornApp().reborn(role, mode, point);
			C2001_RoleRebornRespMessage resp = new C2001_RoleRebornRespMessage();
			resp.setType(Result.SUCCESS);
			role.getBehavior().sendMessage(resp);
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}

}
