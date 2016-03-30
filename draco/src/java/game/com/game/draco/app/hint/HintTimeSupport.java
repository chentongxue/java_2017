package com.game.draco.app.hint;

import java.util.List;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.HintTimeNotifyItem;

public interface HintTimeSupport {
	
	/**
	 * 获取可领取提示特效（同一个APP中可能有多个）
	 * @param role
	 * @return
	 */
	public List<HintTimeNotifyItem> getHintTimeNotifyList(RoleInstance role);
	
}
