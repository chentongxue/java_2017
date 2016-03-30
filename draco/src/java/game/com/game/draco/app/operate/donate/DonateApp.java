package com.game.draco.app.operate.donate;

import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.operate.donate.config.DonateInfo;
import com.game.draco.app.operate.donate.domain.RoleDonate;
import com.game.draco.app.operate.donate.domain.WorldDonate;

public interface DonateApp extends Service, AppSupport {
	public static final byte REWARD_STATE_ERROR = -1; //错误，不存在活动排行等
	public static final byte REWARD_STATE_NO = 0; //无奖励
	public static final byte REWARD_STATE_DISABLE = 1; //有奖励但尚不能领取
	public static final byte REWARD_STATE_ENABLE = 2; //可领奖
	public static final byte REWARD_STATE_REWARDED = 3; //已领奖
	
	public final String CAT = "--";
	
	/**
	 * 服务器停止时存全民活动db信息
	 */
	public  void saveWorldDonateDb();
	
	/**
	 * 返回所有的乐翻天活动
	 */
	public Map<Integer, DonateInfo> getAllDonateMap();
	/**
	 * 返回活动排行榜领奖状态：0:无奖励 1:有奖励但尚不能领取 2:可领奖 3:已领奖
	 * @param role
	 * @param rankItem
	 * @return DonateResult
	 */
	public DonateResult getRankRewardStat(RoleInstance role, DonateInfo donateInfo);
	
	/**
	 * 返回乐翻天全民奖励状态
	 * @param role
	 * @param donateInfo
	 * @return
	 */
	public byte getWorldAwardStat(RoleInstance role, DonateInfo donateInfo);
	
	/**
	 * 创建活动排行榜详细信息消息
	 * @param role
	 * @param activeId
	 * @return
	 */
	public Message createRankDetailMsg(RoleInstance role, int activeId);
	
	/**
	 * 热加载接口
	 * @return
	 */
	public Result reLoad();
	
	/**
	 * 创建乐翻天详情消息
	 * @param role
	 * @param activeId
	 * @return
	 */
	public Message createDonateDetailMsg(RoleInstance role, int activeId);
	
	/**
	 * 捐献
	 * @param role
	 * @param activeId
	 * @return
	 */
	public Result donate(RoleInstance role, int activeId);
	
	/**
	 * 创建乐翻天全民领奖信息
	 * @param role
	 * @param activeId
	 * @param condValue
	 * @return
	 */
	public Message recvWorldReward(RoleInstance role, int activeId, int condValue);
	
	/**
	 * 创建乐翻天个人排名领奖信息
	 * @param role
	 * @param activeId
	 * @return
	 */
	public Message recvRankReward(RoleInstance role, int activeId);
	
	/**
	 * 根据乐翻天数据库信息
	 * @param role
	 * @param rankItem
	 * @return
	 */
	public RoleDonate getRoleDonate(RoleInstance role, int rankId);
	
	public int getEffectData4Rank(RoleDonate roleDonate) ;
	
	
	public boolean isOpen(int activeId) ;
	
	/**
	 * 是否有奖励
	 * @param role
	 * @return
	 */
	public boolean canRecvReward(RoleInstance role);

	public Map<Integer, WorldDonate> getWorldDonateMap();

	public RoleDonate getRoleDonate(String roleId, int rankId);
	
}
