package com.game.draco.app.alchemy;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;
/**
 * 炼金接口
 * @date 2014-4-3 下午03:02:53
 */
public interface AlchemyApp extends Service{
	
	/**
	 * 处理1913号消息，返回界面信息
	 * @param role
	 * @return
	 */
	public Message openAlchemyPanel(RoleInstance role);
	/**
	 * 1914 返回炼金的结果
	 * @param role
	 * @return
	 */
	public Message getAlchemyResult(RoleInstance role, byte rewardType);
	
	public int[] getMaxTimes(RoleInstance role) ;
	
	public int[] getPlayTimes(RoleInstance role) ;
	/**
	 * 红点提示用
	 * @param role
	 * @return
	 */
	boolean hasFreeTimes(RoleInstance role);
}
