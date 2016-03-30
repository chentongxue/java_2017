package sacred.alliance.magic.app.count;

import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.RoleInstance;

public interface CountApp extends Service{
	
	/**
	 * 增加完成随机任务次数
	 * @param role
	 * @param questType
	 */
	public void incrRmQuestSubmitCount(RoleInstance role);
	
	
	
	
	/**
	 * 更新用户参加上古法阵地阶次数
	 * @param role
	 * @param id 法阵ID
	 * @param num 抽奖次数
	 */
	public void updateTaobao(RoleInstance role, short id, int num);
	
	/**
	 * 更新用户使用藏宝图相应品质次数
	 * qualityType, 2:绿(普通), 3:蓝(神秘), 4:紫(远古)
	 */
	public void updateTreasureMap(RoleInstance role, byte qualityType);
	
	/**
	 * 上线加载角色计数
	 * */
	public void loadRoleCount(RoleInstance role);
	
	/**
	 * 角色计数入库
	 * */
	public void saveRoleCount(RoleInstance role);
	
	public void saveRoleCount(RoleCount count);
	/**
	 * 处理用户充值行为 
	 * payValue:充值金额
	 */
	public void updateRolePay(RoleInstance chargeRole, int payValue);
	/**
	 * 处理用户消费行为
	 * buyValue:消费金额
	 */
	public void updateRoleBuy(RoleInstance role,int buyValue,OutputConsumeType outputConsumeType);
	
	
	public void incrArenaFail(RoleInstance role,ArenaType arenaType,int score);
	
	public void incrArenaWin(RoleInstance role,ArenaType arenaType,int score);
	
	
	public void incrArenaJoin(RoleInstance role,ArenaType arenaType);

	/**
	 * 增加每日免费传送次数
	 * @param role
	 */
	public void incrDayFreeTransport(RoleInstance role) ;
	
	/**
	 * 增加每日免费原地复活次数
	 * @param role
	 */
	public void incrDayFreeReborn(RoleInstance role) ;

	public void offlineLog(RoleInstance role);
	
	/**
	 * 收到好友送花
	 * @param receiver
	 * @param count
	 */
	public void receiveFlower(RoleInstance receiver, int count);
	
	/**
	 * 
	 * @param role 角色
	 * @param points 积分数
	 * @param whichRound 第几轮
	 */
	public boolean addCompassRewardPoints(RoleInstance role,int points,int whichRound);
	
	/**
	 * 炼金每日清零，连续炼金次数清零，未获得暴击的连续次数清零
	 */
	public boolean setAlchemyCount(RoleInstance role,byte alchemyNoBreakOutCount,String alchemyCount);
	/**
	 * 幸运宝箱
	 * @param role
	 * @return
	 */
	public boolean setLuckyBoxCount(RoleInstance role,byte luckyBoxUsedTimes,String luckyBoxCountJsonStr, String luckyBoxPlaceJsonStr);
	
}
