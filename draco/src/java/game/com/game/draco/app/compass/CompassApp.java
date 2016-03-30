package com.game.draco.app.compass;

import com.game.draco.app.AppSupport;
import com.game.draco.app.compass.config.Compass;
import com.game.draco.app.compass.config.TaobaoExtra;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import java.util.Collection;

/**
 * 上古法阵
 * 又名淘宝
 */
public interface CompassApp extends Service,AppSupport{
	
	/**
	 * 获得上古法阵配置信息
	 * @param id
	 * @return
	 */
	public Compass getCompass(short id);
	
	public TaobaoExtra getTaobaoExtra(short extraId) ;
	
	/**
	 * 验证是否满足抽奖条件
	 * @param role
	 * @param id
	 * @param count
	 * @return
	 */
	public Result checkCondition(RoleInstance role, short id, byte count);
	
	/**
	 * 获得停止位置
	 * @param role
	 * @param id
	 * @param count 抽奖次数
	 * @return 中奖的位置
	 */
	public byte[] getCompassStopPlace(RoleInstance role, short id, byte count);
	
	/**
	 * 转盘停止 获得奖励
	 * @param role
	 * @param id
	 */
	public Result compassStop(RoleInstance role, short id);
	

	/**
	 * 获取上古法阵列表消息
	 * @param role
	 * @return
	 */
	public Message getCompassListMessage(RoleInstance role);
	
	/**
	 * 打开上古法阵面板
	 * @param role
	 * @param id
	 * @return
	 */
	public Message openCompassPanel(RoleInstance role, short id);
	
	/**
	 * 获取所有上古法阵
	 * @return
	 */
	public Collection<Compass> getAllCompass();
	
}
