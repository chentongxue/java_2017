package com.game.draco.app.dailyplay;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.dailyplay.config.DailyPlayReward;
import com.game.draco.app.dailyplay.config.DailyPlayRule;
import com.game.draco.app.dailyplay.domain.DailyItemData;
import com.game.draco.app.dailyplay.domain.RoleDailyPlay;
import com.game.draco.app.hint.vo.HintType;
import com.game.log.util.DateUtil;
import com.google.common.collect.Maps;

public class DailyPlayAppImpl implements DailyPlayApp,Service{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final byte HAS_RECV_REWARD = 1 ;
	private Map<String,RoleDailyPlay> roleDailyPlayMap = Maps.newConcurrentMap() ;
	private Map<String,DailyPlayRule> dailyPlayRuleMap = Maps.newLinkedHashMap() ;
	/**
	 * key: 类型_参数
	 */
	private Map<String,DailyPlayRule> typeMatchRuleMap = Maps.newHashMap() ;
	private Map<String,DailyPlayReward> dailyPlayRewardMap = Maps.newHashMap() ;
	/**
	 * key: playId_level
	 */
	private Map<String,String> roleLevelRewardMatchMap = Maps.newHashMap() ;
	
	private DailyPlayRule getDailyPlayRule(int dailyId){
		return dailyPlayRuleMap.get(String.valueOf(dailyId)) ;
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	private DailyPlayRule getDailyPlayRuleByType(int type,String ext){
		String key = type + "_" + ext ;
		return typeMatchRuleMap.get(key) ;
	}
	
	private RoleDailyPlay getRoleDailyPlay(String roleId){
		RoleDailyPlay record = roleDailyPlayMap.get(roleId);
		this.resetDay(record);
		return record ;
	}
	
	private void resetDay(RoleDailyPlay record){
		if(null == record){
			return ;
		}
		Date now = new Date() ;
		if(DateUtil.sameDay(record.getUpdateOn(), now)){
			return ;
		}
		record.setData(null);
		record.clear();
		record.setUpdateOn(now);
	}
	
	private DailyItemData getDailyItemData(String roleId,int dailyId){
		RoleDailyPlay dailyPlay = this.getRoleDailyPlay(roleId);
		if(null == dailyPlay){
			return null ;
		}
		String key = String.valueOf(dailyId) ;
		return dailyPlay.find(key) ;
	}
	
	private DailyItemData getOrCreateDailyItemData(String roleId,int dailyId){
		RoleDailyPlay dailyPlay = this.getRoleDailyPlay(roleId);
		if(null == dailyPlay){
			dailyPlay = new  RoleDailyPlay() ;
			dailyPlay.setRoleId(roleId);
			dailyPlay.setInDb(false);
			this.roleDailyPlayMap.put(roleId, dailyPlay) ;
		}
		String key = String.valueOf(dailyId) ;
		DailyItemData status = dailyPlay.find(key) ;
		if(null != status){
			return status ;
		}
		status = new DailyItemData();
		status.setDailyId(key);
		dailyPlay.put(status);
		return status ;
	}
	
	@Override
	public Collection<DailyPlayRule> getAllDailyPlayRule() {
		return dailyPlayRuleMap.values() ;
	}
	
	@Override
	public byte getStatus(DailyPlayRule daily,RoleInstance role) {
		//判断可见性
		short activeId = daily.getActiveId() ;
		Active active = GameContext.getActiveApp().getActive(activeId);
		if(null != active && !active.isDayNowActive()){
			return DailyPlayStatus.canot_show.getType();
		}
		/**
		 * 0 已经领取
		 * 1 尚未完成
		 * 2 已经完成未领取
		 */
		DailyItemData status = this.getDailyItemData(role.getRoleId(), daily.getPlayId());
		if(null == status || status.getCurr() < daily.getRequireNum() ){
			return DailyPlayStatus.un_finished.getType();
		}
		if(HAS_RECV_REWARD == status.getState()){
			return DailyPlayStatus.has_received.getType() ;
		}
		return DailyPlayStatus.can_receive.getType() ;
	}
	
	
	@Override
	public short getCompleteTimes(int dailyId,RoleInstance role){
		DailyItemData status = this.getDailyItemData(role.getRoleId(), dailyId);
		if(null == status){
			return 0 ;
		}
		return status.getCurr() ;
	}
	
	private void updateRoleDailyPlay(RoleDailyPlay roleData){
		if(null == roleData){
			return ;
		}
		this.resetDay(roleData);
		//序列化
		roleData.preToDatabase();
		if(roleData.isInDb()){
			GameContext.getBaseDAO().update(roleData);
			return ;
		}
		GameContext.getBaseDAO().insert(roleData);
		roleData.setInDb(true);
	}
	
	private RoleDailyPlay selectRoleDailyPlay(String roleId){
		RoleDailyPlay roleData = GameContext.getBaseDAO().selectEntity(RoleDailyPlay.class, 
				RoleDailyPlay.ROLE_ID, roleId) ;
		if(null == roleData){
			return null ;
		}
		this.resetDay(roleData);
		roleData.setInDb(true);
		//反序列
		roleData.postFromDatabase();
		return roleData ;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		RoleDailyPlay roleData = this.selectRoleDailyPlay(role.getRoleId()) ;
		if(null == roleData){
			return 0 ;
		}
		this.roleDailyPlayMap.put(role.getRoleId(), roleData) ;
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		RoleDailyPlay roleData = roleDailyPlayMap.remove(role.getRoleId()) ;
		this.updateRoleDailyPlay(roleData);
		return 1;
	}
	
	@Override
	public DailyPlayReward getDailyPlayReward(int dailyId,RoleInstance role){
		String key = dailyId + "_" + role.getLevel() ;
		String rewardKey = roleLevelRewardMatchMap.get(key);
		if(Util.isEmpty(rewardKey)){
			return null ;
		}
		return dailyPlayRewardMap.get(rewardKey) ;
	}
	
	@Override
	public Result recvReward(RoleInstance role,int dailyId){
		Result result = new Result() ;
		DailyPlayRule daily = this.getDailyPlayRule(dailyId);
		if(null == daily){
			result.setInfo(this.getText(TextId.ERROR_INPUT)) ;
			return result ;
		}
		//获得状态
		byte status = this.getStatus(daily, role) ;
		if(DailyPlayStatus.can_receive.getType() != status){
			result.setInfo(this.getText(TextId.DAILY_PLAY_CANOT_RECV_REWARD)) ;
			return result ;
		}
		//获得奖励
		DailyPlayReward reward = this.getDailyPlayReward(dailyId, role);
		if(null == reward){
			result.setInfo(this.getText(TextId.ERROR_DATA)) ;
			return result ;
		}
		if(!Util.isEmpty(reward.getGoodsList())){
			GoodsResult gr = GameContext.getUserGoodsApp().addGoodsBeanForBag(role,
					reward.getGoodsList(), OutputConsumeType.daily_play_reward) ;
			if(!gr.isSuccess()){
				return gr ;
			}
		}
		boolean addAttri = !Util.isEmpty(reward.getAttriMap()) ;
		if(addAttri){
			for(byte attriType : reward.getAttriMap().keySet()){
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.get(attriType), OperatorType.Add, 
						reward.getAttriMap().get(attriType), OutputConsumeType.daily_play_reward);
			}
			role.getBehavior().notifyAttribute(); 
		}
		//标识已经领奖
		DailyItemData playStatus = this.getOrCreateDailyItemData(role.getRoleId(), dailyId);
		playStatus.setState(HAS_RECV_REWARD);
		// 红点提示
		if (!this.canHavaReceive(role)) {
			GameContext.getHintApp().hintChange(role, HintType.dailyplay, false);
		}
		result.success() ;
		return result ;
	}
	
