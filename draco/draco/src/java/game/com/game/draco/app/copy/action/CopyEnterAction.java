package com.game.draco.app.copy.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0209_CopyEnterReqMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class CopyEnterAction extends BaseAction<C0209_CopyEnterReqMessage>{

	@Override
	public Message execute(ActionContext context, C0209_CopyEnterReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		//设置玩家进入点
		//更换地图方法统一设置
		//role.setCopyBeforePoint(role.getMapId(), role.getMapX(), role.getMapY());
		//是否已在自由组队匹配队列中
		boolean inApply = GameContext.getCopyTeamApp().inApplyStatus(role);
		if(inApply){
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
			msg.setMsgContext(Status.Copy_Not_Enter_In_Auto_Team.getTips());
			return msg;
		}
		//判断是否所处可进入的地图列表中
		/*boolean isAllowedMap = GameContext.getCopyTeamApp().isAllowedMap(role.getMapId());
		if(!isAllowedMap){
			TipNotifyMessage msg = new TipNotifyMessage();
			msg.setMsgContext("您所在的地图无法进入副本");
			return msg;
		}*/
		GameContext.getCopyLogicApp().enterCopy(role, Short.valueOf(reqMsg.getCopyId()));
		return null;
	}
	
}
