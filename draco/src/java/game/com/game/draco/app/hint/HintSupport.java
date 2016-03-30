package com.game.draco.app.hint;

import java.util.Set;

import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.hint.vo.HintType;

public interface HintSupport {
	
	/**
	 * 获取可领取提示特效（同一个APP中可能有多个）
	 * @param role
	 * @return
	 */
	public Set<HintType> getHintTypeSet(RoleInstance role);
	
}
