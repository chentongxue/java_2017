package sacred.alliance.magic.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.app.compass.CompassType;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import sacred.alliance.magic.dao.RankDAO;
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
import com.game.draco.app.rank.logic.RankLogic;
import com.game.draco.app.richman.domain.RoleRichMan;
import com.game.draco.app.tower.domain.RoleTowerGate;
import com.game.draco.app.tower.domain.RoleTowerInfo;
/**
 * 排行榜
 */
public class RankDAOImpl extends SqlMapClientDaoSupport implements RankDAO {
	
	private static final String NAMESPACE = "Rank";
	private static final String SELECT_ROLE = NAMESPACE + ".selectRole";
	

	//1 score 
	private static final String SELECT_SCORE_ALL_ROLE = NAMESPACE + ".selectScoreAllRole";
	private static final String SELECT_SCORE_CAMP_ROLE = NAMESPACE + ".selectScoreCampRole";
	
	//2 level
	private static final String SELECT_LEVEL_ALL_ROLE = NAMESPACE + ".selectLevelAllRole";
	private static final String SELECT_LEVEL_CAMP_ROLE = NAMESPACE + ".selectLevelCampRole";
	
	//3 
	private static final String SELECT_HERO_ALL_ROLE = NAMESPACE + ".selectHeroAllRole";
	private static final String SELECT_HERO_CAMP_ROLE = NAMESPACE + ".selectHeroCampRole";
	//5 horse
	private static final String SELECT_HORSE_ALL_ROLE = NAMESPACE + ".selectHorseAllRole";
	private static final String SELECT_HORSE_CAMP_ROLE = NAMESPACE + ".selectHorseCampRole";
	
	//6
	private static final String SELECT_PET_ALL_ROLE = NAMESPACE + ".selectPetAllRole";
	private static final String SELECT_PET_CAMP_ROLE = NAMESPACE + ".selectPetCampRole";
	
	private static final String SELECT_GAMEMONEY_ALL_ROLE = NAMESPACE + ".selectGameMoneyAllRole";
	private static final String SELECT_HONOR_ALL_ROLE = NAMESPACE + ".selectHonorAllRole";
	private static final String SELECT_RECHARGE_ALL_ROLE = NAMESPACE + ".selectRechargeAllRole";
	private static final String SELECT_CONSUME_ALL_ROLE = NAMESPACE + ".selectConsumeAllRole";
	
	private static final String SELECT_LEVEL_CAREER_ROLE = NAMESPACE + ".selectLevelCareerRole";
	private static final String SELECT_SCORE_CAREER_ROLE = NAMESPACE + ".selectScoreCareerRole";


	private static final String SELECT_GAMEMONEY_CAREER_ROLE = NAMESPACE + ".selectGameMoneyCareerRole";
	private static final String SELECT_HONOR_CAREER_ROLE = NAMESPACE + ".selectHonorCareerRole";
	private static final String SELECT_RECHARGE_CAREER_ROLE = NAMESPACE + ".selectRechargeCareerRole";
	private static final String SELECT_CONSUME_CAREER_ROLE = NAMESPACE + ".selectConsumeCareerRole";
	
	
	private static final String SELECT_EQUIP_ALL_ROLE = NAMESPACE + ".selectEquipAllRole";
	private static final String SELECT_EQUIP_CAMP_ROLE = NAMESPACE + ".selectEquipCampRole";
	
	private static final String SELECT_EQUIP = NAMESPACE + ".selectEquip";
	//擂台赛
	private static final String SELECT_ALL_ARENA1V1 = NAMESPACE + ".selectAllArena1V1";
	private static final String SELECT_CAREER_ARENA1V1 = NAMESPACE + ".selectCareerArena1V1";
	private static final String SELECT_ALL_ARENA1VN = NAMESPACE + ".selectAllArena1VN";
	private static final String SELECT_CAREER_ARENA1VN = NAMESPACE + ".selectCareerArena1VN";
	
	//淘宝-未用
	private static final String SELECT_TYPE_TAOBAO = NAMESPACE + ".selectTypeTaobao";