	@Override
	public void incrCompleteTimes(RoleInstance role,int times,
			DailyPlayType dailyType, String ext) {
		try {
			DailyPlayRule daily = this.getDailyPlayRuleByType(
					dailyType.getType(), ext);
			if (null == daily) {
				return;
			}
			DailyItemData status = this.getOrCreateDailyItemData(
					role.getRoleId(), daily.getPlayId());
			if (null == status) {
				return;
			}
			short requireNum = daily.getRequireNum() ;
			int currNum =  status.getCurr() + times;
			status.setCurr((short)Math.min(requireNum, currNum));
			if(currNum >= requireNum 
					&& HAS_RECV_REWARD != status.getState()){
				this.notifyComplete(role);
			}
		} catch (Exception ex) {
			logger.error("incrCompleteTimes error",ex);
		}
	}
	
	private void notifyComplete(RoleInstance role){
		if (this.canHavaReceive(role)) {
			GameContext.getHintApp().hintChange(role, HintType.dailyplay, true);
		}
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		roleDailyPlayMap.remove(roleId);
		return 1;
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadDailyPlayRules();
		this.loadDailyPlayReward();
	}

	@Override
	public void stop() {
		
	}
	

	private void loadDailyPlayRules() {
		Map<String, DailyPlayRule> map = XlsPojoUtil.loadMap(
				XlsSheetNameType.daily_play_rules, DailyPlayRule.class, true);
		if (Util.isEmpty(map)) {
			this.checkEmpty(XlsSheetNameType.daily_play_rules);
			return ;
		}
		Map<String,DailyPlayRule> typeMatch = Maps.newHashMap() ;
		for(DailyPlayRule rule : map.values()){
			typeMatch.put(rule.getPlayType() + "_" + rule.getParameter(), rule);
		}
		this.dailyPlayRuleMap = map;
		this.typeMatchRuleMap = typeMatch ;
	}

	private void loadDailyPlayReward() {
		Map<String, DailyPlayReward> map = XlsPojoUtil.loadMap(
				XlsSheetNameType.daily_play_rewards, DailyPlayReward.class,
				false);
		if (Util.isEmpty(map)) {
			this.checkEmpty(XlsSheetNameType.daily_play_rewards);
			return ;
		}
		Map<String,String> matchMap = Maps.newHashMap() ;
		for(DailyPlayReward reward : map.values()){
			reward.init();
			for(int i=reward.getMinRoleLevel();i<= reward.getMaxRoleLevel();i++){
				matchMap.put(reward.getPlayId() + "_" + i, reward.getKey()) ;
			}
		}
		this.dailyPlayRewardMap = map;
		this.roleLevelRewardMatchMap = matchMap ;
	}
	
	private void checkEmpty(XlsSheetNameType xls){
		Log4jManager.CHECK.error("not config data,file="
				+ xls.getXlsName() + " sheet=" + xls.getSheetName());
		Log4jManager.checkFail();
	}

	@Override
	public Set<HintType> getHintTypeSet(RoleInstance role) {
		if (this.canHavaReceive(role)) {
			Set<HintType> set = new HashSet<HintType>();
			set.add(HintType.dailyplay);
			return set;
		}
		return null;
	}
	
	private boolean canHavaReceive(RoleInstance role) {
		for (DailyPlayRule rule : this.getAllDailyPlayRule()) {
			if (null == rule) {
				continue;
			}
			if (DailyPlayStatus.can_receive.getType() == this.getStatus(rule, role)) {
				return true;
			}
		}
		return false;
	}

}
