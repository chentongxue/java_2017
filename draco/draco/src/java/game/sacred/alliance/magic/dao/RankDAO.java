package sacred.alliance.magic.dao;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.app.rank.RankLogArenaDB;
import sacred.alliance.magic.app.rank.RankLogCountDB;
import sacred.alliance.magic.domain.RankDbInfo;
import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public interface RankDAO {
	/**
	 * 从db返回roleInstance 
	 * @param keyName "roleId"
	 * @param value role.getRoleId()
	 * @return
	 */
	public RoleInstance selectRole(String keyName, String value);
	/**
	 * 从db返回等级排行榜需要的role，世界取前100，活动取1000
	 * @param keyName "career"
	 * @param value rankItem.getSubType()
	 * @param keyLimit "limit"
	 * @param valueLimit 记录限制数目
	 */
	public List<RoleInstance> selectLevelRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	
	public List<RoleInstance> selectScoreRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	public List<RoleInstance> selectGameMoneyRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	public List<RoleInstance> selectHonorRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	public List<RoleInstance> selectRechargeRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	public List<RoleInstance> selectConsumeRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	/** 大师赛 */
	public List<RoleArena> selectAllArena(String keyLimit, int valueLimit);
	
	
	/**
	 * 从db返回评分排行榜需要的role
	 * @param keyName "career"
	 * @param value rankItem.getSubType()
	 * @param keyLimit "limit"
	 * @param valueLimit 记录限制数目
	 */
	public List<RoleInstance> selectEquipRole(String keyName, byte value, String keyLimit, int valueLimit);
	/**
	 * 从db返回评分排行榜需要的装备
	 * @param keyName "roleId"
	 * @param value 
	 
	 */
	public List<RoleGoods> selectEquip(String keyName, String value);
	/**
	 * 从db返回单挑擂台赛排行榜需要的RoleArena,按职业取，并且参加场次 >= 20
	 * @param keyName "career"
	 * @param value rankItem.getSubType()
	 * @param keyDate "weekDate"
	 * @param date 当前周的第一天
	 * @param keyLimit "limit"
	 * @param valueLimit 记录限制数目
	 * @return
	 */
	public List<RankLogArenaDB> selectArena1V1(String keyName, byte value, String keyDate, Date date, String keyLimit, int valueLimit);
	/**
	 * 从db返回群殴擂台赛排行榜需要的RoleArena,按职业取，并且参加场次 >= 1
	 * @param keyName "career"
	 * @param value rankItem.getSubType()
	 * @param keyLimit "limit"
	 * @param valueLimit 记录限制数目
	 * @return
	 */
	public List<RankLogArenaDB> selectArena1VN(String keyName, byte value, String keyLimit, int valueLimit);
	
	
	/**
	 * 从db返回上古法阵排行榜需要的RoleCount,按上古法阵类型取
	 * @param keyType "compassType"
	 * @param compassType 法阵类型：0，1，2
	 * @param keyLimit "limit"
	 * @param valueLimit 记录限制数目
	 * @return
	 */
	public List<RoleCount> selectTaobao(String keyType, byte compassType, String keyLimit, int valueLimit);
	/**
	 * 从db返回藏宝图排行榜需要的RoleCount,按藏宝图物品品质类型取
	 * @param keyType "qualityType"
	 * @param qualityType 法阵类型：2，3，4，5，6
	 * @param keyLimit "limit"
	 * @param valueLimit 记录限制数目
	 * @return
	 */
	public List<RoleCount> selectTreasure(String keyType, byte qualityType, String keyLimit, int valueLimit);
	
	
	/**
	 * 从db返回活动排行榜数据
	 * @param keyName
	 * @param value
	 * @return
	 */
	public List<RankDbInfo> selectAllRankDbInfo(String keyName, String value);
	
	
	public List<RankLogCountDB> selectFlower(String keyType, int gender, String keyLimit, int valueLimit) ;
	
	public List<RankLogCountDB> selectTodayFlower(String keyType, int gender, String keyLimit, int valueLimit) ;
	
	/**
	 * 当天杀人数
	 * @param keyType
	 * @param campType
	 * @param keyLimit
	 * @param valueLimit
	 * @return
	 */
	public List<RankLogCountDB> selectTodayKillCount(String keyType, int campType, String keyLimit, int valueLimit) ;
	
	/**
	 * 异步竞技场
	 * @param keyType
	 * @param campType
	 * @param keyLimit
	 * @param valueLimit
	 * @return
	 */
	public List<RoleInstance> selectTodayAsyncArena(String keyType, int campType, String keyLimit, int valueLimit) ;
}
