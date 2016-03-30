package com.game.draco.app.operate.donate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.donate.config.DonateInfo;
import com.game.draco.app.operate.donate.config.DonateRankKey;
import com.game.draco.app.operate.donate.config.DonateRankReward;
import com.game.draco.app.operate.donate.config.DonateRule;
import com.game.draco.app.operate.donate.config.DonateScore;
import com.game.draco.app.operate.donate.config.DonateWorldReward;
import com.game.draco.app.operate.donate.domain.RoleDonate;
import com.game.draco.app.operate.donate.domain.WorldDonate;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLogData;
import com.game.draco.app.rank.domain.RankLogRoleInfo;
import com.game.draco.message.item.ActiveDonateRankItem;
import com.game.draco.message.item.RankDetailItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C2391_ActiveDonateDetailRespMessage;
import com.game.draco.message.response.C2392_ActiveDonateRankRespMessage;
import com.game.draco.message.response.C2395_ActiveDonateRankRewardRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DonateAppImpl implements DonateApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String selectAllWorldDonate = "selectAllWorldDonate";
	private Map<Integer, DonateInfo> donateInfoMap;
	private Map<Integer, DonateInfo> rankDonateInfoMap ;
	//private Map<Integer, DonateWorldReward> worldRewardMap;
	//private Map<String, DonateRankReward> rankRewardMap;
	private Map<Integer, DonateScore> scoreMap;
	//private Map<Integer, DonateRule> ruleMap;
	private Map<Integer, List<DonateRankKey>> rankKeyListMap;
	//db
	private Map<Integer, WorldDonate> worldDonateMap = Maps.newConcurrentMap() ;
	/**
	 * key: roleId
	 * value: 
	 * 		key: rankId
	 */
	private Map<String,Map<Integer,RoleDonate>> roleDonateMap = Maps.newConcurrentMap() ;
	
	@Override
	public Map<Integer, DonateInfo> getAllDonateMap() {
		return donateInfoMap;
	}
	
	@Override
	public Map<Integer, WorldDonate> getWorldDonateMap(){
		return worldDonateMap;
	}
	private DonateInfo getDonateInfoByRank(int rankId){
		return Util.fromMap(rankDonateInfoMap, rankId);
	}
	
	@Override
	public RoleDonate getRoleDonate(String roleId,int rankId){
		Map<Integer,RoleDonate> map =this.getRoleAllDonate(roleId);
		if(null == map){
			return null ;
		}
		return map.get(rankId);
	}
	
	private Map<Integer,RoleDonate> getRoleAllDonate(String roleId){
		return this.roleDonateMap.get(roleId); 
	} 
	
	private void removeRoleAllDonate(String roleId){
		this.roleDonateMap.remove(roleId);
	}
	
	private void putRoleDonate(RoleDonate donate){
		if(null == donate){
			return ;
		}
		Map<Integer,RoleDonate> map = this.getRoleAllDonate(donate.getRoleId());
		if(null == map){
			map = Maps.newHashMap() ;
			this.roleDonateMap.put(donate.getRoleId(), map);
		}
		map.put(donate.getRankId(), donate);
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		Result result = this.init() ;
		if(result.isSuccess()){
			//启动时加载全民活动数据
			this.worldDonateMap.putAll(loadWorldDonateDb());
		}
	}
	
	
	private Result init() {
		Result result = new Result();
		result.failure();

		try {
			boolean success = true;
			DonateResult donateResult = new DonateResult();
			this.loadWorldReward(donateResult);
			if (!donateResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + donateResult.getInfo());
			}

			this.loadRankReward(donateResult);
			if (!donateResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + donateResult.getInfo());
			}

			this.loadRankKey(donateResult);
			if (!donateResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + donateResult.getInfo());
			}

			this.loadScore(donateResult);
			if (!donateResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + donateResult.getInfo());
			}

			this.loadRules(donateResult);
			if (!donateResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + donateResult.getInfo());
			}

			this.loadList(donateResult);
			if (!donateResult.isSuccess()) {
				success = false;
				result.setInfo(result.getInfo() + "\n" + donateResult.getInfo());
			}
			Map<Integer, DonateInfo> rankMatchMap = Maps.newHashMap();
			for (DonateInfo info : donateResult.getDonateMap().values()) {
				DonateInfo exist = rankMatchMap.get(info.getRankId());
				if (null == exist) {
					rankMatchMap.put(info.getRankId(), info);
					continue;
				}
				success = false;
				result.setInfo(result.getInfo() + "\n" + " same rankId="
						+ info.getRankId() + " in donate:" + exist.getId()
						+ " " + info.getId());
			}
			//注册
			Result regResult = GameContext.getOperateActiveApp().registerOperateActive(
					this.getOperateActiveList(donateResult), OperateActiveType.donate);
			if(!regResult.isSuccess()){
				success = false;
				result.setInfo("DonateApp init error," + result.getInfo() + "\n" + regResult.getInfo());
			}
			if (!success) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error(result.getInfo());
				return result;
			}
			// this.worldRewardMap = donateResult.getWorldRewardMap();
			// this.ruleMap = donateResult.getRuleMap();
			this.scoreMap = donateResult.getScoreMap();
			this.donateInfoMap = donateResult.getDonateMap();
			// this.rankRewardMap = donateResult.getRankRewardMap();
			this.rankKeyListMap = donateResult.getRankKeyListMap();
			this.rankDonateInfoMap = rankMatchMap;
		} catch (Exception ex) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error(result.getInfo());
			Log4jManager.CHECK.error("donateApp init error" ,ex);
			return result ;
		}
		return result.success();
	}
	
	@Override
	public Result reLoad() {
		Result result = this.init() ;
		if(result.isSuccess()){
			this.reloadWorldDonate();
		}
		return result ;
	}
	
	private void reloadWorldDonate() {
		if(Util.isEmpty(donateInfoMap)) {
			return ;
		}
		
		for(Entry<Integer, DonateInfo> entry : donateInfoMap.entrySet()) {
			DonateInfo donateInfo = entry.getValue();
			if(null == donateInfo) {
				continue;
			}
			int activeId = donateInfo.getId();
			if(this.worldDonateMap.containsKey(activeId)) {
				continue;
			}
			
			WorldDonate worldDonate = new WorldDonate();
			worldDonate.setActiveId(activeId);
			worldDonate.setExistRecord(false);
			this.worldDonateMap.put(activeId, worldDonate);
		}
	}

	@Override
	public void stop() {
		this.saveWorldDonateDb() ;
	}
	
	private void loadWorldReward(DonateResult result){
		result.failure();
		String fileName = XlsSheetNameType.operate_worlddonate_rewards.getXlsName();
		String sheetName = XlsSheetNameType.operate_worlddonate_rewards.getSheetName();
		Map<Integer, DonateWorldReward> allRewardMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allRewardMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, DonateWorldReward.class);
		}catch (Exception ex){
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + ex);
			return ;
		}
		
		if(Util.isEmpty(allRewardMap)){
			result.setInfo("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return ;
		}
		
		for(Entry<Integer, DonateWorldReward> entry : allRewardMap.entrySet()){
			DonateWorldReward reward = entry.getValue();
			if(null == reward){
				continue;
			}
			Result initResult = reward.init();
			if(!initResult.isSuccess()){
				result.setInfo(initResult.getInfo());
				return ;
			}
		}
		
		result.setWorldRewardMap(allRewardMap);
		result.success();
	}
	
	private void loadRankReward(DonateResult result) {
		result.failure();
		String fileName = XlsSheetNameType.operate_worlddonate_rankreward.getXlsName();
		String sheetName = XlsSheetNameType.operate_worlddonate_rankreward.getSheetName();
		Map<String, DonateRankReward> allRankRewardMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allRankRewardMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, DonateRankReward.class);
		}catch (Exception ex){
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + ex);
			return ;
		}
		
		if(Util.isEmpty(allRankRewardMap)){
			result.setInfo("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return ;
		}
		
		for(Entry<String, DonateRankReward> entry : allRankRewardMap.entrySet()){
			DonateRankReward reward = entry.getValue();
			if(null == reward){
				continue;
			}
			Result initResult = reward.init();
			if(!initResult.isSuccess()){
				result.setInfo(initResult.getInfo());
				return ;
			}
		}
		
		result.setRankRewardMap(allRankRewardMap);
		result.success();
	}
	
	private void loadRankKey(DonateResult result) {
		result.failure();
		String fileName = XlsSheetNameType.operate_worlddonate_rankkey.getXlsName();
		String sheetName = XlsSheetNameType.operate_worlddonate_rankkey.getSheetName();
		List<DonateRankKey> xlsRankKeyList = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			xlsRankKeyList = XlsPojoUtil.sheetToList(sourceFile, sheetName, DonateRankKey.class);
		}catch (Exception ex){
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + ex);
			return ;
		}
		
		if(Util.isEmpty(xlsRankKeyList)){
			result.setInfo("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return ;
		}
		Map<Integer, List<DonateRankKey>> rankKeyListMap = new HashMap<Integer, List<DonateRankKey>>();
		for(DonateRankKey rankKey : xlsRankKeyList) {
			int rankId = rankKey.getRankId();
			List<DonateRankKey> rankKeyList = rankKeyListMap.get(rankId);
			if(null == rankKeyList) {
				rankKeyList = new ArrayList<DonateRankKey>();
				rankKeyListMap.put(rankId, rankKeyList);
			}
			Result initResult = rankKey.init(result);
			if(!initResult.isSuccess()){
				result.setInfo(initResult.getInfo());
				return ;
			}
			rankKeyList.add(rankKey);
		}
		
		result.setRankKeyListMap(rankKeyListMap);
		result.success();
	}
	
	private void loadScore(DonateResult result){
		result.failure();
		String fileName = XlsSheetNameType.operate_worlddonate_score.getXlsName();
		String sheetName = XlsSheetNameType.operate_worlddonate_score.getSheetName();
		Map<Integer, DonateScore> allScoreMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allScoreMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, DonateScore.class);
		} catch (Exception ex){
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + ex);
			return ;
		}
		
		if(Util.isEmpty(allScoreMap)){
			result.setInfo("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return ;
		}
		
		for(Entry<Integer, DonateScore> entry : allScoreMap.entrySet()){
			DonateScore score = entry.getValue();
			if(null == score){
				continue; 
			}
			Result initResult = score.init();
			if(!initResult.isSuccess()){
				result.setInfo(initResult.getInfo());
				return ;
			}
		}
		
		result.setScoreMap(allScoreMap);
		result.success();
	}
	
	private void loadRules(DonateResult result){
		result.failure();
		String fileName = XlsSheetNameType.operate_worlddonate_rules.getXlsName();
		String sheetName = XlsSheetNameType.operate_worlddonate_rules.getSheetName();
		Map<Integer, DonateRule> allRuleMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allRuleMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, DonateRule.class);
		} catch (Exception ex){
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + ex);
			return ;
		}
		
		if(Util.isEmpty(allRuleMap)){
			result.setInfo("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return ;
		}
		//检测配置的奖励是否都有效
		for(Entry<Integer, DonateRule> entry : allRuleMap.entrySet()){
			DonateRule rule = entry.getValue();
			Map<Integer, DonateWorldReward> allRewardMap = result.getWorldRewardMap();
			Result initResult = rule.init(allRewardMap);
			if(!initResult.isSuccess()){
				result.setInfo(initResult.getInfo());
				return ;
			}
		}
		result.setRuleMap(allRuleMap);
		result.success();
	}
	
	private void loadList(DonateResult result){
		result.failure();
		String fileName = XlsSheetNameType.operate_worlddonate_list.getXlsName();
		String sheetName = XlsSheetNameType.operate_worlddonate_list.getSheetName();
		Map<Integer, DonateInfo> donateInfoMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			donateInfoMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, DonateInfo.class);
		}catch (Exception ex){
			result.setInfo("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + ", " + ex);
			return ;
		}
		if(Util.isEmpty(donateInfoMap)){
			result.setInfo("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return ;
		}
		
		for(Entry<Integer, DonateInfo> entry : donateInfoMap.entrySet()){
			DonateInfo donateInfo = entry.getValue();
			if(null == donateInfo){
				continue;
			}
			Result initResult = donateInfo.init(result);
			if(!initResult.isSuccess()){
				result.setInfo(initResult.getInfo());
				return ;
			}
			//验证规则的有效性
			Map<Integer, DonateRule> allRuleMap = result.getRuleMap();
			int ruleId = donateInfo.getRuleId();
			DonateRule rule = allRuleMap.get(ruleId);
			if(null == rule) {
				result.setInfo("WorldDonateInfo id = "+donateInfo.getId() +", ruleId ="+ruleId + ", do not exsit");
				return ;
			}
			donateInfo.setDonateRule(rule);
		}
		
		result.success();
		result.setDonateMap(donateInfoMap);
	}
	
	@Override
	public int onLogin(RoleInstance role,Object context) {
		try{
			List<RoleDonate> roleDonateList = GameContext.getBaseDAO().selectList(RoleDonate.class, "roleId", role.getRoleId());
			if(Util.isEmpty(roleDonateList)){
				return 1;
			}
			for(RoleDonate donate : roleDonateList){
				if(null == donate){
					continue;
				}
				donate.setExistRecord(true);
				this.putRoleDonate(donate);
			}
			return 1 ;
		}catch(Exception e){
			this.logger.error("DonateApp.loadRoleDonate error: ", e);
		}
		return 0 ;
	}
	
	private RoleDonate newRoleDonate(String roleId,int rankId,int count){
		RoleDonate donate = new RoleDonate();
		donate.setRoleId(roleId);
		donate.setCurCount(count);
		donate.setRankId(rankId);
		donate.setChanged(true);
		return donate ;
	}
	
	public Map<Integer, WorldDonate> loadWorldDonateDb() {
		Map<Integer, WorldDonate> worldDonateMap = new HashMap<Integer, WorldDonate>();
		List<WorldDonate> rwDbInfoList = GameContext.getBaseDAO().selectList(WorldDonate.class, selectAllWorldDonate);
		if(!Util.isEmpty(rwDbInfoList)){
			for(WorldDonate rwDbInfo : rwDbInfoList) {
				rwDbInfo.setExistRecord(true);
				rwDbInfo.init();
				worldDonateMap.put(rwDbInfo.getActiveId(), rwDbInfo);
			}
		}
		for(Entry<Integer, DonateInfo> entry : donateInfoMap.entrySet()) {
			int id = entry.getKey();
			if(worldDonateMap.containsKey(id)) {
				continue;
			}
			WorldDonate worldDonate = new WorldDonate();
			worldDonate.setActiveId(id);
			worldDonate.init();
			worldDonateMap.put(id, worldDonate);
		}
		return worldDonateMap;
	}
	
	@Override
	public void saveWorldDonateDb() {
		if(Util.isEmpty(worldDonateMap)) {
			return ;
		}
		for(Entry<Integer, WorldDonate> entry : worldDonateMap.entrySet()){
			WorldDonate wdDbInfo = entry.getValue();
			if(null == wdDbInfo){
				continue;
			}
			if(!wdDbInfo.isChanged()) {
				continue;
			}
			if(wdDbInfo.isExistRecord()){
				GameContext.getBaseDAO().update(wdDbInfo);
			}else {
				GameContext.getBaseDAO().insert(wdDbInfo);
				wdDbInfo.setExistRecord(true);
			}
		}
	}

	@Override
	public int onLogout(RoleInstance role,Object context) {
		try {
			Map<Integer, RoleDonate> roleData = this.getRoleAllDonate(role.getRoleId()) ;
			if(Util.isEmpty(roleData)){
				return 1;
			}
			for(Entry<Integer, RoleDonate> entry : roleData.entrySet()){
				RoleDonate rankDbInfo = entry.getValue();
				if(null == rankDbInfo){
					continue;
				}
				if(!rankDbInfo.isChanged()){
					continue ;
				}
				if(rankDbInfo.isExistRecord()){
					GameContext.getBaseDAO().update(rankDbInfo);
				}
				else{
					GameContext.getBaseDAO().insert(rankDbInfo);
					rankDbInfo.setExistRecord(true);
				}
			}
			return 1 ;
		}catch(Exception ex){
			logger.error("donateApp offline error",ex);
		}
		return 0 ;
	}
	

	@Override
	public DonateResult getRankRewardStat(RoleInstance role, DonateInfo donateInfo) {
		DonateResult result = new DonateResult();
		//如果不是活动排行榜
		if(null == donateInfo){
			result.setRewardState(REWARD_STATE_ERROR);
			return result;
		}
		int rankId = donateInfo.getRankId();
		RoleDonate rankDbInfo = this.getRoleDonate(role.getRoleId(), rankId) ;
		if(null != rankDbInfo && rankDbInfo.getReward() == RoleDonate.REWARDED_YES){
			result.setRewardState(REWARD_STATE_REWARDED);
			return result;
		}
		RankLogRoleInfo roleInfo = GameContext.getRankApp().getRoleRank(rankId, role.getRoleId());
		if(null == roleInfo || roleInfo.getRank() == 0){
			result.setRewardState(REWARD_STATE_NO);
			return result;
		}
		DonateRankReward reward = this.getRankReward(rankId, roleInfo.getRank());
		//当前排名没有奖项
		if(null == reward){
			result.setRewardState(REWARD_STATE_NO);
			return result;
		}
		result.setRankReward(reward);
		if(donateInfo.isInRewardDate()){
			result.setRewardState(REWARD_STATE_ENABLE);
			return result;
		}
		result.setRewardState(REWARD_STATE_DISABLE);
		return result;
	}
	
	@Override
	public byte getWorldAwardStat(RoleInstance role, DonateInfo donateInfo) {
		int rankId = donateInfo.getRankId();
		RoleDonate roleDonate = this.getRoleDonate(role.getRoleId(), rankId);
		//未参加活动则不能领取
		if(null == roleDonate) {
			return REWARD_STATE_NO;
		}
		DonateRule rule = donateInfo.getDonateRule();
		Map<Integer, DonateWorldReward> condAwardMap = rule.getCondAwardMap();
		int curCount = worldDonateMap.get(donateInfo.getId()).getCurCount();
		for(Entry<Integer, DonateWorldReward> entry : condAwardMap.entrySet()) {
			int condValue = entry.getKey();
			if(rule.getCondReward(condValue, curCount, roleDonate) == REWARD_STATE_ENABLE){
				return REWARD_STATE_ENABLE;
			}
		}
		return REWARD_STATE_NO;
	}
	

	private void realTimeWriteDB(RoleDonate roleDonate) {
		if(roleDonate.isExistRecord()){
			GameContext.getBaseDAO().update(roleDonate);
			return ;
		}
		GameContext.getBaseDAO().insert(roleDonate);
		roleDonate.setExistRecord(true);
	}

	private final static int PAGE_LEN = 6;
	private final static int PAGE_HALF_LEN = PAGE_LEN / 2;
	@Override
	public Message createRankDetailMsg(RoleInstance role, int activeId) {
		DonateInfo wdInfo = this.getAllDonateMap().get(activeId);
		if(null == wdInfo || wdInfo.isOutDate()){
			return null;
		}
		//没有关联的排行榜
		RankInfo rankInfo = wdInfo.getRankInfo();
		if(null == rankInfo){
			return null;
		}
		int rankId = wdInfo.getRankId();
		C2392_ActiveDonateRankRespMessage respMsg = new C2392_ActiveDonateRankRespMessage();
		respMsg.setRankId(rankId);
		respMsg.setName(rankInfo.getName());
		List<RankDetailItem> rdItemList = getRank2PageData(rankId, 0);
		if(Util.isEmpty(rdItemList)){
			return respMsg;
		}
		
		respMsg.setState(this.getRankRewardStat(role, wdInfo).getRewardState());
		List<ActiveDonateRankItem> rankItemList = new ArrayList<ActiveDonateRankItem>();
		//取第1,2页
		for(RankDetailItem rdItem : rdItemList) {
			rankItemList.add(this.getActiveDonateRankItem(rankId, rdItem));
		}
		
		//取自己排名
		String roleId = role.getRoleId();
		RankLogRoleInfo rlRoleInfo = GameContext.getRankApp().getRoleRank(rankId, roleId);
		if(null == rlRoleInfo) {
			respMsg.setRankItemList(rankItemList);
			return respMsg;
		}
		
		short selfRank = rlRoleInfo.getRank();
		//自己未上榜或在12名以内
		if(selfRank <= 0 || selfRank <= PAGE_LEN * 2) {
			respMsg.setRankItemList(rankItemList);
			return respMsg;
		}
		
		int page = ((selfRank-1) / PAGE_LEN);
		RankLogData selfPageLogData = GameContext.getRankApp().getPageData(rankId, page);
		//取排行数据异常
		if(null == selfPageLogData || Util.isEmpty(selfPageLogData.getRdItemList())){
			respMsg.setRankItemList(rankItemList);
			return respMsg;
		}
		List<RankDetailItem> selfPageItemList = selfPageLogData.getRdItemList();
		//如果是在第3页
		if(page == 2) {
			int curPageRank = (selfRank-1) % PAGE_LEN;
			if(curPageRank >= PAGE_HALF_LEN) {
				for(int i= curPageRank - 3; i <= curPageRank; i++) {
					RankDetailItem rdItem = selfPageItemList.get(i);
					rankItemList.add(this.getActiveDonateRankItem(rankId, rdItem));
				}
				respMsg.setRankItemList(rankItemList);
				return respMsg;
			}
			else {
				for(int i=0; i<=curPageRank; i++) {
					RankDetailItem rdItem = selfPageItemList.get(i);
					rankItemList.add(this.getActiveDonateRankItem(rankId, rdItem));
				}
				respMsg.setRankItemList(rankItemList);
				return respMsg;
			}
		}
		else {
			rdItemList = getRank2PageData(rankId, page-1);
			if(Util.isEmpty(rdItemList)){
				return respMsg;
			}
			for(RankDetailItem rdItem : rdItemList) {
				int rank = rdItem.getRank();
				if(!(rank == selfRank || rank == selfRank - 1
						|| rank == selfRank - 2 || rank == selfRank - 3)){
					continue;
				}
				
				rankItemList.add(this.getActiveDonateRankItem(rankId, rdItem));
			}
		}
		respMsg.setRankItemList(rankItemList);
		return respMsg;
		
	}
	
	/**
	 * 取排行榜第1，2页数据
	 * @param rankId
	 * @return
	 */
	private List<RankDetailItem> getRank2PageData(int rankId, int startPage) {
		List<RankDetailItem> itemList = new ArrayList<RankDetailItem>();
		RankLogData rankLogData = GameContext.getRankApp().getPageData(rankId, startPage);
		List<RankDetailItem> rdItemList = null;
		if(null != rankLogData) {
			rdItemList = rankLogData.getRdItemList();
			if(!Util.isEmpty(rdItemList)) {
				itemList.addAll(rdItemList);
			}
		}
		//如果只有一页
		if(rankLogData.getTotalPage() <= 1) {
			return itemList;
		}
		rankLogData = GameContext.getRankApp().getPageData(rankId, startPage + 1);
		rdItemList = rankLogData.getRdItemList();
		if(null == rankLogData || Util.isEmpty(rdItemList)) {
			return itemList;
		}
		itemList.addAll(rankLogData.getRdItemList());
		return itemList;
	}
	
	private ActiveDonateRankItem getActiveDonateRankItem(int rankId, RankDetailItem rdItem) {
		ActiveDonateRankItem donateRankItem = new ActiveDonateRankItem();
		donateRankItem.setRank(rdItem.getRank());
		donateRankItem.setRoleId(Integer.valueOf(rdItem.getKey()));
		donateRankItem.setCount(Integer.valueOf(rdItem.getData4()));
		donateRankItem.setRoleName(rdItem.getData1());
		donateRankItem.setCamp(rdItem.getData3());
		DonateRankReward reward = this.getRankReward(rankId, rdItem.getRank());
		if(null != reward) {
			donateRankItem.setAwardGoodsList(reward.getGoodsLiteList());
		}
		return donateRankItem;
	}
	
	private DonateRankReward getRankReward(int rankId, int rank) {
		List<DonateRankKey> rankKeyList = rankKeyListMap.get(rankId);
		if(Util.isEmpty(rankKeyList)) {
			return null;
		}
		for(DonateRankKey rankKey : rankKeyList) {
			if(null == rankKey){
				continue;
			}
			if(rankKey.getRankStart() > rank || rankKey.getRankEnd() < rank){
				continue;
			}
			return rankKey.getRankReward();
		}
		return null;
	}

	@Override
	public Message createDonateDetailMsg(RoleInstance role, int activeId) {
		DonateInfo wdInfo = donateInfoMap.get(activeId);
		if(null == wdInfo){
			return null;
		}
		if(wdInfo.isOutDate()) {
			return null;
		}
		C2391_ActiveDonateDetailRespMessage respMsg = new C2391_ActiveDonateDetailRespMessage();
		respMsg.setActiveId(activeId);
		respMsg.setType((byte)1);
		respMsg.setTitle(wdInfo.getTitle());
		Date now = new Date();
		respMsg.setTimeToAward(DateUtil.dateDiffSecond(now, wdInfo.getStatEndDate()));
		respMsg.setTimeToAwardEnd(DateUtil.dateDiffSecond(now, wdInfo.getRewardEndDate()));
		respMsg.setRuleDesc(wdInfo.getRuleDesc());
		//设置排行榜奖励状态
		if(wdInfo.isInRewardDate() && 
				getRankRewardStat(role, wdInfo).getRewardState()== REWARD_STATE_ENABLE){
			respMsg.setRankRewardstate((byte)1);
		}
		int rankId = wdInfo.getRankId();
		respMsg.setRankId(rankId);
		int curCount = worldDonateMap.get(activeId).getCurCount();
		respMsg.setCurCount(curCount);
		//档位信息
		DonateRule rule = wdInfo.getDonateRule();
		respMsg.setDonateRuleItemList(rule.getDonateRuleItemList(curCount,
				this.getRoleDonate(role.getRoleId(), rankId)));
		respMsg.setDonateGoodsList(wdInfo.getDonateGoodsLiteNamedList());
		return respMsg;
	}
	
	@Override
	public boolean isOpen(int activeId){
		DonateInfo wdInfo = this.donateInfoMap.get(activeId);
		if(null == wdInfo) {
			return false ;
		}
		if(wdInfo.isOutDate()) {
			return false ;
		}
		return true ;
	}
	
	@Override
	public Result donate(RoleInstance role,int activeId){
		Result result = new Result(); 
		result.failure() ;
		
		DonateInfo wdInfo = this.donateInfoMap.get(activeId);
		if(null == wdInfo) {
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		if(!wdInfo.isInStatDate()) {
			result.setInfo(this.getText(TextId.Donate_Goods_Stat_TimeOver));
			return result ;
		}
		
		Map<Integer, Integer> delMap = new HashMap<Integer, Integer>();
		List<Integer> donateGoodsList = wdInfo.getDonateGoodsList();
		int totalScore = 0;
		for(int goodsId : donateGoodsList) {
			DonateScore donateScore = scoreMap.get(goodsId);
			if(null == donateScore) {
				continue;
			}
			int num = role.getRoleBackpack().countByGoodsId(goodsId);
			if(num <= 0) {
				continue;
			}
			delMap.put(goodsId, num);
			totalScore += donateScore.getScore() * num;
		}
		//背包中没有捐献的物品
		if(Util.isEmpty(delMap)) {
			result.setInfo(this.getText(TextId.Donate_Goods_No_Goods));
			return result;
		}
		
		//删除玩家物品
		GoodsResult goodsResult = GameContext.getUserGoodsApp()
			.deleteForBagByMap(role, delMap, OutputConsumeType.donate_goods_donate);
		//失败
		if(!goodsResult.isSuccess()) {
			return goodsResult ;
		}
		//成功
		this.incrRoleDonateCount(role, activeId, totalScore);
		this.incrWorldDonateCount(activeId, totalScore);
		//更新排行榜		
		GameContext.getRankApp().updateDonate(role, wdInfo.getRankId());
		String info = GameContext.getI18n().messageFormat(TextId.Donate_goods_TIPS, totalScore);
		result.success();
		result.setInfo(info);
		return result ;
	}
	
	
	private void incrRoleDonateCount(RoleInstance role,int activeId,int value) {
		DonateInfo wdInfo = this.donateInfoMap.get(activeId);
		if(null == wdInfo) {
			return ;
		}
		int rankId = wdInfo.getRankId();
		RoleDonate roleDonate = this.getRoleDonate(role.getRoleId(), rankId);
		if(null == roleDonate){
			roleDonate = this.newRoleDonate(role.getRoleId(), rankId, value) ;
			this.putRoleDonate(roleDonate);
			return ;
		}
		roleDonate.setCurCount(roleDonate.getCurCount() + value);
		roleDonate.setChanged(true);
	}
	
	
	private void incrWorldDonateCount(int activeId, int value) {
		DonateInfo wdInfo = this.donateInfoMap.get(activeId);
		if(null == wdInfo) {
			return ;
		}
		WorldDonate worldDonate = worldDonateMap.get(activeId);
		if(null == worldDonate) {
			return ;
		}
		if(!wdInfo.isInStatDate()) {
			return ;
		}
		int totalScore = worldDonate.addAndGetCount(value);
		worldDonate.setChanged(true);
		Log4jManager.DONATE_SCORE_LOG.info(activeId + "#" + value + "#" + totalScore);
	}
	

	@Override
	public Message recvWorldReward(RoleInstance role, int activeId,
			int condValue) {
		DonateInfo wdInfo = donateInfoMap.get(activeId);
		if(null == wdInfo) {
			return null;
		}
		if(wdInfo.isOutDate()) {
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(this.getText(TextId.Donate_Out_Date));
			return tipMsg;
		}
		RoleDonate roleDonate = this.getRoleDonate(role.getRoleId(), wdInfo.getRankId()) ;
		if(null == roleDonate) {
			return null;
		}
		DonateRule rule = wdInfo.getDonateRule();
		byte state = rule.getCondReward(condValue
				, worldDonateMap.get(activeId).getCurCount(), roleDonate);
		if(state != REWARD_STATE_ENABLE) {
			return null;
		}
		//领奖
		DonateWorldReward reward = rule.getCondAwardMap().get(condValue);
		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, reward.getGoodsList(),
				OutputConsumeType.donate_world_reward);
		if(!goodsResult.isSuccess()){
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(goodsResult.getInfo());
			return tipMsg ;
		}
		int bindingGoldMoney = reward.getBindMoney();
		int silverMoney = reward.getSilverMoney();
		int goldMoney = reward.getGoldMoney();
		if (goldMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.goldMoney, OperatorType.Add,
					goldMoney, OutputConsumeType.donate_world_reward);
		}
		if (silverMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.gameMoney, OperatorType.Add, silverMoney,
					OutputConsumeType.donate_world_reward);
		}
		if (bindingGoldMoney > 0 || silverMoney > 0 || goldMoney > 0) {
			role.getBehavior().notifyAttribute();
		}
		//更新roleDonate
		rule.updateRoleDonateWorldAward(condValue, roleDonate);
		
		//实时入库
		try{
			this.realTimeWriteDB(roleDonate);
		}catch (Exception e){
			logger.error("roleDonate realTime write db", e);
		}
		
		//更新提示效果
		if (!this.canRecvReward(role)) {
			GameContext.getHintApp().hintChange(role, HintType.operate, false);
		}
		return createDonateDetailMsg(role, activeId);
	}
	
	/**
	 * 修改数据库中排名奖领奖字段
	 */
	private void updateRoleDonateRankAward(RoleDonate roleDonate) {
		roleDonate.setReward(RoleDonate.REWARDED_YES);
	}

	@Override
	public Message recvRankReward(RoleInstance role, int activeId) {
		DonateInfo wdInfo = donateInfoMap.get(activeId);
		if(null == wdInfo) {
			return null;
		}
		if(wdInfo.isOutDate()) {
			C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
			tipMsg.setMsgContext(this.getText(TextId.Donate_Out_Date));
			return tipMsg;
		}
		RoleDonate roleDonate = this.getRoleDonate(role.getRoleId(), wdInfo.getRankId());
		if(null == roleDonate) {
			return null;
		}
		C2395_ActiveDonateRankRewardRespMessage respMsg = new C2395_ActiveDonateRankRewardRespMessage();
		DonateResult result = this.getRankRewardStat(role, wdInfo);
		byte state = result.getRewardState();
		if(state == REWARD_STATE_ERROR) {
			respMsg.setResult(Result.FAIL);
			respMsg.setInfo(this.getText(TextId.Sys_Error));
			return respMsg;
		}
		
		if(state == REWARD_STATE_DISABLE) {
			respMsg.setResult(Result.FAIL);
			respMsg.setInfo(this.getText(TextId.Donate_Rank_Reward_Time_No));
			return respMsg;
		}
		
		if(state == REWARD_STATE_NO) {
			respMsg.setResult(Result.FAIL);
			respMsg.setInfo(this.getText(TextId.Donate_Rank_Reward_No));
			return respMsg;
		}
		
		if(state == REWARD_STATE_REWARDED) {
			respMsg.setResult(Result.FAIL);
			respMsg.setInfo(this.getText(TextId.Donate_Rank_Rewarded));
			return respMsg;
		}
		//领奖
		DonateRankReward reward = result.getRankReward();
		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, reward.getGoodsList(),
				OutputConsumeType.donate_rank_reward);
		if(!goodsResult.isSuccess()){
			respMsg.setInfo(goodsResult.getInfo());
			respMsg.setResult(Result.FAIL);
			return respMsg ;
		}
		
		int bindingGoldMoney = reward.getBindMoney();
		int silverMoney = reward.getSilverMoney();
		int goldMoney = reward.getGoldMoney();
		if (goldMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.goldMoney, OperatorType.Add,
					goldMoney, OutputConsumeType.donate_rank_reward);
		}
		if (silverMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.gameMoney, OperatorType.Add, silverMoney,
					OutputConsumeType.donate_rank_reward);
		}
		if (bindingGoldMoney > 0 || silverMoney > 0 || goldMoney > 0) {
			role.getBehavior().notifyAttribute();
		}
		
		updateRoleDonateRankAward(roleDonate);
		//实时入库
		try{
			this.realTimeWriteDB(roleDonate);
		}catch (Exception e){
			logger.error("roleDonate realTime write db", e);
		}
		//更新提示效果
		if (!GameContext.getOperateActiveApp().hasHint(role)) {
			GameContext.getHintApp().hintChange(role, HintType.operate, false);
		}
		respMsg.setActiveId(activeId);
		respMsg.setResult(Result.SUCCESS);
		respMsg.setRewardState(REWARD_STATE_REWARDED);
		return respMsg;
	}

	@Override
	public RoleDonate getRoleDonate(RoleInstance role,int rankId) {
		RoleDonate roleDonate = this.getRoleDonate(role.getRoleId(), rankId);
		if(null == roleDonate){
			roleDonate = new RoleDonate();
			roleDonate.setRankId(rankId);
			roleDonate.setRoleId(role.getRoleId());
			roleDonate.setReward(RoleDonate.REWARDED_NO);
			roleDonate.setExistRecord(false);
			this.putRoleDonate(roleDonate);
		}
		return roleDonate;
	}
	
	public int getEffectData4Rank(RoleDonate roleDonate){
		if(null == roleDonate){
			return 0 ;
		}
		DonateInfo info = this.getDonateInfoByRank(roleDonate.getRankId());
		if(null == info){
			return 0 ;
		}
		if(roleDonate.getCurCount() < info.getIntoRankMinScore()){
			return 0 ;
		}
		return roleDonate.getCurCount() ;
	}
	
	/**
	 * 是否有奖励
	 * @param role
	 * @return
	 */
	@Override
	public boolean canRecvReward(RoleInstance role) {
		if (Util.isEmpty(donateInfoMap)) {
			return false;
		}

		for (Entry<Integer, DonateInfo> entry : donateInfoMap.entrySet()) {
			DonateInfo wdInfo = entry.getValue();
			if (wdInfo.isOutDate()) {
				continue;
			}
			if (getWorldAwardStat(role, wdInfo) != DonateApp.REWARD_STATE_ENABLE
					&& getRankRewardStat(role, wdInfo).getRewardState() != DonateApp.REWARD_STATE_ENABLE) {
				continue;
			}
			return true;
		}
		return false;
	}

	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		this.removeRoleAllDonate(roleId);
		return 1 ;
	}

	private List<OperateActive> getOperateActiveList(DonateResult donateResult) {
		List<OperateActive> list = Lists.newArrayList() ;
		if(null == donateResult.getDonateMap()){
			return list ;
		}
		for(DonateInfo info : donateResult.getDonateMap().values()){
			DonateActive active = new DonateActive(info);
			list.add(active);
		}
		return list;
	}
	
}
