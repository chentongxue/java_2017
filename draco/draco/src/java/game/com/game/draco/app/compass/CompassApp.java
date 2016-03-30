package com.game.draco.app.compass;
import java.util.Collection;

import com.game.draco.app.compass.domain.Compass;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;
/**
 * 罗盘，sortId是区分"淘宝"，"幸运转盘"的类型id
 * 罗盘Id是全局唯一
 * @author gaibaoning@moogame.cn modified
 * @date 2014-3-21 下午04:12:47
 */
public interface CompassApp extends Service{
	
	/**
	 * 获得罗盘系统配置信息
	 * @param id
	 * @return
	 */
	public abstract Compass getCompass(short id);

	/**
	 * 验证是否满足抽奖条件
	 * @param role
	 * @param id
	 * @param count 转动几次
	 * @return
	 */
	public abstract Result checkCondition(RoleInstance role, short id, int count);
	
	/**
	 * 获得停止位置
	 * @param role
	 * @param id
	 * @param count 抽奖次数
	 * @return 中奖的位置
	 */
	public abstract byte[] getCompassStopPlace(RoleInstance role, short id, byte count);
	
	/**
	 * 转盘停止 获得奖励
	 * @param role
	 * @param id
	 */
	public abstract Result compassStop(RoleInstance role, short id);
	
	/**
	 * 下线处理
	 * @param role
	 */
	public abstract void offline(RoleInstance role);
	
	/**
	 * 获取罗盘列表列表消息
	 * @param role
	 * @return
	 */
	public abstract Message getCompassListMessage(RoleInstance role);
	
	/**
	 * 打开罗盘面板
	 * @param role
	 * @param id
	 * @return
	 */
	public abstract Message openCompassPanel(RoleInstance role, short id);
	
	/**
	 * 获取所有罗盘
	 * @return
	 */
	public abstract Collection<Compass> getAllCompass();
	
}
