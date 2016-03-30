package com.game.draco.app.richman.vo.event;

import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.richman.config.RichManEvent;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.message.internal.C0083_RichManGetGoodsInternalMessage;
import com.game.draco.message.response.C2653_RichManRoleEventNoticeMessage;

public class RichManEventGetCard extends RichManEventLogic {
	private static RichManEventGetCard instance = new RichManEventGetCard();
	private RichManEventGetCard() {
		
	}
	
	public static RichManEventGetCard getInstance() {
		return instance;
	}

	@Override
	public void execute(MapRichManInstance mapInstance, 
			RoleInstance role, RichManRoleBehavior behavior) {
		RichManEvent event = behavior.getEvent();
		if(null == event) {
			return ;
		}
		int goodsId = GameContext.getRichManApp().getRandomCardId();
		if(goodsId <= 0) {
			return ;
		}
		//发送内部获得物品消息
		C0083_RichManGetGoodsInternalMessage reqMsg = new C0083_RichManGetGoodsInternalMessage();
		reqMsg.setRoleId(role.getIntRoleId());
		reqMsg.setGoodsId(goodsId);
		role.getBehavior().addEvent(reqMsg);
		//通知客户端
		C2653_RichManRoleEventNoticeMessage respMsg = getRoleEventNoticeMessage(behavior);
		role.getBehavior().sendMessage(respMsg);
	}

}
