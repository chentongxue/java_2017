//package sacred.alliance.magic.app.faction.godbeast;
//
//import java.util.List;
//
//import com.game.draco.message.item.FactionSoulBuffItem;
//import com.game.draco.message.item.FactionSoulSkillItem;
//import com.game.draco.message.response.C1730_FactionSoulFeedRespMessage;
//
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.core.Service;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public interface FactionSoulApp extends Service{
//	
//	/**
//	 * 加载配置
//	 */
//	public void init();
//	
//	/**
//	 * 创建门派神兽
//	 * @param faction
//	 * @return
//	 */
//	public Result createFactionSoul(Faction faction);
//
//	/**
//	 * 得到神兽对像
//	 * @param soulId
//	 * @return
//	 */
//	public FactionSoulInfo getFactionSoulInfo(int soulId);
//
//	/**
//	 * 喂养
//	 * @param role
//	 * @param instanceId
//	 * @param soulId
//	 * @return
//	 */
//	public C1730_FactionSoulFeedRespMessage feed(RoleInstance role);
//	
//	/**
//	 * 更新神兽数据
//	 * @param faction
//	 */
//	public void updateFactionSoul(Faction faction);
//	
//	/**
//	 * 初始化神兽数据
//	 * @param faction
//	 */
//	public void initFactionSoul(Faction faction);
//	
//	/**
//	 * 判断神兽是否可以飞升
//	 * @param role
//	 * @param soulId
//	 * @return
//	 */
//	public Result canFly(RoleInstance role,int soulId);
//	
//	/**
//	 * 神兽飞升
//	 * @param role
//	 * @param soulId
//	 * @return
//	 */
//	public Result factionSoulFly(RoleInstance role,int soulId);
//	
//	/**
//	 * 获取喂养配置
//	 * @param soulId
//	 * @param level
//	 * @return
//	 */
//	public FactionSoulFeed getFactionSoulFeed(int soulId, int level);
//	
//	/**
//	 * 获取飞升配置
//	 * @param soul
//	 * @param level
//	 * @return
//	 */
//	public FactionSoulFlyConfig getFactionSoulFly(int soulId, int flyNum);
//	
//	/**
//	 * 获取最大飞升次数
//	 * @param soulId
//	 * @return
//	 */
//	public int getFactionSoulMaxFly(int soulId);
//	
//	/**
//	 * 获取神兽技能
//	 * @param soulId
//	 * @param flyNum
//	 * @return
//	 */
//	public List<FactionSoulSkillItem> getFactionSoulSkill(int soulId, int flyNum);
//	
//	/**
//	 * 获取下一级神兽技能
//	 * @param soulId
//	 * @param flyNum
//	 * @return
//	 */
//	public List<FactionSoulSkillItem> getNextFactionSoulSkill(int soulId, int flyNum);
//	
//	/**
//	 * 获得神兽鼓舞
//	 * @param factionId
//	 * @return
//	 */
//	public List<FactionSoulInspireConfig> getSoulInspire(String factionId);
//	
//	/**
//	 * 获得鼓舞buff配置
//	 * @param buffId
//	 * @param level
//	 * @return
//	 */
//	public FactionSoulInspireBuffConfig getSoulInspireBuff(short buffId, int level);
//	
//	/**
//	 * 获得鼓舞buff类型
//	 * @param buffId
//	 * @return
//	 */
//	public FactionSoulInspireType getInspireBuffType(short buffId);
//	
//	/**
//	 * 获取鼓舞的buffItem
//	 * @param recored
//	 * @return
//	 */
//	public List<FactionSoulBuffItem> getBuffItems(FactionSoulRecord recored);
//}
