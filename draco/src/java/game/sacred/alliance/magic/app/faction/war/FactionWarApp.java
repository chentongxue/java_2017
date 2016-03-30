//package sacred.alliance.magic.app.faction.war;
//
//import java.util.Set;
//
//import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
//import com.game.draco.message.response.C1745_FactionWarGambleInfoRespMessage;
//import com.game.draco.message.response.C1741_FactionWarRespMessage;
//
//import sacred.alliance.magic.app.active.vo.Active;
//import sacred.alliance.magic.app.faction.war.config.FactionWarAwardRoleConfig;
//import sacred.alliance.magic.app.faction.war.domain.FactionWarInfo;
//import sacred.alliance.magic.app.faction.war.result.GambleResult;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.core.Service;
//import sacred.alliance.magic.vo.MapInstance;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public interface FactionWarApp extends Service,NpcFunctionSupport{
//	/**
//	 * 添加match
//	 * @param match
//	 */
//	public void addFactionWarMatch(FactionWarMatch match);
//	
//	/**
//	 * 获取info
//	 * @param factionId
//	 * @return
//	 */
//	public FactionWarInfo getFactionWarInfo(String factionId);
//	
//	/**
//	 * 获取门派战活动
//	 * @return
//	 */
//	public Active getActive();
//	
//	/**
//	 * 活动结束时，没分出胜负，根据规则取胜利的门派
//	 * @param match
//	 * @return
//	 */
//	public FactionWarInfo getTimeOverWinFaction(FactionWarMatch match, String mapInstanceId);
//	
//	/**
//	 * 获取地图内活着的人数
//	 * @param roleSet
//	 * @return
//	 */
//	public int getMapLiveListSize(Set<String> roleSet, String mapInstanceId, String factionId);
//	
//	/**
//	 * 战斗结束
//	 * @param match
//	 * @param winFactionId
//	 */
//	public void factionWarOver(FactionWarMatch match, String winFactionId);
//	
//	/**
//	 * 获取个人奖励
//	 * @param rounds
//	 * @return
//	 */
//	public FactionWarAwardRoleConfig getRoleAwardConfig(int rounds);
//	
//	/**
//	 * 打开对战面板
//	 * @param role
//	 * @return
//	 */
//	public C1741_FactionWarRespMessage getFactionWarRespMessage(RoleInstance role);
//	
//	/**
//	 * 进入门派战
//	 * @param role
//	 * @return
//	 */
//	public Result enterFactionWar(RoleInstance role); 
//	
//	/**
//	 * 是否有门派战
//	 * @param factionId
//	 * @return
//	 */
//	public boolean hasFactionWar(String factionId);
//	
//	/**
//	 * 押注
//	 * @param role
//	 * @param factionId
//	 * @param money
//	 * @return
//	 */
//	public GambleResult gameble(RoleInstance role, String factionId, int money);
//	
//	/**
//	 * 获取押注信息
//	 * @param role
//	 * @return
//	 */
//	public C1745_FactionWarGambleInfoRespMessage getGambleInfo(RoleInstance role, String factionId);
//	
//	/**
//	 * 开始广播
//	 * @param match
//	 */
//	public void broadCastBegin(FactionWarMatch match);
//	
//	/**
//	 * 通知不在地图内的玩家进入门派战
//	 * @param match
//	 */
//	public void notifyEnterFactionWar(FactionWarMatch match, String mapInstanceId);
//	
//	/**
//	 * 刷新鼓舞NPC
//	 * @param match
//	 * @param mapInstance
//	 */
//	public void refreshFactionSoul(FactionWarMatch match, MapInstance mapInstance);
//	
//	/**
//	 * 鼓舞
//	 * @param param
//	 */
//	public Result factionWarInpire(RoleInstance role, String param);
//	
//	/**
//	 * 创建门派战
//	 */
//	public void createWar();
//	
//	/**
//	 * 离开门派时，从门派战中移除，不发奖
//	 * @param role
//	 */
//	public void exitFaction(RoleInstance role);
//}
