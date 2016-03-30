package com.game.draco.app.goddess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.GoddessEquipBackpack;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsGoddess;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.goddess.config.GoddessBless;
import com.game.draco.app.goddess.config.GoddessConfig;
import com.game.draco.app.goddess.config.GoddessGrade;
import com.game.draco.app.goddess.config.GoddessLevelup;
import com.game.draco.app.goddess.config.GoddessLinger;
import com.game.draco.app.goddess.config.GoddessPvpConfig;
import com.game.draco.app.goddess.config.GoddessRefresh;
import com.game.draco.app.goddess.domain.GoddessWeakTime;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.goddess.domain.RoleGoddessStatus;
import com.game.draco.app.goddess.vo.GoddessEnlistResult;
import com.game.draco.app.goddess.vo.GoddessLingerResult;
import com.game.draco.app.goddess.vo.GoddessOnBattleResult;
import com.game.draco.app.goddess.vo.GoddessPvpInfoListResult;
import com.game.draco.app.goddess.vo.GoddessUpgradeResult;
import com.game.draco.app.goddess.vo.RoleGoddessBehavior;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.GoddessBattleItem;
import com.game.draco.message.item.GoddessPvpRoleInfoItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0004_TipTitleNotifyMessage;
import com.game.draco.message.push.C1363_GoddessOnBattleNotifyMessage;
import com.game.draco.message.push.C1364_GoddessOffBattleNotifyMessage;
import com.game.draco.message.push.C1365_GoddessLevelNotifyMessage;
import com.game.draco.message.response.C1354_GoddessLingerInfoRespMessage;
import com.game.draco.message.response.C1357_GoddessPvpInfoListRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GoddessAppImpl implements GoddessApp {
	public final static byte OWN_NO = 0;
	public final static byte OWN_YES = 1;
	public final static byte OFF_BATTLE = 0;
	public final static byte ON_BATTLE = 1;
	public final static float TEN_THOUSAD_F = 10000.f;
	public final static int TEN_THOUSAD = 10000;
	public final static byte PVP_TYPE_ROB = 0;
	public final static byte PVP_TYPE_REVENGE = 1;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private GoddessConfig goddessConfig = null;
	private GoddessPvpConfig goddessPvpConfig = null;
	private List<Integer> goddessIdList = Lists.newArrayList();
	private Map<String, GoddessLevelup> goddessLevelupMap = null;
	private Map<Byte, GoddessGrade> goddessGradeMap = null;
	private Map<String, GoddessLinger> goddessLingerMap = null;
	private Map<Short, GoddessBless> goddessBlessMap = null;
	private Map<Integer, GoddessRefresh> goddessRefreshMap = null;
	
	private <T> T fromMap(Map<String, T>map, String key) {
		if(null == map) {
			return null;
		}
		return map.get(key);
	}
	
	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		this.loadGoddessConfig();
		this.loadGoddessPvpConfig();
		this.loadGoddessIdList();
		this.loadGoddessLevelup();
		this.loadGoddessGrade();
		this.loadGoddessLinger();
		this.loadGoddessBless();
		this.loadGoddessRefresh();
	}

	@Override
	public void stop() {

	}
	
	/**
	 * 加载基本配置
	 */
	private void loadGoddessConfig() {
		String fileName = XlsSheetNameType.goddess_config.getXlsName();
		String sheetName = XlsSheetNameType.goddess_config.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			goddessConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, GoddessConfig.class);
			if(null == goddessConfig) {
				Log4jManager.CHECK.error("goddessApp not config the goddessConfig,file=" 
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			goddessConfig.init();
		}catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载基本配置
	 */
	private void loadGoddessPvpConfig() {
		String fileName = XlsSheetNameType.goddess_pvp_config.getXlsName();
		String sheetName = XlsSheetNameType.goddess_pvp_config.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			goddessPvpConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, GoddessPvpConfig.class);
			if(null == goddessPvpConfig) {
				Log4jManager.CHECK.error("goddessApp not config the goddessPvpConfig,file=" 
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			
			String mapId = goddessPvpConfig.getMapId();
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
			if(null == map){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("goddessApp The map is not exist. mapId = " + mapId + ",file="
					+ sourceFile + " sheet=" + sheetName);
			}
			//将地图逻辑修改为goddess类型
			if(!map.getMapConfig().changeLogicType(MapLogicType.goddess)) {
				Log4jManager.CHECK.error("goddessApp The map logic type config error. mapId= "	+ fileName);
				Log4jManager.checkFail();
			}
		}catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 加载需要列出的女神的id
	 */
	private void loadGoddessIdList() {
		String fileName = XlsSheetNameType.goddess_list.getXlsName();
		String sheetName = XlsSheetNameType.goddess_list.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<String> list = XlsPojoUtil.sheetToStringList(sourceFile, sheetName) ;
			if(null == list) {
				Log4jManager.CHECK.error("goddessApp, not confg ids ,file=" 
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return ;
			}
			for(String str : list) {
				int goodsId = Integer.parseInt(str);
				GoodsGoddess goddess = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goodsId);
				if(null == goddess) {
					Log4jManager.CHECK.error("goddess id confg error ,goodsId=" + goodsId 
							+ " not goodsGoddess,file=" + sourceFile + " sheet=" + sheetName);
					Log4jManager.checkFail();
					continue ;
				}
				this.goddessIdList.add(goodsId);
			}
		}catch(Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
		
	}
	
	/**
	 * 女神升级配置
	 */
	private void loadGoddessLevelup() {
		String fileName = XlsSheetNameType.goddess_levelup.getXlsName();
		String sheetName = XlsSheetNameType.goddess_levelup.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<GoddessLevelup> list = XlsPojoUtil.sheetToList(sourceFile, sheetName,GoddessLevelup.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("goddessApp not config the goddess levelup,file=" 
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			
			Map<String, GoddessLevelup> levelupMap = Maps.newHashMap();
			for(GoddessLevelup levelup : list) {
				levelupMap.put(levelup.getKey(), levelup);
			}
			if(levelupMap.size() != list.size()){
				Log4jManager.CHECK.error("GoddessLevelup config error,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return ;
			}
			this.goddessLevelupMap = levelupMap;
		}catch(Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
		
	}
	
	/**
	 * 女神进化配置
	 */
	private void loadGoddessGrade() {
		String fileName = XlsSheetNameType.goddess_upgrade.getXlsName();
		String sheetName = XlsSheetNameType.goddess_upgrade.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			goddessGradeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, GoddessGrade.class);
			
			if(Util.isEmpty(goddessGradeMap)){
				Log4jManager.CHECK.error("goddessApp not config the goddess evolution,file="
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			byte maxGrade = 0;
			for(GoddessGrade gg : goddessGradeMap.values()) {
				byte grade = gg.getGrade();
				if(grade <= maxGrade) {
					continue;
				}
				maxGrade = grade;
			}
			GoddessGrade.setMaxGrade(maxGrade);
		}catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
		
	}

	/**
	 * 女神缠绵配置
	 */
	private void loadGoddessLinger() {
		String fileName = XlsSheetNameType.goddess_linger.getXlsName();
		String sheetName = XlsSheetNameType.goddess_linger.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<GoddessLinger> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, GoddessLinger.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("goddessApp not config the goddess evolution,file=" 
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			Map<String, GoddessLinger> lingerMap = Maps.newHashMap();
			for(GoddessLinger linger : list) {
				lingerMap.put(linger.getKey(), linger);
			}
			this.goddessLingerMap = lingerMap;
		}catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 女神进化祝福值配置
	 */
	private void loadGoddessBless() {
		String fileName = XlsSheetNameType.goddess_bless.getXlsName();
		String sheetName = XlsSheetNameType.goddess_bless.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			goddessBlessMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, GoddessBless.class);
			
			if(Util.isEmpty(goddessBlessMap)){
				Log4jManager.CHECK.error("goddessApp not config the goddess bless,file=" 
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
		
	}
	
	/**
	 * 女神抢夺刷新配置
	 */
	private void loadGoddessRefresh() {
		String fileName = XlsSheetNameType.goddess_refresh.getXlsName();
		String sheetName = XlsSheetNameType.goddess_refresh.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			goddessRefreshMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, GoddessRefresh.class);
			
			if(Util.isEmpty(goddessRefreshMap)){
				Log4jManager.CHECK.error("goddessApp not config the goddess refresh,file="
						+ sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void login(RoleInstance role) {
		try{
			//加载女神
			String roleId = role.getRoleId();
			List<RoleGoddess> goddesses = GameContext.getBaseDAO().selectList(RoleGoddess.class, 
					RoleGoddess.MASTER_ID, role.getRoleId());
			long now = System.currentTimeMillis();
			for(RoleGoddess goddess : goddesses) {
				if(null == goddess) {
					continue ;
				}
				this.postFromStore(goddess);
				goddess.setExistDb(true);
				this.loadGoddessWeakTime(goddess, now);
			}
			//女神装备
			this.initGoddessEquip(role);
			//女神状态
			RoleGoddessStatus record = GameContext.getGoddessStorage().getRoleGoddessStatus(roleId);
			this.loginAction(role, goddesses, record);
			
		} catch (Exception ex) {
			logger.error("goddessApp login error, roleId= " + role.getRoleId(), ex);
		}
	}
	
	/**
	 * 从ssd中初始化女神虚弱时间
	 * @param goddess
	 * @param now
	 */
	private void loadGoddessWeakTime(RoleGoddess goddess, long now) {
		GoddessWeakTime gwt = GameContext.getGoddessStorage().getGoddessWeakTime(goddess.getRoleId(),
				goddess.getGoddessId());
		if(null != gwt) {
			goddess.setWeakTime(gwt.getWeakTime());
			goddess.setLoginTime(now);
		}
	}

	@Override
	public void logout(RoleInstance role) {
		try {
			String roleId = role.getRoleId();
			//女神入库
			Map<Integer, RoleGoddess> all = GameContext.getUserGoddessApp().getAllRoleGoddess(roleId);
			if(Util.isEmpty(all)) {
				return ;
			}
			
			long now = System.currentTimeMillis();
			for(Entry<Integer, RoleGoddess> entry : all.entrySet()) {
				RoleGoddess goddess = entry.getValue();
				if(null == goddess) {
					continue;
				}
				this.saveRoleGoddess(goddess);
				this.saveGoddessWeakTime(goddess, now);
			}
			//删除女神
			GameContext.getUserGoddessApp().removeAllRoleGoddess(roleId);
			GoddessEquipBackpack pack = GameContext.getUserGoddessApp().getGoddessEquipBackpack(roleId);
			if(null != pack){
				pack.offline();
			}
			//删除女神装备
			GameContext.getUserGoddessApp().removeEquipList(roleId);
			//女神状态入ssdb
			RoleGoddessStatus record = GameContext.getUserGoddessApp().getRoleGoddessStatus(roleId);
			GameContext.getGoddessStorage().saveRoleGoddessStatus(record);
			GameContext.getUserGoddessApp().removeRoleGoddessRecord(roleId);
		}catch (Exception ex) {
			logger.error("goddess app logout error, roleId= " + role.getRoleId(), ex);
		}
		
	}
	
	/**
	 * 女神虚弱时间如ssdb
	 * @param goddess
	 * @param now
	 */
	private void saveGoddessWeakTime(RoleGoddess goddess, long now) {
		short weakTime = goddess.getWeakTime();
		if(weakTime <= 0) {
			return ;
		}
		long time = now - goddess.getLoginTime() - weakTime;
		if(time < 0) {
			time = 0;
		}
		GoddessWeakTime weakTimeRecord = new GoddessWeakTime();
		weakTimeRecord.setWeakTime((short)time);
		GameContext.getGoddessStorage().saveGoddessWeakTime(weakTimeRecord);
	}
	

	@Override
	public Result useGoddessGoods(RoleInstance role, RoleGoods roleGoods) throws ServiceException {
		try {
			Result result = new Result();
			GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(
						GoodsGoddess.class, roleGoods.getGoodsId());
			if(null == goodsGoddess) {
				result.setInfo(GameContext.getI18n().getText(
						TextId.ERROR_INPUT));
				return result;
			}
			GoodsResult gr = GameContext.getUserGoodsApp()
			.deleteForBagByInstanceId(role, roleGoods.getId(), 1,
					OutputConsumeType.goddess_goods_use);
			if(!gr.isSuccess()) {
				return gr;
			}
			return this.useGoddessTemplate(role, goodsGoddess);
		} catch (Exception ex) {
			throw new ServiceException("useGoddessGoods error",ex) ;
		}
	}
	
	private void loginAction(RoleInstance role, List<RoleGoddess> goddessList, RoleGoddessStatus record) {
		String roleId = role.getRoleId();
		if(null == record) {
			record = new RoleGoddessStatus();
			record.setRoleId(roleId);
		}
		GameContext.getUserGoddessApp().addRoleGoddessRecord(roleId, record);
		if(Util.isEmpty(goddessList)) {
			return ;
		}
		int onBattleId = record.getBattleGoddessId();
		for(RoleGoddess goddess : goddessList) {
			if(onBattleId == goddess.getGoddessId()) {
				goddess.setOnBattle(ON_BATTLE);
			}
			else {
				goddess.setOnBattle(OFF_BATTLE);
			}
			initRoleGoddessBehavior(role, goddess);
			GameContext.getUserGoddessApp().addRoleGoddess(roleId, goddess);
		}
		RoleGoddess battleGoddess = this.getOnBattleGoddes(role.getRoleId());
		if(null != battleGoddess) {
			this.reCalct(battleGoddess);
		}	
	}
	
	private void postFromStore(RoleGoddess goddess){
		//解析技能MAP
		Map<Short,Integer> map = Util.parseShortIntMap(goddess.getSkills());
		if(!Util.isEmpty(map)){
			Map<Short,RoleSkillStat> skillMap = Maps.newHashMap() ;
			for(Iterator<Map.Entry<Short, Integer>> it = map.entrySet().iterator();it.hasNext();){
				Map.Entry<Short, Integer> entry = it.next() ;
				RoleSkillStat stat = new RoleSkillStat() ;
				stat.setSkillId(entry.getKey());
				stat.setSkillLevel(entry.getValue());
				stat.setRoleId(String.valueOf(goddess.getGoddessId()));
				stat.setLastProcessTime(this.getLastProcessTimeFromStore(stat));
				skillMap.put(stat.getSkillId(), stat) ;
			}
			goddess.setSkillMap(skillMap);
		}
		this.initSkill(goddess);
	}
	
	/**
	 * 登录初始化女神装备 
	 */
	private void initGoddessEquip(RoleInstance role) {
		//女神装备
		GoddessEquipBackpack pack =  new GoddessEquipBackpack(role, ParasConstant.GODDESS_EQUIP_MAX_NUM);
		GameContext.getUserGoddessApp().initGoddessEquipBackpack(role.getRoleId(), pack);
	}
	
	private long getLastProcessTimeFromStore(RoleSkillStat stat){
		//TODO:
		return 0 ;
	}
	
	private Result useGoddessTemplate(RoleInstance role, GoodsGoddess goodsGoddess) {
		Result result = new Result();
		int goddessId = goodsGoddess.getId();
		try {
			RoleGoddess goddess = new RoleGoddess();
			goddess.setGoddessId(goddessId);
			goddess.setMasterId(role.getRoleId());
			goddess.setLevel(goodsGoddess.getStartLevel());
			goddess.setExistDb(false);
			//添加技能
			this.initSkill(goddess);
			//实时入库
			GameContext.getBaseDAO().insert(goddess);
			initRoleGoddessBehavior(role, goddess);
			GameContext.getUserGoddessApp().addRoleGoddess(role.getRoleId(), goddess);
			result.success();
		} catch (Exception e) {
			logger.error("goddessApp useGoddessTemplate error,heroId=" + goddessId + " roleId=" + role.getRoleId(),e);
			result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		}
		return result;
	}
	
	private void initSkill(RoleGoddess goddess) {
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsGoddess.class, goddess.getGoddessId());
		//普通攻击
		this.initSkill(goddess, goodsGoddess.getCommonSkill());
		//技能
		for(short skillId : goodsGoddess.getSkillIdList()) {
			this.initSkill(goddess, skillId);
		}
	}
	
	private void initSkill(RoleGoddess goddess, short skillId) {
		RoleSkillStat stat = goddess.getSkillMap().get(skillId);
		if(null != stat){
			return ;
		}
		stat = new RoleSkillStat(); 
		stat.setSkillId(skillId);
		stat.setSkillLevel(1);
		stat.setRoleId(String.valueOf(goddess.getGoddessId()));
		stat.setLastProcessTime(0);
		goddess.getSkillMap().put(skillId, stat);
	}

	@Override
	public List<Integer> getAllGoddessIds() {
		return this.goddessIdList;
	}

	@Override
	public GoodsGoddess getGoddessName(int goddessId) {
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp()
				.getGoodsTemplate(GoodsGoddess.class, goddessId);
		if(null == goodsGoddess) {
			return null;
		}
		return goodsGoddess;
	}

	@Override
	public GoddessLevelup getGoddessLevelup(int goddessId, int level) {
		String key = goddessId + Cat.underline + level;
		return this.fromMap(this.goddessLevelupMap, key);
	}
	
	@Override
	public GoddessOnBattleResult onBattle(RoleInstance role, RoleGoddess goddess, byte onBattle) {
		GoddessOnBattleResult result = new GoddessOnBattleResult();
		String roleId = role.getRoleId();
		int onBatttleId = 0;
		boolean isToOnBattle = (onBattle == ON_BATTLE);
		if(isToOnBattle) {
			if(goddess.getOnBattle() != ON_BATTLE) {
				RoleGoddess oldGoddess = GameContext.getUserGoddessApp().getOnBattleRoleGoddess(roleId);
				if(null != oldGoddess) {
					oldGoddess.setOnBattle(OFF_BATTLE);
				}
				//出战
				goddess.setOnBattle(ON_BATTLE);
				C1363_GoddessOnBattleNotifyMessage onMsg = new C1363_GoddessOnBattleNotifyMessage();
				onMsg.setRoleId(role.getIntRoleId());
				onMsg.setBattleGoddess(getGoddessBattleItem(goddess));
				this.summonNotify(role,onMsg);
				//计算属性
				this.reCalct(goddess);
				this.goddessOnOffBattleEffect(role, oldGoddess, goddess);
				onBatttleId = goddess.getGoddessId();
				//英雄情缘
				GameContext.getHeroApp().onGoddessChanged(role.getIntRoleId(), goddess.getGoddessId()
						, oldGoddess == null ? 0 : oldGoddess.getGoddessId());
			}
		}
		else {
			//召回
			if(goddess.getOnBattle() == ON_BATTLE) {
				goddess.setOnBattle(OFF_BATTLE);
				this.goddessOnOffBattleEffect(role, goddess, null);
				//地图广播
				C1364_GoddessOffBattleNotifyMessage offMsg = new C1364_GoddessOffBattleNotifyMessage();
				offMsg.setRoleId(role.getIntRoleId());
				this.summonNotify(role, offMsg);
				//英雄情缘
				GameContext.getHeroApp().onGoddessChanged(role.getIntRoleId(), 0, goddess.getGoddessId());
			}
		}
		
		//更新女神记录
		RoleGoddessStatus record = GameContext.getUserGoddessApp().getRoleGoddessStatus(role.getRoleId());
		record.setBattleGoddessId(onBatttleId);
		
		result.setOnBattleGoddess(goddess);
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goddess.getGoddessId());
		String name= (null== gb)?"":gb.getName();
		result.setInfo(GameContext.getI18n().messageFormat(TextId.Goddess_on_battle_success,name));
		result.success();
		return result;
	}
	
	private GoddessBattleItem getGoddessBattleItem(RoleGoddess goddess) {
		GoddessBattleItem item = new GoddessBattleItem();
		if(null == goddess) {
			return item;
		}
		
		item.setGoddessInstanceId(goddess.getGoddessInstanceId());
		item.setLevel(goddess.getLevel());
		GoodsGoddess gg = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goddess.getGoddessId());
		if (null == gg) {
			return item;
		}
		item.setGoddessName(goddess.getRoleName());
		item.setGoddessResId((short) gg.getResId());
		return item;
	}
	
	/**
	 * 女神出战收回角色属性改变
	 * @param oldBuffer
	 * @param newBuffer
	 */
	private void goddessOnOffBattleEffect(RoleInstance role, RoleGoddess oldGoddess, RoleGoddess newGoddess) {
		AttriBuffer oldBuffer = this.getGoddessAttriBuffer(oldGoddess);
		AttriBuffer newBuffer = this.getGoddessAttriBuffer(newGoddess);
		if(oldBuffer == null && newBuffer == null) {
			return ;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		buffer.append(oldBuffer.reverse());
		buffer.append(newBuffer);
		// 修改角色属性值
		GameContext.getUserAttributeApp().changeAttribute(role, buffer);
		role.getBehavior().notifyAttribute();
	}
	
	private void summonNotify(RoleInstance role,Message msg){
		role.getBehavior().sendMessage(msg);
		MapInstance map = role.getMapInstance() ;
		if(null == map){
			return ;
		}
		map.broadcastMap(role, msg);
	}
	
	@Override
	public GoddessGrade getGoddessGrade(byte grade) {
		return this.goddessGradeMap.get(grade);
	}
	
	@Override
	public GoddessConfig getGoddessConfig() {
		return this.goddessConfig;
	}
	
	@Override
	public GoddessUpgradeResult upgrade(RoleInstance role, RoleGoddess goddess){
		GoddessUpgradeResult result = new GoddessUpgradeResult();
		byte grade = goddess.getGrade();
		GoddessGrade gg = GameContext.getGoddessApp().getGoddessGrade(grade);
		if(goddess.getLevel() < gg.getGoddessLv()) {
			result.setInfo(GameContext.getI18n().messageFormat(TextId.Goddess_upgrade_lv_limit,
					String.valueOf(gg.getGoddessLv())));
			return result;
		}
		
		if(grade >= GoddessGrade.getMaxGrade()) {
			result.setInfo(GameContext.getI18n().getText(TextId.Goddess_reach_max_grade));
			return result ;
		}
		
	    //扣除物品
		GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, gg.getGoodsId(), gg.getGoodsNum(),
				OutputConsumeType.goddess_upgrade_consume);
		if(!gr.isSuccess()){
			result.setInfo(gr.getInfo());
			return result ;
		}
		this.upgradeByRate(goddess, result);
		//进化失败
		if(!result.isSuccess()) {
			result.setBless(goddess.getCurBless());
			return result;
		}
		//进化成功
		byte newGrade = (byte)(grade + 1);
		goddess.setGrade(newGrade);
		GoddessGrade newGg = GameContext.getGoddessApp().getGoddessGrade(newGrade);
		result.setMaxBless(newGg.getBlessMax());
		result.setCurGradeAttriAddRate(newGg.getAttriAddRate());
		//下一阶
		if(grade < GoddessGrade.getMaxGrade()) {
			GoddessGrade nextGradeGg = GameContext.getGoddessApp().getGoddessGrade((byte)(newGrade + 1));
			result.setNextGradeAttriAddRate(nextGradeGg.getAttriAddRate());
		}
		result.setGrade(newGrade);
		//计算角色属性加成
		this.goddessUpgradeEffect(role, goddess, gg.getAttriAddRate(), newGg.getAttriAddRate());
		result.success();
		return result ;
	}
	
	/**
	 * 进化对角色属性影响
	 * @param role
	 * @param goddess
	 * @param oldAddRate
	 * @param newAddRate
	 */
	private void goddessUpgradeEffect(RoleInstance role, RoleGoddess goddess
			, short oldAddRate, short newAddRate) {
		if(!this.isOnBattle(goddess)) {
			return ;
		}
		AttriBuffer buffer = this.getGoddessAttriBuffer(goddess);
		if(null == buffer) {
			return ;
		}
		float addRate = (newAddRate - oldAddRate / TEN_THOUSAD_F);
		buffer.rate(addRate * getGoddessWeakRate(goddess));
		// 修改角色属性值
		GameContext.getUserAttributeApp().changeAttribute(role, buffer);
		role.getBehavior().notifyAttribute();
	}
	
	/**
	 * 根据策划的随机算法来决定当前是成功还是失败 
	 */
	private void upgradeByRate(RoleGoddess goddess, GoddessUpgradeResult result) {
		short curBless = goddess.getCurBless();
		GoddessBless bless = this.goddessBlessMap.get(curBless);
		if(null == bless) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return ;
		}
		int rate = Util.randomInt(0, TEN_THOUSAD);
		if(rate < bless.getUpgradeRate()) {
			//升级成功
			//当前祝福值清零
			goddess.setCurBless((short)0);
			result.success();
			return ;
		}
		//失败获得祝福值
		int failBless = Util.randomInt(bless.getMinBless(), bless.getMaxBless());
		int realBless = curBless + failBless;
		int blessMax = this.goddessConfig.getBlessMax();
		if(realBless > blessMax) {
			realBless = blessMax;
		}
		//当前祝福值不能大于配置最大上限
		goddess.setCurBless((short)realBless);
		result.setInfo(GameContext.getI18n().messageFormat(TextId.Goddess_upgrade_fail, realBless - curBless));
	}

	@Override
	public GoddessLinger getGoddessLinger(int goddessId, int num) {
		String key = goddessId + Cat.underline + num;
		return this.fromMap(this.goddessLingerMap, key);
	}
	
	/**
	 * 初始化女神战斗属性 
	 */
	private void initRoleGoddessBehavior(RoleInstance role, RoleGoddess goddess) {
		if(null == goddess) {
			return ;
		}
		//设置角色对象
		goddess.setRole(role);
		//设置行为类
		goddess.setBehavior(new RoleGoddessBehavior(goddess));
	}

	@Override
	public void roleGoddessUseSkill(RoleInstance role, AbstractRole targetRole) {
		try {
			RoleGoddess goddess = this.getOnBattleGoddes(role.getRoleId());
			if(null == goddess) {
				return ;
			}
			if (null == targetRole || targetRole.isDeath()) {
				return;
			}
			//更新法宝位置
			goddess.setMapX(role.getMapX());
			goddess.setMapY(role.getMapY());
			goddess.setDir(role.getDir());
			goddess.setTarget(targetRole);
			short skillId = this.selectSkillId(goddess);
			if (skillId <= 0) {
				// 没有可用技能
				return;
			}
			//首先执行buff(法宝的buff是没有在主循环中执行的)
			GameContext.getUserBuffApp().runBuff(goddess, 0, System.currentTimeMillis());
			GameContext.getUserSkillApp().useSkill(goddess, skillId, 0);
		}catch(Exception ex){
			logger.error("roleGoddessUseSkill error",ex);
		}
		
	}
	
	private short selectSkillId(RoleGoddess goddess) {
    	Map<Short, RoleSkillStat> skillsMap = goddess.getSkillMap();
    	if(Util.isEmpty(skillsMap)){
    		return 0 ;
    	}
    	for(Entry<Short, RoleSkillStat> entry : skillsMap.entrySet()) {
    		RoleSkillStat skillStat = entry.getValue();
    		if(null == skillStat) {
    			continue;
    		}
    		Skill skill = GameContext.getSkillApp().getSkill(skillStat.getSkillId());
    		if(null == skill) {
    			continue;
    		}
    		if(!skill.isActiveSkill()) {
    			continue;
    		}
    		SkillApplyResult con = skill.condition(goddess);
    		if(SkillApplyResult.SUCCESS == con){
				return skill.getSkillId();
			}
    	}
    	return 0 ;
    }


	@Override
	public RoleGoddess getOnBattleGoddes(String roleId) {
		RoleGoddessStatus record = GameContext.getUserGoddessApp().getRoleGoddessStatus(roleId);
		if(null == record) {
			return null;
		}
		return GameContext.getUserGoddessApp().getRoleGoddess(roleId, record.getBattleGoddessId());
	}

	@Override
	public GoddessLingerResult goddessLinger(RoleInstance role, int goddessId) {
		GoddessLingerResult result = this.goddessLingerCond(role, goddessId);
		if(!result.isSuccess()) {
			return result;
		}
		//扣除消耗
		int silverMoney = result.getSilverMoney();
		if(silverMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.silverMoney, 
					OperatorType.Decrease, silverMoney, OutputConsumeType.goddess_linger_consume);
		}
		int lq = result.getLq();
		if(lq > 0) {
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.lq, 
					OperatorType.Decrease, lq, OutputConsumeType.goddess_linger_consume);
		}
		
		RoleGoddess roleGoddess = result.getRoleGoddess();
		roleGoddess.setCurLingerNum((short)(roleGoddess.getCurLingerNum() + 1));
		//缠绵加成属性修改
		this.GoddessLingerEffect(role, result.getRoleGoddess());
		return result;
	}
	
	/**
	 * 女神缠绵角色属性加成
	 */
	private void GoddessLingerEffect(RoleInstance role, RoleGoddess goddess) {
		short curLingerNum = goddess.getCurLingerNum();
		short preLingerNum = (short)(curLingerNum - 1);
		int goddessId = goddess.getGoddessId();
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		//前一次添加属性
		if(preLingerNum > 0) {
			GoddessLinger preLinger = this.getGoddessLinger(goddessId, preLingerNum);
			buffer.append(preLinger.getAttriItemList());
			buffer.reverse();
		}
		//当前次添加属性
		GoddessLinger nowLinger = this.getGoddessLinger(goddessId, curLingerNum);
		buffer.append(nowLinger.getAttriItemList());
		// 修改角色属性值
		GameContext.getUserAttributeApp().changeAttribute(role, buffer);
		role.getBehavior().notifyAttribute();
	}
	
	private GoddessLingerResult goddessLingerCond(RoleInstance role, int goddessId) {
		GoddessLingerResult result = new GoddessLingerResult();
		RoleGoddess roleGoddess = GameContext.getUserGoddessApp().getRoleGoddess(role.getRoleId(), goddessId);
		if(null == roleGoddess) {
			//提示参数错误
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		
		if(roleGoddess.getRemainLingerNum() <= 0) {
			result.setInfo(GameContext.getI18n().getText(TextId.Goddess_linger_max_num));
			return result;
		}
		//下一次缠绵消耗
		GoddessLinger linger = this.getGoddessLinger(goddessId, roleGoddess.getCurLingerNum() + 1);
		if(null == linger) {
			//提示参数错误
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		
		int silverMoney = linger.getSilverMoney();
		if(silverMoney > 0) {
			if(role.getSilverMoney() < silverMoney) {
				//提示游戏币不足
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Goddess_linger_lack_money, silverMoney));
				return result ;
			}
		}
		int lq = linger.getLq();
		if(lq > 0) {
			if(role.get(AttributeType.lq) < lq) {
				//提示灵气不足
				result.setInfo(GameContext.getI18n().messageFormat(TextId.Goddess_linger_lack_money, silverMoney));
				return result ;
			}
		}
		result.setRoleGoddess(roleGoddess);
		result.setSilverMoney(silverMoney);
		result.setLq(lq);
		result.success();
		return result;
	}
	
	private void reCalct(RoleGoddess goddess){
		try {
			GameContext.getUserAttributeApp().reCalct(goddess);
		}catch(Exception ex){
			logger.error("",ex);
		}
	}

	@Override
	public void saveRoleGoddess(RoleGoddess goddess) {
		this.preToStore(goddess);
		GameContext.getBaseDAO().update(goddess);
		//将女神虚弱时间存入ssdb
		short weakTime = goddess.getWeakTime();
		GoddessWeakTime gwt = new GoddessWeakTime();
		gwt.setRoleId(goddess.getRoleId());
		gwt.setGoddessId(goddess.getGoddessId());
		gwt.setWeakTime(weakTime);
		GameContext.getGoddessStorage().saveGoddessWeakTime(gwt);
	}
	
	private void preToStore(RoleGoddess goddess){
		//存储技能MAP
		String skillStr = GameContext.getSkillApp().skillIdLevelString(goddess.getSkillMap());
		goddess.setSkills(skillStr);
	}

	@Override
	public Message createGoddessLingerInfoMsg(RoleGoddess roleGoddess) {
		int goddessId = roleGoddess.getGoddessId();
		GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goddessId);
		if(null == goodsGoddess) {
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.Sys_Param_Error));
		}
		
		C1354_GoddessLingerInfoRespMessage respMsg = new C1354_GoddessLingerInfoRespMessage();
		respMsg.setId(goddessId);
		respMsg.setName(goodsGoddess.getName());
		byte grade = roleGoddess.getGrade();
		respMsg.setGrade(grade);
		respMsg.setLevel(roleGoddess.getLevel());
		short curLingerNum = roleGoddess.getCurLingerNum();
		respMsg.setCurLingerNum(curLingerNum);
		//每升级一次能缠绵一次
		respMsg.setMaxLingerNum((short)roleGoddess.getLevel());
		GoddessLinger gl = GameContext.getGoddessApp().getGoddessLinger(goddessId, curLingerNum);
		if(null != gl) {
			respMsg.setAttriItemList(gl.getAttriTypeValueList());
		}
		GoddessLinger nextLvGl = GameContext.getGoddessApp().getGoddessLinger(goddessId, curLingerNum + 1);
		if(null != nextLvGl) {
			respMsg.setNextLvAttriItemList(nextLvGl.getAttriTypeValueList());
		}
		respMsg.setSliverMoney(nextLvGl.getSilverMoney());
		respMsg.setLq(nextLvGl.getLq());
		respMsg.setQuality(goodsGoddess.getQualityType());
		return respMsg;
	}

	@Override
	public void addExp(RoleInstance role, int addExp) {
		if(addExp <= 0) {
			return ;
		}
		RoleGoddess onBattle = this.getOnBattleGoddes(role.getRoleId());
		if(null == onBattle) {
			return ;
		}
		int nowLevel = onBattle.getLevel();
		if(nowLevel >= role.getLevel()) {
			return ;
		}
		addExp = (int)(addExp * this.goddessConfig.getExpRate());
		if(addExp < 1) {
			addExp = 1;
		}
		
		long exp = onBattle.getExp() + addExp ;
		if(exp > Integer.MAX_VALUE){
			exp = Integer.MAX_VALUE ;
		}
		//实际添加经验
		addExp = (int)(exp - onBattle.getExp()); ;
		onBattle.setExp((int)exp);
		//获得当前等级的最大经验上限
		if(onBattle.getMaxExp() <=0){
			this.resetMaxExp(onBattle);
		}
		
		//设置女神经验变化,外部同步属性后,即可同步到客户端
		role.getBehavior().getLastStatusAtt().setGoddessExpChange(addExp);
		//升级逻辑
		this.goddessLevelUp(role, onBattle);
	}
	
	private void resetMaxExp(RoleGoddess goddess) {
		GoddessLevelup lvConfig = this.getGoddessLevelup(goddess.getGoddessId()
				, goddess.getLevel());
		if(null == lvConfig) {
			return ;
		}
		int maxExp = lvConfig.getMaxExp();
		goddess.setMaxExp(maxExp);
	}
	
	private void goddessLevelUp(RoleInstance role, RoleGoddess goddess) {
		//是否触发了升级
		boolean levelup = false ;
		while(true){
			if(goddess.getLevel() >= role.getLevel()){
				//法宝等级不能超过人物等级
				break ;
			}
			int maxExp = goddess.getMaxExp();
			if(maxExp <=0){
				//外面已经处理最大经验问题
				break ;
			}
			if(goddess.getExp() < maxExp){
				break ;
			}
			goddess.setLevel(goddess.getLevel() + 1);
			goddess.setExp(goddess.getExp() - goddess.getMaxExp()) ;
			this.resetMaxExp(goddess);
			levelup = true ;
		}
		
		if(!levelup){
			return ;
		}
		
		C1365_GoddessLevelNotifyMessage notifyMsg = new C1365_GoddessLevelNotifyMessage();
		notifyMsg.setGoddessInstanceId(goddess.getGoddessInstanceId());
		notifyMsg.setGoddessId(goddess.getGoddessId());
		notifyMsg.setLevel((byte)goddess.getLevel());
		role.getBehavior().sendMessage(notifyMsg);
		//升级重新计算属性
		this.goddessLevelUpEffect(role, goddess);
		//目标是出战的需要广播信息给同地图内玩家
		if(isOnBattle(goddess)) {
			C1363_GoddessOnBattleNotifyMessage onMsg = new C1363_GoddessOnBattleNotifyMessage();
			onMsg.setRoleId(role.getIntRoleId());
			onMsg.setBattleGoddess(getGoddessBattleItem(goddess));
			this.summonNotify(role,onMsg);
		}
	}
	
	private boolean isOnBattle(RoleGoddess goddess) {
		return (null != goddess && goddess.getOnBattle() == ON_BATTLE);
	}
	
	private void goddessLevelUpEffect(RoleInstance role, RoleGoddess goddess) {
		if(ON_BATTLE != goddess.getOnBattle()) {
			return ;
		}
		//女神本身属性改变
		GameContext.getUserAttributeApp().reCalctAndNotify(goddess);
		//对主角属性改变
		GoddessGrade curGrade = this.getGoddessGrade(goddess.getGrade());
		if(null == curGrade) {
			return ;
		}
		float rate = curGrade.getAttriAddRate() / TEN_THOUSAD_F;
		if(rate <= 0) {
			return ;
		}
		int goddessId = goddess.getGoddessId();
		int newLevel = goddess.getLevel();
		int preLevel = newLevel -1;
		GoddessLevelup preLevelup = this.getGoddessLevelup(goddessId, preLevel);
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		buffer.append(preLevelup.getAttriItemList());
		buffer.reverse();
		GoddessLevelup newLevelup = this.getGoddessLevelup(goddessId, newLevel);
		buffer.append(newLevelup.getAttriItemList());
		buffer.rate(rate * getGoddessWeakRate(goddess));
		// 修改角色属性值
		GameContext.getUserAttributeApp().changeAttribute(role, buffer);
		role.getBehavior().notifyAttribute();
	}
	
	@Override
	public float getGoddessWeakRate(RoleGoddess goddess) {
		if(goddess.getWeakTime() <= 0) {
			return 1;
		}
		float rate = this.goddessConfig.getWeakRate() / TEN_THOUSAD_F;
		if(rate == 0) {
			return 1 ;
		}
		return rate ;
	}

	@Override
	public AttriBuffer getAttriBuffer(RoleInstance role) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		//出战女神
		RoleGoddess onBattle = GameContext.getUserGoddessApp().getOnBattleRoleGoddess(role.getRoleId());
		if(null != onBattle) {
			buffer.append(this.getGoddessAttriBuffer(onBattle));
		}
		//缠绵属性
		buffer.append(this.getGoddessLingerAttriBuffer(role));
		return buffer;
	}
	
	/**
	 * 单个女神角色加成属性
	 * @param goddess
	 * @return
	 */
	private AttriBuffer getGoddessAttriBuffer(RoleGoddess goddess) {
		if(null == goddess) {
			return null;
		}
		byte grade = goddess.getGrade();
		//如果没有进阶则属性不加成给主角
		if(grade <= 0) {
			return null;
		}
		//进化
		GoddessGrade gradeConfig = this.getGoddessGrade(grade);
		if(null == gradeConfig) {
			return null;
		}
		List<AttriItem> attriItems = getGoddessAttriItem(goddess);
		if(null == attriItems) {
			return null;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		buffer.append(attriItems);
		//装备属性
		buffer.append(this.getEquipAttriBuffer(goddess.getRoleId()));
		//百分比加成到角色
		buffer.rate(gradeConfig.getAttriAddRate() / TEN_THOUSAD_F);
		return buffer;
	}
	
	private List<AttriItem> getGoddessAttriItem(RoleGoddess goddess) {
		GoddessLevelup lp = this.getGoddessLevelup(goddess.getGoddessId(), goddess.getLevel());
		if(null == lp) {
			return null;
		}
		return lp.getAttriItemList();
	}
	
	private AttriBuffer getGoddessLingerAttriBuffer(RoleInstance role) {
		Map<Integer, RoleGoddess> roleGoddessMap = GameContext.getUserGoddessApp()
					.getAllRoleGoddess(role.getRoleId());
		if(Util.isEmpty(roleGoddessMap)) {
			return null;
		}
		
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for(Entry<Integer, RoleGoddess> entry : roleGoddessMap.entrySet()) {
			RoleGoddess goddess = entry.getValue();
			if(null == goddess) {
				continue;
			}
			List<AttriItem> attriItems = this.getGoddessLingerAttriItems(goddess);
			if(Util.isEmpty(attriItems)) {
				continue;
			}
			buffer.append(attriItems);
		}
		return buffer;
	}
	
	private List<AttriItem> getGoddessLingerAttriItems(RoleGoddess goddess) {
		short curLingerNum = goddess.getCurLingerNum();
		if(curLingerNum <= 0) {
			return null;
		}
		GoddessLinger linger = this.getGoddessLinger(goddess.getGoddessId(), curLingerNum);
		if(null == linger) {
			return null;
		}
		return linger.getAttriItemList();
	}

	@Override
	public GoddessEnlistResult goddessEnlist(RoleInstance role, int goddessId) {
		GoddessEnlistResult result = this.goddessEnlistCond(role, goddessId);
		if(!result.isSuccess()) {
			return result;
		}
		result.failure();
		int goodsId = result.getGoodsId();
		short goodsNum = result.getGoodsNum();
		if(goodsId <= 0 || goodsNum <= 0) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result ;
		}
		//删除物品
		GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, goodsId,
				goodsNum, OutputConsumeType.goddess_enlist_consume);
		if(!gr.isSuccess()){
			result.setInfo(gr.getInfo());
			return result ;
		}
		//活动女神
		Result initResult = this.useGoddessTemplate(role, result.getGoddessTemplate());
		if(!initResult.isSuccess()) {
			result.setInfo(initResult.getInfo());
			return result;
		}
		result.success();
		return result;
	}
	
	private GoddessEnlistResult goddessEnlistCond(RoleInstance role, int goddessId) {
		GoddessEnlistResult result = new GoddessEnlistResult();
		//模版是否正确
		GoodsGoddess template = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goddessId);
		if(null == template) {
			//提示参数错误
			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return result ;
		}
		if(GameContext.getUserGoddessApp().isOwnGoddess(role.getRoleId(), goddessId)) {
			//提示已经拥有
			result.setInfo(GameContext.getI18n().getText(TextId.Goddess_had_owned));
			return result ;
		}
		
		//背包里面有相应女神物品则优先扣除物品
		if(GameContext.getUserGoodsApp().isExistGoodsForBag(role, goddessId)) {
			result.setGoodsId(goddessId);
			result.setGoodsNum((short)1);
		}
		else {
			result.setGoodsId(template.getGoodsId());
			result.setGoodsNum(template.getGoodsNum());
		}
		result.setGoddessTemplate(template);
		result.success();
		return result;
	}

	@Override
	public int getBattleScore(RoleGoddess goddess) {
		List<AttriItem> items = getGoddessAttriItem(goddess);
		if(Util.isEmpty(items)) {
			return 0;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		buffer.append(items);
		return GameContext.getAttriApp().getAttriBattleScore(buffer);
	}
	
	@Override
	public AttriBuffer getEquipAttriBuffer(String roleId) {
		GoddessEquipBackpack eb = GameContext.getUserGoddessApp()
						.getGoddessEquipBackpack(roleId);
		if(null == eb) {
			return null;
		}
		List<RoleGoods> equips = eb.getAllGoods();
		if(Util.isEmpty(equips)) {
			return null;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for (RoleGoods equip : equips) {
			if(null == equip || RoleGoodsHelper.isExpired(equip)) {
				continue;
			}
			buffer.append(RoleGoodsHelper.getAttriBuffer(equip));
		}
		return buffer;
	}

	@Override
	public GoddessBattleItem getOnBattleGoddessItem(RoleInstance role) {
		return this.getGoddessBattleItem(this.getOnBattleGoddes(role.getRoleId()));
	}

	@Override
	public GoddessPvpInfoListResult getPvpInfoList(RoleInstance role, byte opType) {
		GoddessPvpInfoListResult result = new GoddessPvpInfoListResult();
		List<String> roleIdList = this.getPvpRoleIdList(role, opType);
		if(Util.isEmpty(roleIdList)) {
			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return result;
		}
		
		List<AsyncPvpRoleAttr> pvpRoleAttrList = Lists.newArrayList();
		List<String> offlineRoleIds = Lists.newArrayList();
		for(String roleId : roleIdList) {
			RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if(null == targetRole) {
				offlineRoleIds.add(roleId);
				continue;
			}
			pvpRoleAttrList.add(new AsyncPvpRoleAttr(targetRole));
		}
		if(!Util.isEmpty(offlineRoleIds)) {
			pvpRoleAttrList.addAll(GameContext.getAsyncPvpApp().getAsyncPvpRoleAttrList(offlineRoleIds));
		}
		result.setPvpRoleAttrList(pvpRoleAttrList);
		result.success();
		return result;
	}
	
	private List<String> getPvpRoleIdList(RoleInstance role, byte opType) {
		int battleScore = role.getBattleScore();
		List<String> backupRoleIds = null;
		if(opType == PVP_TYPE_ROB) {
			Map<String, String> battleScoreMap = GameContext.getAsyncPvpApp()
			.getRoleBattleScores(role.getRoleId(), String.valueOf(battleScore), 10);
			if(Util.isEmpty(battleScoreMap)) {
				return null;
			}
			backupRoleIds = Lists.newArrayList();
			for(Entry<String, String> entry : battleScoreMap.entrySet()) {
				String roleId = entry.getKey();
				if(Util.isEmpty(roleId)) {
					continue;
				}
				if(roleId.equals(role.getRoleId())) {
					continue;
				}
				
				backupRoleIds.add(roleId);
			}
		}
		else {
			RoleGoddessStatus status = GameContext.getUserGoddessApp().getRoleGoddessStatus(role.getRoleId());
			if(null == status) {
				return null;
			}
			backupRoleIds = Lists.newArrayList(status.getRoberRoleIdSet());
		}
		
		if(Util.isEmpty(backupRoleIds)) {
			return null;
		}
		
		if(backupRoleIds.size() <= 5) {
			return backupRoleIds;
		}
		
		//大于5个则按随机算法取
		Set<String> realRoleIdSet = new HashSet<String>();
		int size = backupRoleIds.size();
		while(realRoleIdSet.size() < 5) {
			String roleId = backupRoleIds.get(Util.randomInt(0, size - 1));
			if(realRoleIdSet.contains(roleId)) {
				continue;
			}
			realRoleIdSet.add(roleId);
		}
		return new ArrayList<String>(realRoleIdSet);
	}

	@Override
	public void sendPvpInfoPanel(RoleInstance role, byte opType) {
		try {
			GoddessPvpInfoListResult result = this.getPvpInfoList(role, opType);
			C1357_GoddessPvpInfoListRespMessage respMsg = new C1357_GoddessPvpInfoListRespMessage();
			respMsg.setType(opType);
			respMsg.setRemainNum((byte)this.getPvpRemainNum(role, opType));
			List<AsyncPvpRoleAttr> pvpRoleAttrList = result.getPvpRoleAttrList();
			if(Util.isEmpty(pvpRoleAttrList)) {
				role.getBehavior().sendMessage(respMsg);
				return ;
			}
			List<GoddessPvpRoleInfoItem> roleInfoItems = Lists.newArrayList();
			for(AsyncPvpRoleAttr roleAttr : pvpRoleAttrList) {
				int goddessId = roleAttr.getGoddessId();
				GoodsGoddess template = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, goddessId);
				if(null == template) {
					continue;
				}
				
				GoddessPvpRoleInfoItem item =  new GoddessPvpRoleInfoItem();
				item.setRoleId(roleAttr.getRoleId());
				item.setRoleName(roleAttr.getRoleName());
				item.setBattleScore(roleAttr.getBattleScore());
				item.setGoddessId(roleAttr.getGoddessId());
				item.setGoddessName(template.getName());
				roleInfoItems.add(item);
			}
			//排序
			Comparator<GoddessPvpRoleInfoItem> comparator = new Comparator<GoddessPvpRoleInfoItem>() {
				
				@Override
				public int compare(GoddessPvpRoleInfoItem r1, GoddessPvpRoleInfoItem r2) {
					if(r1.getBattleScore() < r2.getBattleScore()) {
						return 1;
					}
					return 0;
				}
			};
			Collections.sort(roleInfoItems, comparator);
			
			respMsg.setPvpRoleInfoItemList(roleInfoItems);
			GoddessRefresh refresh = this.goddessRefreshMap.get(role.getLevel());
			if(null != refresh) {
				respMsg.setSilverMoney(refresh.getSilverMoney());
			}
			role.getBehavior().sendMessage(respMsg);
			
		} catch (Exception ex) {
			logger.error("goddessAppImpl.sendPvpInfoPanel error", ex);
		}
	}

	private int getPvpRemainNum(RoleInstance role, byte opType) {
		RoleGoddessStatus status =  GameContext.getUserGoddessApp().getRoleGoddessStatus(role.getRoleId());
		status.reset();
		if(opType == PVP_TYPE_ROB) {
			return this.goddessConfig.getRobNum() - status.getRobNum();
		} 
		
		return this.goddessConfig.getRevengeNum() - status.getRevengeNum();
		
	}
	
	private void incrPvpNum(RoleInstance role, byte opType) {
		RoleGoddessStatus status =  GameContext.getUserGoddessApp().getRoleGoddessStatus(role.getRoleId());
		status.reset();
		if(opType == PVP_TYPE_ROB) {
			status.setRobNum((byte)(status.getRobNum() + 1));
		} else {
			status.setRevengeNum((byte)(status.getRevengeNum() + 1));
		}
	}

	@Override
	public Result challenge(RoleInstance role, String targetRoleId, String targetRoleName, 
			int targetGoddssId, byte opType) {
		Result result = new Result();
		try {
			if(this.getPvpRemainNum(role, opType) <= 0) {
				return result.setInfo(GameContext.getI18n().getText(TextId.Goddess_pvp_lack_num));
			}
			AsyncPvpBattleInfo info = new AsyncPvpBattleInfo();
			info.setRoleId(role.getRoleId());
			info.setRoleName(role.getRoleName());
			info.setTargetRoleId(targetRoleId);
			info.setTargetRoleName(targetRoleName);
			info.setOpType(opType);
			info.setGoddessId(targetGoddssId);
			GoodsGoddess goodsGoddess = GameContext.getGoodsApp().getGoodsTemplate(GoodsGoddess.class, targetGoddssId);
			if(null != goodsGoddess) {
				info.setGoddessName(goodsGoddess.getName());
			}
			
			GameContext.getAsyncPvpApp().addAsyncPvpBattleInfo(info);
			//切换地图 
			Point targetPoint = new Point(goddessPvpConfig.getMapId(), goddessPvpConfig.getMapX(), goddessPvpConfig.getMapY());
			GameContext.getUserMapApp().changeMap(role, targetPoint);
			this.incrPvpNum(role, opType);
			return result.success();
		} catch (Exception ex) {
			logger.error("goddessAppImpl.challenge error", ex);
			return result;
		}
		
	}

	@Override
	public void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType type) {
		if(null == role) {
			return ;
		}
		String roleId = role.getRoleId();
		try {
			if(null == battleInfo) {
				return ;
			}
			RoleGoddessStatus status = GameContext.getUserGoddessApp().getRoleGoddessStatus(roleId);
			if(null == status) {
				return ;
			}
			//失败
			if(type == ChallengeResultType.Lose) {
				C0004_TipTitleNotifyMessage tipMsg = new C0004_TipTitleNotifyMessage();
				tipMsg.setTitle(GameContext.getI18n().getText(TextId.Goddess_pvp_title));
				byte opType = battleInfo.getOpType();
				if(opType == PVP_TYPE_ROB) {
					tipMsg.setMsgContext(GameContext.getI18n().getText(TextId.Goddess_pvp_rob_lose));
					role.getBehavior().sendMessage(tipMsg);
					return ;
				}
				tipMsg.setMsgContext(GameContext.getI18n().getText(TextId.Goddess_pvp_revenge_lose));
				role.getBehavior().sendMessage(tipMsg);
				return ;
			}
			
			this.handleAttacker(role, battleInfo, status);
			this.handleDefender(roleId, battleInfo);
			
		}catch(Exception e){
			logger.error("goddessApp.challengeOver error: ",e);
		}
	}
	
	private void handleAttacker(RoleInstance role, AsyncPvpBattleInfo info, RoleGoddessStatus status) {
		byte opType = info.getOpType();
		//更新计数
		status.updateNum(opType);
		//发奖
		List<GoodsOperateBean> goodsList = Lists.newArrayList();
		String tips = null;
		if(opType == PVP_TYPE_ROB) {
			goodsList.add(this.goddessConfig.getRobAwardGoods());
			tips = GameContext.getI18n().messageFormat(TextId.Goddess_pvp_rob_win, 
						info.getTargetRoleName(), info.getGoddessName());
		}
		else {
			goodsList.add(this.goddessConfig.getRevengeAwardGoods());
			tips = GameContext.getI18n().getText(TextId.Goddess_pvp_revenge_win);
		}
		
		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, goodsList,
				OutputConsumeType.goddess_pvp_award);
		if(!goodsResult.isSuccess()){
			//发邮件
			this.sendMail(role.getRoleId(), goodsList);
		}
		//弹板提示结果
		C0004_TipTitleNotifyMessage tipMsg = new C0004_TipTitleNotifyMessage();
		tipMsg.setTitle(tips);
		role.getBehavior().sendMessage(tipMsg);
	}
	
	private void sendMail(String roleId,List<GoodsOperateBean> goodsList){
		String title =  GameContext.getI18n().getText(TextId.Goddess_pvp_title);
		String context = title;
		OutputConsumeType ocType = OutputConsumeType.goddess_pvp_award_mail;
		GameContext.getMailApp().sendMail(roleId, title, context,
				MailSendRoleType.System.getName(), ocType.getType(), goodsList);
	}
	
	private void handleDefender(String attackerRoleId, AsyncPvpBattleInfo info) {
		if(info.getOpType() != PVP_TYPE_ROB) {
			return ;
		}
		
		String defenderRoleId = info.getTargetRoleId();
		int goddessId = info.getGoddessId();
		RoleGoddessStatus status = null;
		short weakTime = this.goddessConfig.getWeakTime();
		//在线
		boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(defenderRoleId);
		if(isOnline) {
			status = GameContext.getUserGoddessApp().getRoleGoddessStatus(defenderRoleId);
			RoleGoddess goddess = GameContext.getUserGoddessApp().getRoleGoddess(defenderRoleId, goddessId);
			if(null != goddess) {
				goddess.setWeakTime(weakTime);
			}
		}
		else {
			status = GameContext.getGoddessStorage().getRoleGoddessStatus(defenderRoleId);
		}
		
		if(null == status) {
			return;
		}
		status.getRoberRoleIdSet().add(attackerRoleId);
		if(!isOnline) {
			GameContext.getGoddessStorage().saveRoleGoddessStatus(status);
		}
		//女神虚弱时间
		GoddessWeakTime weakTimeRecord = GameContext.getGoddessStorage().getGoddessWeakTime(defenderRoleId, goddessId);
		if(null != weakTimeRecord) {
			weakTimeRecord.setWeakTime(weakTime);
			GameContext.getGoddessStorage().saveGoddessWeakTime(weakTimeRecord);
		}
	}

	@Override
	public Result refreshPvpInfoList(RoleInstance role) {
		Result result = new Result();
		GoddessRefresh refresh = this.goddessRefreshMap.get(role.getLevel());
		if(null == refresh) {
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		
		int silverMoney = refresh.getSilverMoney();
		if(silverMoney <= 0) {
			return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
		}
		
		if(role.getSilverMoney() < silverMoney) {
			//提示游戏币不足
			return result.setInfo(GameContext.getI18n()
					.messageFormat(TextId.Goddess_pvp_refresh_lack_money, silverMoney));
		}
		
		if(silverMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.silverMoney, 
					OperatorType.Decrease, silverMoney, OutputConsumeType.goddess_pvp_refresh_consume);
			role.getBehavior().notifyAttribute();
		}
		
		this.sendPvpInfoPanel(role, PVP_TYPE_ROB);
		return result.success();
	}

	@Override
	public void goddessWeakTimeOver(RoleInstance role) {
		try {
			RoleGoddess onBattle = GameContext.getGoddessApp().getOnBattleGoddes(role.getRoleId());
			if(null == onBattle) {
				return ;
			}
			if (System.currentTimeMillis() - onBattle.getLoginTime()
					- onBattle.getWeakTime() > 0) {
				return ;
			}
			//重算女神
			GameContext.getUserAttributeApp().reCalctAndNotify(onBattle);
			//重算角色
			GameContext.getUserAttributeApp().reCalctAndNotify(role);
		} catch (Exception ex) {
			logger.error("goddessApp.goddessWeakTimeOver error ", ex);
		}
	}

	@Override
	public boolean isOwnGoddess(RoleInstance role) {
		Map<Integer, RoleGoddess> all = GameContext.getUserGoddessApp()
			.getAllRoleGoddess(role.getRoleId());
		return !Util.isEmpty(all);
	}

	@Override
	public void preRoleAttrToStore(RoleInstance role, AsyncPvpRoleAttr roleAttr) {
		if(!this.isOwnGoddess(role)) {
			return ;
		}
		
		//女神id
		RoleGoddess goddess = GameContext.getGoddessApp().getOnBattleGoddes(role.getRoleId());
		if(null != goddess) {
			roleAttr.setGoddessId(goddess.getGoddessId());
			roleAttr.setGoddessLevel(goddess.getLevel());
			roleAttr.setGoddessQuality(goddess.getGrade());
		}else {
			Map<Integer, RoleGoddess> all = GameContext.getUserGoddessApp()
					.getAllRoleGoddess(role.getRoleId());
			List<Integer> goddessIdList = Lists.newArrayList(all.keySet());
			roleAttr.setGoddessId(goddessIdList.get(Util.randomInt(0, goddessIdList.size() - 1)));
		}
	}

	@Override
	public GoddessPvpConfig getGoddessPvpConfig() {
		return this.goddessPvpConfig;
	}

}
