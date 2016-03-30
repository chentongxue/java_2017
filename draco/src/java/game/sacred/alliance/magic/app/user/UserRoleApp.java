package sacred.alliance.magic.app.user;

import java.util.List;

import com.game.draco.app.AppSupport;

import sacred.alliance.magic.base.ChangeNameFlag;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.RoleLevelDistribution;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleShape;

public interface UserRoleApp extends Service, AppSupport{
	
	/**
	 * 处理玩家手动升级
	 * @param role
	 * @return
	 */
	public int handUpLevel(RoleInstance role);

	/**
	 * 通过角色名获取角色
	 * @param roleName
	 * @return
	 * @throws ServiceException
	 */
	public RoleInstance getRoleByRoleName(String roleName) throws ServiceException;
	
	public RoleInstance getRoleByRoleId(String roleId) throws ServiceException;

	/**
	 * 通过角色名获取在线玩家对象
	 * @param roleName
	 * @return 不在线返回null
	 * @throws ServiceException
	 */
	public RoleInstance getOnlineRoleByRoleName(String roleName) throws ServiceException;
	
	public RoleInstance roleLogin(RoleInstance role,ChannelSession session) throws Exception;
	
	 /** 返回被禁言的角色 **/
	public List<RoleInstance> getForbidRoleList();
	
	/** 根据角色名查询禁言玩家 **/
	public List<RoleInstance> getForbidRoleList(String roleName);
	
	 /** 返回被隔离的角色 **/
	public List<RoleInstance> getFrozenRoleList();
	
	/** 根据角色名查询隔离玩家 **/
	public List<RoleInstance> getFrozenRole(String roleName);
	
	/** 
	 * 查询角色列表(or)
	 * 各条件直接是或的关系
	 * @param userName 帐号
	 * @param roleId 角色ID
	 * @param roleName 模糊的角色名称
	 * @param userId 
	 * @param channelUserId 
	 * @return
	 */
	public List<RoleInstance> getRoleList(String userName, String roleId,
			String roleName, String userId, String channelUserId);
	
	/**
	 * 根据角色ID获取角色列表
	 * @param userId
	 * @return
	 */
	public List<RoleInstance> getRoleList(String userId);
	
	/** 查询玩家金钱排行 **/
	public List<RoleInstance> getRoleMoneyCharts(int orderby,int start, int end);
	
	/** 查询用户金钱排行 */
	public List<RolePayRecord> getUserMoneyRankList(int orderby,int start, int end);
	
	/** 修改角色的隔离和禁言信息 */
	public int updateFrozenAndForbid(RoleInstance role);
	
	/**
	 * 根据帐号查询所有角色(and)
	 * 各条件之间是与的关系
	 * @param userName
	 * @return
	 */
	public List<RoleInstance> getUserRoles(String userName, String userId, String channelUserId);
	
	/**
	 * 修改角色名称
	 * @param userId
	 * @param roleId
	 * @param newName
	 * @return
	 */
	public Result modifyRoleName(String userId, String roleId, String newName) throws ServiceException ;
	/**
	 * 下线时异常日志
	 * @param player
	 */
	public void roleOfflineLog(RoleInstance player);
	
	/**
	 * 系统赠送体力值（定时任务触发）
	 * 给所有在线玩家
	 */
	//public void sysRewardRolePower();
	
	public ChangeNameFlag getChangeNameFlag(RoleInstance role) ;
	
	/**
	 * 获取角色的等级分布列表
	 */
	public List<RoleLevelDistribution> getLevelDistributionList();
	
	
	/**
	 * 根据userId查询最高等级的角色
	 * @param userId
	 * @return
	 */
	public RoleInstance selectMaxLevelRole(String userId);
	
	/**
	 * 主推角色外形资源的消息
	 * @param role
	 */
	public void pushRoleMorphNotifyMessage(RoleInstance role);
	
	/** 返回所穿装备资源ID */
	public RoleShape getRoleShape(String roleId);
	
	public RoleShape getDefaultRoleShape() ;
	
}
