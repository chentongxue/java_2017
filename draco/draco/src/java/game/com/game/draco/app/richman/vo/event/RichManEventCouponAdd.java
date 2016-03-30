package com.game.draco.app.richman.vo.event;

import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.richman.domain.RoleRichMan;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.message.response.C2653_RichManRoleEventNoticeMessage;

public class RichManEventCouponAdd extends RichManEventLogic {
	private static RichManEventCouponAdd instance = new RichManEventCouponAdd();
	private RichManEventCouponAdd() {
		
	}
	
	public static RichManEventCouponAdd getInstance() {
		return instance;
	}

	@Override
	public void execute(MapRichManInstance mapInstance, RoleInstance role,
			RichManRoleBehavior behavior) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(role.getIntRoleId());
		if(null == rrm) {
			return ;
		}
		int eventValue = (int)(behavior.getEvent().getEventValue());
		
		if(eventValue == 0) {
			return ;
		}
		//修改点券值
		GameContext.getRichManApp().changeRoleToadyCoupon(role, eventValue);
	}

}
