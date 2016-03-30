package sacred.alliance.magic.app.team;

import java.util.List;

import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public interface TeamApp {
	
	/**
	 * 判断是否满足组队条件，给目标发送弹板
	 * @param role 邀请人
	 * @param targRole 被邀请人
	 * @return
	 */
	public Status canBuildTeam(RoleInstance role, RoleInstance targRole);
	
	/**
	 * 组队
	 * @param role 被邀请人
	 * @param invitorRole 邀请人
	 * @return
	 */
	public Status buildTeam(RoleInstance role, RoleInstance invitorRole);
	
	public boolean isInSameTeam(AbstractRole role1, AbstractRole role2);
	
	/**
	 * 获取同一地图的活着的组队玩家
	 * @param role
	 * @return
	 */
	public List<AbstractRole> getTeamMembersInSameMap(RoleInstance role);
	
	/**
	 * 组队系数
	 * @param teamMembers
	 * @return
	 */
	public double teamCoefficient(int teamMembers);
	
	/** 加入下线队伍缓存 **/
	public void addOfflineCache(AbstractRole role);
	
	/** 上线时加载队伍 **/
	public void onlineLoadTeam(AbstractRole role);
	
	/**
	 * 玩家下线
	 * @param role
	 */
	public void offline(RoleInstance role);
	

}
