package sacred.alliance.magic.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.game.draco.app.compass.domain.CompassTaobaoType;

import sacred.alliance.magic.app.rank.RankLogArenaDB;
import sacred.alliance.magic.app.rank.RankLogCountDB;
import sacred.alliance.magic.app.rank.type.RankLogic;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.dao.RankDAO;
import sacred.alliance.magic.domain.RankDbInfo;
import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public class RankDAOImpl extends SqlMapClientDaoSupport implements RankDAO {
	
	private static final String NAMESPACE = "Rank";
	private static final String SELECT_ROLE = NAMESPACE + ".selectRole";
	
	private static final String SELECT_LEVEL_ALL_ROLE = NAMESPACE + ".selectLevelAllRole";
	private static final String SELECT_SCORE_ALL_ROLE = NAMESPACE + ".selectScoreAllRole";
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
	private static final String SELECT_ALL_TAOBAO = NAMESPACE + ".selectAllTaobao";
	private static final String SELECT_TYPE_TAOBAO = NAMESPACE + ".selectTypeTaobao";
	//藏宝图
	private static final String SELECT_ALL_TREASURE = NAMESPACE + ".selectAllTreasure";
	private static final String SELECT_TYPE_TREASURE = NAMESPACE + ".selectTypeTreasure";

	//活动排行榜
	private static final String SELECT_ALL_RANKDBINFO = NAMESPACE + ".selectAllRankDbInfo";
	
	//鲜花排行
	private static final String SELECT_ALL_FLOWER = NAMESPACE + ".selectAllFlower";
	private static final String SELECT_TYPE_FLOWER = NAMESPACE + ".selectTypeFlower";
	private static final String SELECT_TODAY_ALL_FLOWER = NAMESPACE + ".selectTodayAllFlower";
	private static final String SELECT_TODAY_TYPE_FLOWER = NAMESPACE + ".selectTodayTypeFlower";
	
	//杀人数
	private static final String SELECT_TODAY_ALL_KILL = NAMESPACE + ".selectTodayAllKill";
	private static final String SELECT_TODAY_TYPE_KILL = NAMESPACE + ".selectTodayTypeKill";
	
	private static final String SELECT_ALL_ASYNCARENA = NAMESPACE + ".selectAllAsyncArena";
	private static final String SELECT_TYPE_ASYNCARENA = NAMESPACE + ".selectTypeAsyncArena";
	
	@Override
	public RoleInstance selectRole(String keyName, String value) {
		Map map = new HashMap();
		map.put("key", keyName);
		map.put("value", value);
		return (RoleInstance)this.getSqlMapClientTemplate().queryForObject(SELECT_ROLE, map);
	}
	
	private List<RoleInstance> selectRoleList(String keyName, byte value, 
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
		return (List<RoleInstance>)this.getSqlMapClientTemplate().queryForList(sqlKey, map);
	}
	
	@Override
	public List<RoleInstance> selectLevelRole(String keyName, byte value, String keyLimit, int valueLimit) {
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_LEVEL_ALL_ROLE, SELECT_LEVEL_CAREER_ROLE);
	}
	
	
	@Override
	public List<RoleInstance> selectScoreRole(String keyName, byte value, String keyLimit, int valueLimit) {
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_SCORE_ALL_ROLE, SELECT_SCORE_CAREER_ROLE);
	}
	
	@Override
	public List<RoleInstance> selectGameMoneyRole(String keyName, byte value, String keyLimit, int valueLimit){
		return this.selectRoleList(keyName, value, keyLimit, valueLimit, 
				SELECT_GAMEMONEY_ALL_ROLE, SELECT_GAMEMONEY_CAREER_ROLE);
	}
	
	@Override
	public List<RoleArena> selectAllArena(String keyLimit, int valueLimit){
		//综合排名
		/*Map map = new HashMap();
		map.put("key", keyLimit);
		map.put("value", valueLimit);
		return (List<RoleArena>)this.getSqlMapClientTemplate().queryForList(SELECT_All_ARENA, map);*/
		return null ;
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
	public List<RoleCount> selectTaobao(String keyType, byte compassType, String keyLimit, int valueLimit) {
		//上古法阵类型
		String value = "";
		if(compassType == CompassTaobaoType.di_gong.getType()){
			value = "todayTaobaoLand";
		}else if(compassType == CompassTaobaoType.yao_gong.getType()){
			value = "todayTaobaoGod";
		}else if(compassType == CompassTaobaoType.tian_gong.getType()){
			value = "todayTaobaoSky";
		}
		Map map = new HashMap();
		map.put("key1", keyType);
		map.put("value1", value);
		//记录数
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		if(compassType == RankLogic.RANK_ALL){
			return (List<RoleCount>)this.getSqlMapClientTemplate().queryForList(SELECT_ALL_TAOBAO, map);
		}
		return (List<RoleCount>)this.getSqlMapClientTemplate().queryForList(SELECT_TYPE_TAOBAO, map);
	}

	@Override
	public List<RoleCount> selectTreasure(String keyType, byte qualityType, String keyLimit, int valueLimit) {
		String value = "";
		if(qualityType == QualityType.green.getType()){
			value = "treasureMapGreen";
		}else if(qualityType == QualityType.blue.getType()){
			value = "treasureMapBlue";
		}else if(qualityType == QualityType.purple.getType()){
			value = "treasureMapPurple";
		}else if(qualityType == QualityType.red.getType()){
			value = "treasureMapGolden";
		}else if(qualityType == QualityType.orange.getType()){
			value = "treasureMapOrange";
		}
		Map map = new HashMap();
		map.put("key1", keyType);
		map.put("value1", value);
		//记录数
		map.put("key2", keyLimit);
		map.put("value2", valueLimit);
		if(qualityType == RankLogic.RANK_ALL){
			return (List<RoleCount>)this.getSqlMapClientTemplate().queryForList(SELECT_ALL_TREASURE, map);
		}
		return (List<RoleCount>)this.getSqlMapClientTemplate().queryForList(SELECT_TYPE_TREASURE, map);
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

}
