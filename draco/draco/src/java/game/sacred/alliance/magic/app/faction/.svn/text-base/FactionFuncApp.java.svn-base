//package sacred.alliance.magic.app.faction;
//
//import java.util.List;
//import java.util.Map;
//
//import com.game.draco.message.item.FactionDonateItem;
//import com.game.draco.message.response.C1728_FactionDonateRespMessage;
//
//import sacred.alliance.magic.app.hint.HintSupport;
//import sacred.alliance.magic.base.FactionBuildFuncType;
//import sacred.alliance.magic.base.OperatorType;
//import sacred.alliance.magic.base.OutputConsumeType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.core.Service;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionBuild;
//import sacred.alliance.magic.domain.FactionRecord;
//import sacred.alliance.magic.domain.FactionSkill;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public interface FactionFuncApp extends Service, HintSupport {
//	
//	/**
//	 * 加载配置
//	 */
//	public void init();
//	
//	/**
//	 * 获取捐献信息
//	 * @param role
//	 * @return
//	 */
//	public List<FactionDonateItem> getFactionDonateInfo(RoleInstance role);
//	
//	
//	/**
//	 * 门派捐献
//	 * @param moneyType
//	 * @param moneyNum
//	 * @param role
//	 * @return
//	 */
//	public C1728_FactionDonateRespMessage factionDonate(int id, RoleInstance role);
//	
//	/**
//	 * 获取公会创建时创建的建筑信息
//	 * @return
//	 */
//	public Map<Integer, FactionBuild> getFactionCreateBuilding();
//	
//	/**
//	 * 创建公会时创建公会建筑
//	 * @param role
//	 */
//	public Result createFactionBuild(RoleInstance role);
//	
//	/**
//	 * 检测未有的公会建筑是否应该加载到公会
//	 */
//	public void checkBuild(RoleInstance role, Faction faction);
//	
//	/**
//	 * 获取公会技能配置
//	 * @param role
//	 * @param skillId
//	 * @param skillLevel
//	 * @return
//	 */
//	public FactionSkill getFactionSkill(RoleInstance role, int skillId, int skillLevel);
//	
//	/**
//	 * 获取门派建筑等级
//	 * @param role
//	 * @return
//	 */
//	public int getFactionBuildLevel(RoleInstance role, String buildingId);
//	
//	/**
//	 * 获取门派成员积分
//	 * @param role
//	 * @return
//	 */
//	public int getFactionIntegral(RoleInstance role);
//	
//	/**
//	 * 获取门派成员贡献度
//	 * @param role
//	 * @return
//	 */
//	public int getFactionRoleContribute(RoleInstance role);
//	
//	/**
//	 * 领取门派工资
//	 * @param role
//	 * @return
//	 */
//	public Result factionSalary(RoleInstance role);
//	
//	/**
//	 * 建筑升级时，更新公会技能
//	 * @param faction
//	 * @param buildId
//	 */
//	public void upgradeFactionSkill(Faction faction, FactionBuild build);
//	
//	/**
//	 * 获取工资配置
//	 * @param role
//	 * @return
//	 */
//	public FactionSalary getFactionSalary(RoleInstance role);
//	
//	/**
//	 * 创建门派记录
//	 * @param factionRecord
//	 */
//	public void createFactionRecord(FactionRecord factionRecord);
//	
//	/**
//	 * 获取门派记录
//	 * @param factionId
//	 * @return
//	 */
//	public List<FactionRecord> getFactionRecord(String factionId, int startRow, int rows);
//	
//	/**
//	 * 改变公会资金
//	 * @param faction
//	 * @param operatorType
//	 * @param value
//	 * @return
//	 */
//	public Result changeFactionMoney(Faction faction, OperatorType operatorType, int value, OutputConsumeType type, String roleId);
//	
//	/**
//	 * 清除一个月以前的公会记录
//	 */
//	public void clearFactionRecord();
//	
//	/**
//	 * 获取工资配置
//	 * @param role
//	 * @return
//	 */
//	public FactionActive getFactionActive(int type);
//	
//	/**
//	 * 改变公会资金
//	 * @param role
//	 * @param operatorType
//	 * @param value
//	 * @return
//	 */
//	public Result changeFactionMoney(RoleInstance role, OperatorType operatorType, int value, OutputConsumeType type);
//	
//	/**
//	 * 获取仓库格子数
//	 * @param role
//	 * @return
//	 */
//	public int getWarehouseCapacity(RoleInstance role);
//	
//	/**
//	 * 通过Id获取门派建筑
//	 * @param role
//	 * @return
//	 */
//	public FactionBuild getFactionBuildById(RoleInstance role, int buildId);
//	
//	/**
//	 * 通过类型获取门派建筑
//	 * @param role
//	 * @return
//	 */
//	public FactionBuild getFactionBuildByType(RoleInstance role, FactionBuildFuncType type);
//	
//	/**
//	 * 改变个人贡献度
//	 * @param role
//	 * @param operatorType
//	 * @param value
//	 * @return
//	 */
//	public Result changeContributeNum(RoleInstance role, OperatorType operatorType, int value);
//	
//	/**
//	 * 仓库扩容
//	 * @param faction
//	 */
//	public void expansionWarehouse(Faction faction,  FactionBuild build);
//	
//	/**
//	 * 初始化捐献
//	 * @param role
//	 */
//	public void roleLoginInitDonate(RoleInstance role);
//	
//	/**
//	 * 下线合并捐献
//	 * @param role
//	 */
//	public void roleOffLineUniteDonate(RoleInstance role);
//}
