package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.config.UnionActivityInfo;
import com.game.draco.app.union.type.UnionActivityType;
import com.game.draco.message.request.C2763_UnionActivityForwordReqMessage;

/**
 * 公会活动跳转
 * @author zhb
 *
 */
public class UnionActivityForwordAction extends BaseAction<C2763_UnionActivityForwordReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C2763_UnionActivityForwordReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		UnionActivityInfo activeInfo = GameContext.getUnionDataApp().getUnionActivityMap().get(reqMsg.getActivityId());
		if(activeInfo == null){
			return null;
		}
		
		if(activeInfo.getFunType() != UnionActivityType.FORWORD_TYPE.getType()){
			return null;
		}
		
		Message msg = GameContext.getActiveApp().obtainActiveDetail(role,Short.parseShort(activeInfo.getParam()));
		return msg;
		
	}

}
