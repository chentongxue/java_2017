package com.game.draco.app.unionbattle.action;

import java.util.Collection;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.type.UnionPowerType;
import com.game.draco.app.unionbattle.config.UnionIntegralNpc;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2547_UnionIntegralSetTargetMessage;
import com.game.draco.message.response.C2547_UnionIntegralSetTargetRespMessage;

/**
 * 设置指挥官目标
 */
public class UnionIntegralSetTargetAction extends BaseAction<C2547_UnionIntegralSetTargetMessage> {

	@Override
	public Message execute(ActionContext context, C2547_UnionIntegralSetTargetMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		try {
			
			if(!role.hasUnion()){
				return new C0003_TipNotifyMessage((GameContext.getI18n().getText(TextId.UNION_NOT)));
			}
			
			if(!GameContext.getUnionApp().getPowerTypeSet(role).contains(UnionPowerType.UnionSetTarget)){
				return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_POSITION_NOT_HAS));
			}
			
			C2547_UnionIntegralSetTargetRespMessage respMsg = new C2547_UnionIntegralSetTargetRespMessage();
			
			UnionIntegralNpc integralNpc = GameContext.getUnionIntegralBattleDataApp().getIntegralNpc(req.getGrid());
			
			GameContext.getUnionIntegralBattleApp().modifyTargetMap(role.getUnionId(),integralNpc.getBossId());
			Collection<RoleInstance> roleList = role.getMapInstance().getRoleList();
			respMsg.setGrid(req.getGrid());
			for(RoleInstance r : roleList){
				r.getBehavior().sendMessage(respMsg);
			}
			
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}

}
