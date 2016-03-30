package com.game.draco.app.richman.vo.event;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.richman.domain.RoleRichMan;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.message.push.C0003_TipNotifyMessage;

public class RichManEventCouponMul extends RichManEventLogic {
	private static RichManEventCouponMul instance = new RichManEventCouponMul();
	private RichManEventCouponMul() {
		
	}
	
	public static RichManEventCouponMul getInstance() {
		return instance;
	}
	
	@Override
	public void execute(MapRichManInstance mapInstance, 
			RoleInstance role, RichManRoleBehavior behavior) {
		RoleRichMan rrm = GameContext.getUserRichManApp().getRoleRichMan(role.getIntRoleId());
		if(null == rrm) {
			return ;
		}
		float factor = behavior.getEvent().getEventValue();
		int curValue = role.getTodayCoupon();
		int newValue = (int)(factor * curValue);
		if(newValue == 0) {
			return ;
		}
		//提示
		String textId = factor > 1 ? TextId.Richman_event_coupon_mul : TextId.Richman_event_coupon_div;
		String info = GameContext.getI18n().messageFormat(GameContext.getI18n().getText(textId), String.valueOf(factor));
		C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
		tipMsg.setMsgContext(info);
		role.getBehavior().sendMessage(tipMsg);
		//修改玩家点券值
		GameContext.getRichManApp().changeRoleToadyCoupon(role, newValue - curValue);
	}
	
}
