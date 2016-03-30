package sacred.alliance.magic.app.count;

import java.util.Date;

import sacred.alliance.magic.app.arena.ArenaType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.base.AppType;

public interface CountApp extends Service, AppSupport{
	
	
	public void joinApp(RoleInstance role,AppType appType);
	
	/**
	 * 更新用户参加上古法阵地阶次数
	 * @param role
	 * @param id 法阵ID
	 * @param num 抽奖次数
	 */
	public void updateTaobao(RoleInstance role, short id, int num);
	

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


	public void offlineLog(RoleInstance role);
	
	/**
	 * 收到好友送花
	 * @param receiver
	 * @param count
	 */
	public void receiveFlower(RoleInstance receiver, int count);
	

	/**
	 * 炼金每日清零，连续炼金次数清零，未获得暴击的连续次数清零
	 */
	public boolean setAlchemyCount(RoleInstance role,byte alchemyNoBreakOutCount,String alchemyCount);
	/**
	 * 
	 * @param role
	 * @param leftTimes 剩余轮次
	 * @return
	 * @date 2014-10-14 上午10:11:01
	 */
	public boolean setLuckyBoxTime(RoleInstance role ,int leftTimes, Date luckyBoxLastOpenTime);
	public boolean setLuckyBoxCount(RoleInstance role,String luckyBoxCountJsonStr, String luckyBoxPlaceJsonStr);

	public boolean setAccumulateLoginCount(RoleInstance role,int accumulateLoginAwardDays,int accumulateLoginDays);

	public boolean setLuckyFirstUsed(RoleInstance role);
	
//	public void onHookExpDataReset(RoleCount roleCount) ;
	
//	public void onJoinAppDataReset(RoleCount count) ;
	
	
	public void onJoinAppDataReset(RoleInstance role,long joinApp,Date date) ;
}
