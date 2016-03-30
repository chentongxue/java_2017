package sacred.alliance.magic.dao;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.domain.ChargeRecord;
import sacred.alliance.magic.domain.RoleLevelDistribution;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.camp.balance.domain.CampLevel;

public interface RoleDAO {
	public List<Integer> getDelRoleList();
	public int delRole(int roleId);
	/** 获得被隔离的玩家 **/
	public List<RoleInstance> getFrozenRoleList() ;

	/**  根据角色名查询隔离玩家 **/
	public List<RoleInstance> getFrozenRole(String roleName);

	/**  获得被禁言的玩家 **/
	public List<RoleInstance> getForbidRoleList() ;

	/**  根据角色名查询禁言玩家 **/
	public List<RoleInstance> getForbidRoleList(String roleName);
	
	/** 
	 * 查询角色列表(or)
	 * 各条件直接是或的关系
	 * @param userName 帐号
	 * @param roleId 角色ID
	 * @param roleName 模糊的角色名称
	 * @param userId 用户ID
	 * @param channelUserId 渠道用户ID
	 * @return
	 */
	public List<RoleInstance> getRoleList(String userName, String roleId, String roleName, String userId, String channelUserId);
	
	/** 查看玩家充值记录 * */
	public ChargeRecord getRoleAllChargeSum(String rolename) ;
	
	/** 查询玩家金钱排行 **/
	public List<RoleInstance> getRoleMoneyCharts(int orderby,int start, int end);
	
	/** 查询用户金钱排行 */
	public List<RolePayRecord> getUserMoneyRankList(int orderby,int start, int end);
	
	/** 更改玩家角色名称 **/
	public int changeRoleName(int roleId,String newRoleName);
	
	public int changeSilverMoney(String roleId,int changeMoney) ;
	
	/** 修改角色的隔离和禁言信息 */
	public int updateFrozenAndForbid(RoleInstance role);
	
	/**
	 * 查询用户的所有角色(and)
	 * 各条件直接是与的关系
	 * @param userName
	 * @param userId
	 * @param channelUserId
	 * @return
	 */
	public List<RoleInstance> getUserRoles(String userName, String userId, String channelUserId);
	
	/**
	 * 定时更新角色，角色的下线时间不更新 
	 */
	public int timingWriteDBRole(RoleInstance role); 
	
	//public List<RoleInstance> getCareerRoleSortByColumn(String columnName, String subColumnName);
	
	public List<RoleInstance> getCampRoleSortByColumn(String columnName, String subColumnName);
	
	public List<RoleInstance> getRoleByColumn(String columnName, int value);
	
	public List<RoleInstance> getRoleMountByScore(int value);
	
	/**
	 * 清除所有角色的副本掉线标识（服务器启动的时候调用）
	 */
	public void clearAllCopyLostReLoginInfo();
	
	/**
	 * 查询当前时刻角色的等级分布
	 * @return
	 */
	public List<RoleLevelDistribution> getLevelDistributionList();
	
	/**
	 * 查询剩余元宝
	 * @return
	 */
	public long getGoldMoneyReamin();
	
	/**
	 * 根据userId查询最高等级的角色
	 * @param userId
	 * @return
	 */
	public RoleInstance selectMaxLevelRole(String userId);
	
	/**
	 * 获取阵营总等级
	 * @return
	 */
	public List<CampLevel> getCampLevelList(int minLevel);
	
	public int changeRoleCamp(String roleId,byte newCampId);
	
	/**
	 * 修改不在线玩家，被踢出公会的时间
	 * @param role
	 */
	public void updateRoleLeaveFactionTime(RoleInstance role); 
	
	public int countLateLoginRole(Date date);
	
	/**
	 * 获取计算世界等级玩家列表
	 * @param minRank
	 * @param number
	 * @return
	 */
	public List<RoleInstance> getRoleWorldLevelList(int number);
	
}
