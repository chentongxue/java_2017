package com.game.draco.app.luckybox;

import com.game.draco.app.AppSupport;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 幸运转盘（原幸运宝箱）
 */
public interface LuckyBoxApp extends Service, AppSupport {
	/**
	 * 1915 打开幸运宝箱界面
	 * 
	 * @param role
	 */
	public Message openLuckyBoxPanel(RoleInstance role, byte refreshFlag);

	/**
	 * 1916 抽取宝箱
	 * 
	 * @param role
	 * @param coordinate
	 * @return
	 */
	// public Message playLuckyBox(RoleInstance role,byte coordinate);
	public Message playLuckyBox(RoleInstance role);

	/**
	 * @param role
	 * @return
	 */
	public int getRemainTimes(RoleInstance role);

	/**
	 * 今日玩家玩了多少次
	 * 
	 * @param role
	 * @return
	 */
	public int getPlayTodayTimes(RoleInstance role);
	
	/**
	 * 获取剩余重置次数
	 * @param role
	 * @param rc
	 * @return
	 */
	public int getRefreshTimes(RoleInstance role, RoleCount rc);

	public Message clearRewards(RoleInstance role);
}
