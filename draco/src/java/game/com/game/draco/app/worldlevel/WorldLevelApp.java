package com.game.draco.app.worldlevel;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface WorldLevelApp extends Service {
	
	/**
	 * 获取世界等级
	 * @return
	 */
	public int getWorldLevel();
	
	/**
	 * 获取经验的比率
	 * @param role
	 * @return 10000=100%
	 */
	public int getWorldLevelRatio(RoleInstance role);
	
	/**
	 * 获取世界等级说明
	 * @return
	 */
	public String getWorldLevelDesc();
	
	/**
	 * 计算世界等级
	 */
	public void calcWorldLevel();
	
	/**
	 * 同步比率变化
	 * @param role
	 */
	public void pushRatioChange(RoleInstance role);

}