	//活动排行榜
	private static final String SELECT_ALL_RANKDBINFO = NAMESPACE + ".selectAllRankDbInfo";
	private static final String SELECT_RANKID_ROLEDONATE = NAMESPACE + ".selectRankIdRoleDonate";
	
	//鲜花排行
	private static final String SELECT_ALL_FLOWER = NAMESPACE + ".selectAllFlower";
	private static final String SELECT_TYPE_FLOWER = NAMESPACE + ".selectTypeFlower";
	private static final String SELECT_TODAY_ALL_FLOWER = NAMESPACE + ".selectTodayAllFlower";
	private static final String SELECT_TODAY_TYPE_FLOWER = NAMESPACE + ".selectTodayTypeFlower";
	
	//杀人数
	private static final String SELECT_TODAY_ALL_KILL = NAMESPACE + ".selectTodayAllKill";
	private static final String SELECT_TODAY_TYPE_KILL = NAMESPACE + ".selectTodayTypeKill";
	//异步竞技场
	private static final String SELECT_ALL_ASYNCARENA = NAMESPACE + ".selectAllAsyncArena";
	private static final String SELECT_TYPE_ASYNCARENA = NAMESPACE + ".selectTypeAsyncArena";
	//大富翁
	private static final String SELECT_ALL_RICHMAN = NAMESPACE + ".selectAllRichMan";
	private static final String SELECT_TYPE_RICHMAN = NAMESPACE + ".selectTypeRichMan";
	
	private static final String SELECT_All_ARENA_3V3 = NAMESPACE + ".selectAllArena3V3";
	private static final String SELECT_All_TOWER_GATE = NAMESPACE + ".selectAllTowerGate";
	
	
	@Override
	public List<RoleDonate> selectRankIdRoleDonate(String keyType, int rankId,
			String keyLimit, int valueLimit) {
		Map map = new HashMap();
		map.put("key1", keyType);
		map.put("value1", rankId);
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		return (List<RoleDonate>)this.getSqlMapClientTemplate().queryForList(SELECT_RANKID_ROLEDONATE, map);
	}
	
	@Override
	public RoleInstance selectRole(String keyName, String value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return (RoleInstance)this.getSqlMapClientTemplate().queryForObject(SELECT_ROLE, map);
	}
	
	private <T>List<T> selectRoleList(String keyName, byte value, 
			String keyLimit, int valueLimit,
			String allSql,String partSql){
		Map map = new HashMap();
		map.put("key1", keyName);
		map.put("value1", value);
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		String sqlKey = allSql ;
		//综合排名
		if(value != RankLogic.RANK_ALL){
			sqlKey = partSql ;
		}
		return (List<T>)this.getSqlMapClientTemplate().queryForList(sqlKey, map);
	}
	
