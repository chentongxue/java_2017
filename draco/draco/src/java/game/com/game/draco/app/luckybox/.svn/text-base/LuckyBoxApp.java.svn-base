package com.game.draco.app.luckybox;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;
/**
 * 幸运宝箱
 */
public interface LuckyBoxApp  extends Service{
	/**
	 * 1915 打开幸运宝箱界面
	 * @param role
	 * @date 2014-4-10 下午06:15:39
	 */
	public Message openLuckyBoxPanel(RoleInstance role,byte refreshFlag);
	/**
	 * 抽取宝箱
	 * @param role
	 * @param coordinate
	 * @return
	 * @date 2014-4-12 下午12:07:35
	 */
	public Message getLuckyBoxDraw(RoleInstance role,byte coordinate);
	/**
	 * @param role
	 * @return
	 * @date 2014-4-14 上午11:59:44
	 */
	public int getRemainTimes(RoleInstance role);
	/**
	 * 下线处理
	 * @param role
	 */
	public abstract void offline(RoleInstance role);
}
