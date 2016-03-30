package sacred.alliance.magic.dao;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.asyncarena.domain.AsyncArenaRole;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.operate.donate.domain.RoleDonate;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.rank.domain.RankDbInfo;
import com.game.draco.app.rank.domain.RankLogArenaDB;
import com.game.draco.app.rank.domain.RankLogCountDB;
import com.game.draco.app.richman.domain.RoleRichMan;
import com.game.draco.app.tower.domain.RoleTowerGate;
import com.game.draco.app.tower.domain.RoleTowerInfo;

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
	 * @param keyName "campId"
	 * @param value rankItem.getSubType()
	 * @param keyLimit "limit"
	 * @param valueLimit 记录限制数目
	 */
	public List<RoleInstance> selectLevelRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	//RankRoleScoreLogic
	public List<RoleInstance> selectScoreRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	// 3 hero 20140630
	public List<RoleHero> selectHeroRole(String keyName, byte value, String keyLimit, int valueLimit);

	// 5 horse 20140704
	public List<RoleHorse> selectHorseRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	// 6 godess
	public List<RolePet> selectPetRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	public List<RoleInstance> selectGameMoneyRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	public List<RoleInstance> selectHonorRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	public List<RoleInstance> selectRechargeRole(String keyName, byte value, String keyLimit, int valueLimit);
	
	public List<RoleInstance> selectConsumeRole(String keyName, byte value, String keyLimit, int valueLimit);
	

	public List<RoleArena> selectAllArena3V3(String keyLimit, int valueLimit);
	/*
	 * 异步竞技场
	 */
	public List<AsyncArenaRole> selectAllAsyncArena(String keyLimit, int valueLimit);
	/*
	 * 大富翁
	 */
	public List<RoleRichMan> selectAllRichMan(String keyLimit, int valueLimit);
	/**
	 * 从db返回评分排行榜需要的role
	 * @param keyName "camp"
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
	 * @param keyName "camp"
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
	 * @param keyName "camp"
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
	public List<RoleCount> selectTaobao( int compassType, int valueLimit);

	
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
	
	/**
	 * @param keyType ："rankId"
	 * @param rankId
	 * @param keyLimit
	 * @param valueLimit
	 * @return
	 */
	public List<RoleDonate> selectRankIdRoleDonate(String keyType, int rankId,
			String keyLimit, int valueLimit);
	public List<RoleTowerGate> selectAllTowerGate(String keyLimit, int valueLimit);
}