	@Override
	public List<RoleInstance> selectLevelRole(String keyName, byte value, String keyLimit, int valueLimit) {
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
//				SELECT_LEVEL_ALL_ROLE, SELECT_LEVEL_CAREER_ROLE);
				SELECT_LEVEL_ALL_ROLE, SELECT_LEVEL_CAMP_ROLE);
	}
	
	
	@Override
	public List<RoleInstance> selectScoreRole(String keyName, byte value, String keyLimit, int valueLimit) {
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
//				SELECT_SCORE_ALL_ROLE, SELECT_SCORE_CAREER_ROLE);
		        SELECT_SCORE_ALL_ROLE, SELECT_SCORE_CAMP_ROLE);
	}
	//3 hero 20140630
	@Override
	public List<RoleHero> selectHeroRole(String keyName, byte value, String keyLimit, int valueLimit) {
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_HERO_ALL_ROLE, SELECT_HERO_CAMP_ROLE);
	}
	//5 horse 20140704
	@Override
	public List<RoleHorse> selectHorseRole(String keyName, byte value, String keyLimit, int valueLimit) {
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_HORSE_ALL_ROLE, SELECT_HORSE_CAMP_ROLE);
	}
	//6  pet 20140701
	@Override
	public List<RolePet> selectPetRole(String keyName, byte value, String keyLimit, int valueLimit) {
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_PET_ALL_ROLE, SELECT_PET_CAMP_ROLE);
	}
	
	@Override
	public List<RoleInstance> selectGameMoneyRole(String keyName, byte value, String keyLimit, int valueLimit){
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_GAMEMONEY_ALL_ROLE, SELECT_GAMEMONEY_CAREER_ROLE);
	}
	
	
	@Override
	public List<RoleArena> selectAllArena3V3(String keyLimit, int valueLimit){
		Map map = new HashMap();
		map.put("key", keyLimit);
		map.put("value", valueLimit);
		return (List<RoleArena>)this.getSqlMapClientTemplate().queryForList(SELECT_All_ARENA_3V3, map);
	}
	
	@Override
	public List<RoleInstance> selectHonorRole(String keyName, byte value, String keyLimit, int valueLimit){
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_HONOR_ALL_ROLE, SELECT_HONOR_CAREER_ROLE);
	}
	
	
	public List<RoleInstance> selectRechargeRole(String keyName, byte value, String keyLimit, int valueLimit){
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_RECHARGE_ALL_ROLE, SELECT_RECHARGE_CAREER_ROLE);
	}
	
	public List<RoleInstance> selectConsumeRole(String keyName, byte value, String keyLimit, int valueLimit){
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_CONSUME_ALL_ROLE, SELECT_CONSUME_CAREER_ROLE);
	}
	

	@Override
	public List<RoleInstance> selectEquipRole(String keyName, byte value, String keyLimit, int valueLimit) {
		//综合排名
		Map map = new HashMap();
		map.put("key1", keyName);
		map.put("value1", value);
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		if(value == RankLogic.RANK_ALL){
			return (List<RoleInstance>)this.getSqlMapClientTemplate().queryForList(SELECT_EQUIP_ALL_ROLE, map);
		}
		return (List<RoleInstance>)this.getSqlMapClientTemplate().queryForList(SELECT_EQUIP_CAMP_ROLE, map);
	}

	@Override
	public List<RoleGoods> selectEquip(String keyName, String value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return (List<RoleGoods>)this.getSqlMapClientTemplate().queryForList(SELECT_EQUIP, map);
	}

	@Override
	public List<RankLogArenaDB> selectArena1V1(String keyName, byte value, String keyDate, Date date, String keyLimit, int valueLimit) {
		Map map = new HashMap();
		//职业排名
		map.put("key1", keyName);
		map.put("value1", value);
		//日期
		map.put("key2", keyDate);
		map.put("value2", date);
		//记录数
		map.put("key3", keyLimit);
		map.put("value3", valueLimit);
		
		if(value == RankLogic.RANK_ALL){
			return (List<RankLogArenaDB>)this.getSqlMapClientTemplate().queryForList(SELECT_ALL_ARENA1V1, map);
		}
		return (List<RankLogArenaDB>)this.getSqlMapClientTemplate().queryForList(SELECT_CAREER_ARENA1V1, map);
	}

	@Override
	public List<RankLogArenaDB> selectArena1VN(String keyName, byte value, String keyLimit, int valueLimit) {
		//职业排名
		Map map = new HashMap();
		map.put("key1", keyName);
		map.put("value1", value);
		//记录数
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		if(value == RankLogic.RANK_ALL){
			return (List<RankLogArenaDB>)this.getSqlMapClientTemplate().queryForList(SELECT_ALL_ARENA1VN, map);
		}
		return (List<RankLogArenaDB>)this.getSqlMapClientTemplate().queryForList(SELECT_CAREER_ARENA1VN, map);
	}


	/**
	 * 淘宝排行榜，未使用
	 */
	@Override
	public List<RoleCount> selectTaobao(int compassType ,int valueLimit) {
		//上古法阵类型
		String value = "";
		if(compassType == CompassType.taitan.getType()){
			value = "todayTaitan";
		}else if(compassType == CompassType.julong.getType()){
			value = "todayJulong";
		}
		Map map = new HashMap();
		map.put("value1", value);
		//记录数
		map.put("value2", valueLimit);
		return (List<RoleCount>)this.getSqlMapClientTemplate().queryForList(SELECT_TYPE_TAOBAO, map);
	}


	@Override
	public List<RankDbInfo> selectAllRankDbInfo(String keyName, String value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return (List<RankDbInfo>)this.getSqlMapClientTemplate().queryForList(SELECT_ALL_RANKDBINFO, map);
	}
	
	
	@Override
	public List<RankLogCountDB> selectFlower(String keyType, int gender,
			String keyLimit, int valueLimit) {
		Map map = new HashMap();
		map.put("key1", keyType);
		map.put("value1", gender);
		// 记录数
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		if (gender == RankLogic.RANK_ALL) {
			return (List<RankLogCountDB>) this.getSqlMapClientTemplate()
					.queryForList(SELECT_ALL_FLOWER, map);
		}
		return (List<RankLogCountDB>) this.getSqlMapClientTemplate()
				.queryForList(SELECT_TYPE_FLOWER, map);
	}

	@Override
	public List<RankLogCountDB> selectTodayFlower(String keyType, int gender, String keyLimit, int valueLimit) {
		Map map = new HashMap();
		map.put("key1", keyType);
		map.put("value1", gender);
		// 记录数
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		if(gender == RankLogic.RANK_ALL){
			return (List<RankLogCountDB>)this.getSqlMapClientTemplate().queryForList(SELECT_TODAY_ALL_FLOWER, map);
		}
		return (List<RankLogCountDB>)this.getSqlMapClientTemplate().queryForList(SELECT_TODAY_TYPE_FLOWER, map);
	}

	@Override
	public List<RankLogCountDB> selectTodayKillCount(String keyType,
			int campType, String keyLimit, int valueLimit) {
		Map map = new HashMap();
		map.put("key1", keyType);
		map.put("value1", campType);
		// 记录数
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		if(campType == RankLogic.RANK_ALL){
			return (List<RankLogCountDB>)this.getSqlMapClientTemplate().queryForList(SELECT_TODAY_ALL_KILL, map);
		}
		return (List<RankLogCountDB>)this.getSqlMapClientTemplate().queryForList(SELECT_TODAY_TYPE_KILL, map);
	}
	/*
	 * 异步竞技场
	 */
	@Override
	public List<RoleInstance> selectTodayAsyncArena(String keyType, 
			int campType, String keyLimit, int valueLimit) {
		Map map = new HashMap();
		map.put("key1", keyType);
		map.put("value1", campType);
		// 记录数
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		if(campType == RankLogic.RANK_ALL){
			return (List<RoleInstance>)this.getSqlMapClientTemplate().queryForList(SELECT_ALL_ASYNCARENA, map);
		}
		return (List<RoleInstance>)this.getSqlMapClientTemplate().queryForList(SELECT_TYPE_ASYNCARENA, map);
	}

	@Override
	public List<AsyncArenaRole> selectAllAsyncArena(String keyName, int valueLimit) {
		Map map = new HashMap();
		map.put("key1", keyName);
		map.put("value2", valueLimit);
		return (List<AsyncArenaRole>)this.getSqlMapClientTemplate().queryForList(SELECT_ALL_ASYNCARENA, map);
	}
	@Override
	public List<RoleRichMan> selectAllRichMan(String keyLimit, int valueLimit) {
		Map map = new HashMap();
		map.put("key1", keyLimit);
		map.put("value2", valueLimit);
		return (List<RoleRichMan>)this.getSqlMapClientTemplate().queryForList(SELECT_ALL_RICHMAN, map);
	}
	
	@Override
	public List<RoleTowerGate> selectAllTowerGate(String keyLimit, int valueLimit){
		Map map = new HashMap();
		map.put("key", keyLimit);
		map.put("value", valueLimit);
		return (List<RoleTowerGate>)this.getSqlMapClientTemplate().queryForList(SELECT_All_TOWER_GATE, map);
	}

}
