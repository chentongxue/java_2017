package com.game.draco.app.npc.refreshrule;

import java.util.Date;
import java.util.Set;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.MapInstance;

public interface RefreshRuleApp extends Service {

	/**
	 * 刷怪
	 * @param ruleId
	 * @param index
	 * @param startTime
	 * @param mapInstance
	 * @return
	 */
	public int refresh(int ruleId, int index, Date startTime, MapInstance mapInstance, boolean isQuest);
	
	/**
	 * 获取最大刷怪数
	 * @param ruleId
	 * @return
	 */
	public int getRefreshMax(int ruleId);
	
	/**
	 * 获取bossId
	 * @param ruleId
	 * @return
	 */
	public Set<String> getBossId(int ruleId);
	
	/**
	 * 规则是否存在
	 * @param ruleId
	 * @return
	 */
	public boolean ruleIsExist(int ruleId);
}
