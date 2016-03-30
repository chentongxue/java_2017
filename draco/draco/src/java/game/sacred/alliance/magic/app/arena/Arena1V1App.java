package sacred.alliance.magic.app.arena;

import java.util.List;

import sacred.alliance.magic.app.arena.config.Reward1V1Finish;
import sacred.alliance.magic.app.arena.config.Reward1v1Bout;
import sacred.alliance.magic.app.arena.domain.Arena1V1RealTime;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface Arena1V1App extends Service{
	

	public void syncRealTimeData(RoleInstance role);
	
	public int getRank(String roleId);
	
	public int getPageSize();
	
	public List<Arena1V1RealTime> getArena1V1RealTime();
	
	public boolean isAcitveTimes();
	
	public void activeStart();
	
	public void activeStop() ;
	
	public void activeIng() ;
	
	public Reward1v1Bout getReward1v1Bout(int level);
	
	public List<Reward1V1Finish> getDefaultReward1V1();
	/**
	 * 获得角色等级相匹配的奖励配置
	 * @param role
	 * @return
	 */
	public List<Reward1V1Finish> getRoleLevelReward1V1(int roleLevel);
	
	/**
	 * 获得可获得奖励的最大名词
	 * @return
	 */
	public int getMaxArena1V1RewardRank() ;
	
	public boolean isAutoApply(RoleInstance role) ;
	
	public void setAutoApply(RoleInstance role,boolean autoApply) ;
	
}
