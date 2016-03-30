package com.game.draco.app.union.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0086_UnionAddMemberMessage;
import com.game.draco.message.push.C0003_TipNotifyMessage;

/**
 * 添加申请成员
 * @author mofun030602
 *
 */
public class UnionAddMemberAction extends BaseAction<C0086_UnionAddMemberMessage> {

	@Override
	public Message execute(ActionContext context,C0086_UnionAddMemberMessage reqMsg) {
		Integer roleId = reqMsg.getRoleId();
		Integer operaRoleId = reqMsg.getOperaRoleId();
		
		RoleInstance operRole =  GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(operaRoleId));
		
		boolean isInActive = GameContext.getUnionIntegralBattleApp().inIntegtalActive(operRole.getUnionId());
		if(isInActive){
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_IN_ACTIVE_ERR));
		}
		
		try{
			Result result = GameContext.getUnionApp().joinUnion(roleId,operRole);
			if(!result.isSuccess()) {
				sendMsg(String.valueOf(operaRoleId), result.getInfo());
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private void sendMsg(String roleId,String msg){
		//在线则发浮动提示
		GameContext.getMessageCenter().sendByRoleId(null, roleId, new C0003_TipNotifyMessage(msg));
	}
}
