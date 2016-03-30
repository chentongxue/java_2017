//package sacred.alliance.magic.app.faction;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import com.game.draco.app.npc.domain.NpcInstance;
//
//import sacred.alliance.magic.app.faction.integral.IntegralChannel;
//import sacred.alliance.magic.app.faction.integral.IntegralResult;
//import sacred.alliance.magic.base.FactionDescType;
//import sacred.alliance.magic.base.FactionIntegralLogType;
//import sacred.alliance.magic.base.FactionPositionType;
//import sacred.alliance.magic.base.FactionPowerType;
//import sacred.alliance.magic.base.OperatorType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.core.Service;
//import sacred.alliance.magic.core.exception.ServiceException;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionBuild;
//import sacred.alliance.magic.domain.FactionIntegralLog;
//import sacred.alliance.magic.domain.FactionRole;
//import sacred.alliance.magic.util.ListPageDisplay;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public interface FactionApp extends Service{
//	
//	/**
//	 * 根据门派ID查找门派
//	 * @param factionId
//	 * @return
//	 */
//	public Faction getFaction(String factionId);
//	
//	/**
//	 * 根据角色对象查找门派
//	 * @param role
//	 * @return
//	 */
//	public Faction getFaction(RoleInstance role);
//	
//	/**
//	 * 验证是否满足门派创建条件
//	 * @param role
//	 * @return
//	 */
//	public Result checkCreateCondition(RoleInstance role);
//	
//	/**
//	 * 创建门派
//	 * @param role
//	 * @param factionName
//	 * @return
//	 */
//	public Result createFaction(RoleInstance role,String factionName, String factionDesc) throws ServiceException;
//	
//	/**
//	 * 获取门派列表
//	 * @param currPage 当前页码
//	 * @param size 每页数目
//	 * @return
//	 */
//	public ListPageDisplay<Faction> getFactionList(int currPage, int size);
//	
//	/**
//	 * 申请加入门派
//	 * @param role
//	 * @param factionId
//	 * @return
//	 */
//	public Result applyJoinFaction(RoleInstance role,String factionId);
//	
//	/**
//	 * 获取申请加入门派的请求列表
//	 * @param factionId
//	 * @param currPage
//	 * @param size
//	 * @return
//	 */
//	public List<FactionRole> getApplyJoinList(String factionId);
//	
//	/**
//	 * 接受加入门派请求
//	 * @param leader
//	 * @param factionId
//	 * @param roleId
//	 * @return
//	 */
//	public Result acceptApplyJoin(RoleInstance leader, int roleId) throws ServiceException;
//	
//	/**
//	 * 拒绝加入门派请求
//	 * @param leader
//	 * @param factionId
//	 * @param roleId
//	 * @return
//	 */
//	public Result refuseApplyJoin(RoleInstance leader, int roleId);
//	
//	/**
//	 * 获取门派的所有帮众（未排序）
//	 * @param factionId
//	 * @return
//	 */
//	public Map<Integer,FactionRole> getFactionRoleMap(String factionId);
//	
//	/**
//	 * 获取门派的全部帮众列表（排序）
//	 * @param factionId
//	 * @return
//	 */
//	public List<FactionRole> getFactionRoleList(String factionId);
//	
//	/**
//	 * 获取在线的门派成员
//	 * @param faction
//	 * @return
//	 */
//	public Collection<RoleInstance> getAllOnlineFactionRole(Faction faction);
//	
//	/**
//	 * 退出门派
//	 * @param role
//	 * @return
//	 */
//	public Result exitFaction(RoleInstance role) throws ServiceException;
//	
//	/**
//	 * 踢出门派成员
//	 * @param leader
//	 * @param roleId
//	 * @return
//	 */
//	public Result removeFactionRole(RoleInstance leader, int roleId) throws ServiceException;
//	
//	/**
//	 * 查找门派成员
//	 * 角色登录的时候从数据库中查找
//	 * @param role
//	 * @return
//	 * @throws ServiceException
//	 */
//	public FactionRole searchFactionRole(String factionId,int roleId);
//	
//	/**
//	 * 获取公会成员信息
//	 * @param role
//	 * @return
//	 */
//	public FactionRole getFactionRole(RoleInstance role);
//	
//	/**
//	 * 帮主禅让
//	 * @param role
//	 * @param roleId
//	 * @return
//	 */
//	public Result demisePresident(RoleInstance leader, int roleId) throws ServiceException;
//	
//	/**
//	 * 邀请角色加入门派
//	 * @param leader
//	 * @param role
//	 * @return
//	 */
//	public Result inviteJoinFaction(RoleInstance leader, RoleInstance role);
//	
//	/**
//	 * 接受入会邀请
//	 * @param role
//	 * @param factionId
//	 * @return
//	 */
//	public Result acceptInvitation(RoleInstance role, String factionId) throws ServiceException;
//	
//	/**
//	 * 获得门派建筑NPC出生配置信息
//	 * @return
//	 */
//	public Map<Integer, Map<Integer, FactionBuild>> getBuildConfigMap();
//	
//	/**
//	 * 创建门派领地的所有建筑NPC
//	 * @param factionId
//	 * @return
//	 */
//	public Map<String,NpcInstance> createBuildNpcInstance(String factionId);
//	
//	/**
//	 * 门派建筑的创建或升级
//	 * @param role
//	 * @param npcId
//	 * @return
//	 * @throws ServiceException
//	 */
//	public Result createOrUpgradeBuilding(RoleInstance role, int buildId);
//	
//	/**
//	 * 建筑NPC死亡
//	 * @param factionId
//	 * @param npc
//	 * @return
//	 * @throws ServiceException
//	 */
//	public Result buildNpcDeath(String factionId, NpcInstance npc);
//	
//	/**
//	 * 修改门派宗旨
//	 * @param role
//	 * @param desc
//	 * @return
//	 */
//	public Result modifyFactionDesc(RoleInstance role, String desc);
//	
//	/**
//	 * 修改自己签名
//	 * @param role
//	 * @param signature
//	 * @return
//	 */
//	public Result modifySignature(RoleInstance role, String signature);
//	
//	/**
//	 * 修改门派名称
//	 * @param role
//	 * @param newName
//	 * @return
//	 */
//	public Result modifyFactionName(RoleInstance role, String newName);
//	
//	public boolean canModifyFactionName(String factionName) ;
//	
//	/**
//	 * 停服时，所有门派成员信息入库
//	 * @return
//	 */
//	public boolean saveAllFactionRoles();
//	
//	/**
//	 * 保存门派信息
//	 * @return
//	 */
//	public void saveAllFaction();
//	
//	/**
//	 * 门派成员下线时即时更新缓存
//	 * @param role
//	 */
//	public void factionRoleOffline(RoleInstance role);
//	
//	/**
//	 * 是否公会PVP地图
//	 * @param mapId
//	 * @return
//	 */
//	public boolean isFactionPvpMap(String mapId);
//	
//	/**
//	 * 获取职位所拥有的权限
//	 * @param positionType
//	 * @return
//	 */
//	public Set<FactionPowerType> getPowerTypeSet(FactionPositionType positionType);
//	
//	/**
//	 * 获取角色在门派中的权限
//	 * @param role
//	 * @return
//	 */
//	public Set<FactionPowerType> getPowerTypeSet(RoleInstance role);
//	
//	public boolean haveFactionPowerType(RoleInstance role,FactionPowerType powerType);
//	
//	/**
//	 * 获取角色的门派职位
//	 * @param role
//	 * @return
//	 */
//	public FactionPositionType getPositionType(RoleInstance role);
//	
//	/**
//	 * 修改门派积分
//	 * @param role
//	 * @param operatorType 增加/减少
//	 * @param value 变化量
//	 * @param channel 修改渠道
//	 * @param isForcedAdd 增加溢出时是否强制执行
//	 * @return
//	 */
//	public IntegralResult changeFactionIntegral(RoleInstance role, OperatorType operatorType, 
//			int value,IntegralChannel channel,boolean isForcedAdd);
//	
//	/**
//	 * 改变门派成员的门派贡献度
//	 * @param role
//	 * @param operatorType
//	 * @param value
//	 * @return
//	 */
//	public Result changeContributeNum(RoleInstance role, OperatorType operatorType, int value);
//	
//	/**
//	 * 角色登录初始化数据
//	 * @param role
//	 */
//	public void login(RoleInstance role);
//	
//	/**
//	 * 门派说明信息
//	 * @return
//	 */
//	public Map<FactionDescType,FactionDescribe> getDescribeMap();
//	
//	
//	/**
//	 * 获取门派等级配置
//	 * @return
//	 */
//	public Map<Byte,FactionUpgrade> getFactionUpgradeMap();
//	
//	/**
//	 * 获取门派积分日志
//	 * @param factionId 门派ID
//	 * @param startRow 开始记录行
//	 * @param rows 查询行数
//	 * @param integralLogType 积分日志类型
//	 * @return
//	 */
//	public List<FactionIntegralLog> getFactionIntegralLogList(String factionId,
//			int startRow, int rows, FactionIntegralLogType integralLogType);
//	
//	/**
//	 * 清理积分日志（定时任务触发）
//	 * 删除一周前的积分日志数据
//	 */
//	public void clearIntegralLog();
//	
//	/**
//	 * 获取门派成员的缓存（不查库）
//	 * @param factionId
//	 * @return
//	 */
//	public Map<Integer,FactionRole> getFactionRoleCache(String factionId);
//	
//	/**
//	 * 弹劾
//	 * @param role
//	 * @param goodsNum
//	 * @return
//	 */
//	public Result impeach(RoleInstance role);
//	
//	/**
//	 * 根据门派名查找门派（支持模糊查询）
//	 * @param factionName 模糊的门派名称
//	 * @return
//	 */
//	public List<Faction> getFactionListByName(String factionName);
//	
//	/**
//	 * 获取所有门派
//	 * @return
//	 */
//	public Map<String, Faction> getFactionMap();
//
//	/**门派日志*/
//	public void factionLog();
//	/**门派成员日志*/
//	public void factionMemberLog();
//	
//	/**
//	 * 获取门派的全部帮众列表（按在线，贡献排序）
//	 * @param frList
//	 * @return
//	 */
//	public List<FactionRole> getFactionRoleListByOnline(List<FactionRole> frList);
//	
//	/**
//	 * 获取建筑名字
//	 * @param buildId
//	 * @param buildLevel
//	 * @return
//	 */
//	public String getBuildName(int buildId, int buildLevel);
//	
//	/**
//	 * 加入门派成员列表
//	 * @param role
//	 * @param faction
//	 * @param factionRole
//	 * @return
//	 * @throws ServiceException
//	 */
//	public Result addFactionRole(RoleInstance role, Faction faction, FactionRole factionRole) throws ServiceException;
//	
//	/**
//	 * 初始化建筑
//	 * @param role
//	 * @param buildId
//	 * @return
//	 */
//	public Result createBuilding(RoleInstance role, int buildId);
//	
//	/**
//	 * 获取门派排行列表
//	 * @param size
//	 * @return
//	 */
//	public List<Faction> getFactionRankList(int size);
//	
//	/**
//	 * 获取权限列表
//	 * @param role
//	 * @return
//	 */
//	public int getFactionPosition(RoleInstance role) ;
//	
//	/**
//	 * 改变职位
//	 * @param faction
//	 */
//	public void changePosition(Faction faction);
//	
//	/**
//	 * 地图ID
//	 * @return
//	 */
//	public String getFactionMapId();
//	
//	/**
//	 * 创建失败消息
//	 * @return
//	 */
//	public String getFactionCreateInfo();
//	
//	/**
//	 * 几天不上线可弹劾
//	 * @return
//	 */
//	public int getImpeachDay();
//	
//	/**
//	 * 获取门派战的门派
//	 * @param totalSize
//	 * @return
//	 */
//	public List<Faction> getFactionWarFactionList(int campFactinCount);
//	
//	/**
//	 * 获取职位
//	 * @param roleId
//	 * @param factionId
//	 * @return
//	 */
//	public int getFactionRolePosition(String roleId, String factionId);
//	
//	/**
//	 * 获取第一门派
//	 * @return
//	 */
//	public Faction getFirstFaction();
//	
//	/**
//	 * 根据阵营排名第一的门派
//	 * @param campId
//	 * @return
//	 */
//	public Faction getCampFirstFaction(byte campId);
//	
//	/**
//	 * 获取factionId
//	 * @param roleId
//	 * @return
//	 */
//	public String getFactionId(String roleId);
//}
