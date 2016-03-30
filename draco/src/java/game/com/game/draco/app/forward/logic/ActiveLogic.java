package com.game.draco.app.forward.logic;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.forward.config.ForwardConfig;
import com.game.draco.message.push.C0003_TipNotifyMessage;

public class ActiveLogic implements ForwardLogic{

	@Override
	public void forward(RoleInstance role, ForwardConfig config) {
		short activeId = Short.parseShort(config.getParameter()) ;
		Active active = GameContext.getActiveApp().getActive(activeId);
		if(null == active){
			return  ;
		}
		if(role.getLevel() < active.getMinLevel()){
			role.getBehavior().sendMessage(new C0003_TipNotifyMessage(
					GameContext.getI18n().messageFormat(TextId.FUNC_NOT_OPEN_BY_LESS_LEVEL,
							String.valueOf(active.getMinLevel())))) ;
			return ;
		}
		//标识为已经鉴定
		Message msg = GameContext.getActiveApp().obtainActiveDetail(role, activeId);
		if(null == msg){
			return ;
		}
		role.getBehavior().sendMessage(msg);
	}

	@Override
	public ForwardLogicType getForwardLogicType() {
		return ForwardLogicType.active;
	}

}
