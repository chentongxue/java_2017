package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.config.UnionActivityInfo;
import com.game.draco.app.union.type.UnionActivityType;
import com.game.draco.app.union.type.UnionFunctionType;
import com.game.draco.message.request.C2764_UnionActivityFunctionReqMessage;

/**
 * 公会活动功能
 * @author zhb
 *
 */
public class UnionActivityFunctionAction extends BaseAction<C2764_UnionActivityFunctionReqMessage> {

	@Override
	public Message execute(ActionContext context, C2764_UnionActivityFunctionReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		UnionActivityInfo activeInfo = GameContext.getUnionDataApp().getUnionActivityMap().get(reqMsg.getActivityId());
		if(activeInfo == null){
			return null;
		}
		
		if(Util.isEmpty(activeInfo.getParam())){
			return null;
		}
		
		if(activeInfo.getFunType() == UnionActivityType.FUNCION_TYPE.getType() 
				|| activeInfo.getFunType() == UnionActivityType.FUNCION_TYPE_MSG.getType()){
			
			UnionFunctionType type = UnionFunctionType.get(Byte.parseByte(activeInfo.getParam()));
			
			switch(type){
				case BUFF_TYPE:
					GameContext.getUnionApp().joinUnionBuff(role);
					break;
				case TERRITORY_TYPE:
					GameContext.getUnionApp().joinUnionTerritory(role);
					break;	
				case SUMMON_TYPE:
					GameContext.getUnionApp().joinUnionSummon(role);
					break;	
				case SHOP:
					GameContext.getUnionApp().openUnionShop(role);
					break;	
				default : 
					break;
				
			}
		}
		
		return null;
	}

}
