package com.game.draco.app.hero.arena;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.arena.config.HeroArenaBaseConfig;
import com.game.draco.app.hero.arena.config.HeroArenaMatchRuleConfig;
import com.game.draco.app.hero.arena.config.HeroArenaRewardConfig;
import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.mail.domain.MailAttriBean;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.HeroArenaHeroItem;
import com.game.draco.message.push.C1273_HeroArenaResultNotifyMessage;
import com.game.draco.message.response.C1270_HeroArenaPanelRespMessage;
import com.google.common.collect.Maps;

public class HeroArenaAppImpl implements HeroArenaApp {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Integer,List<HeroArenaRewardConfig>> rewardMap = Maps.newHashMap();
	private TreeMap<Integer,HeroArenaMatchRuleConfig> matchRuleMap = Maps.newTreeMap();
	private HeroArenaBaseConfig baseConfig;
	private Map<String,RoleHeroArenaRecord> roleHeroArenaRecordMap = Maps.newHashMap();
	private static final int Three = 3;
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadHeroArenaConfig();
	}

	@Override
	public void stop() {
		
	}
	
	private void loadHeroArenaConfig(){
		//加载基本配置
		String fileName = XlsSheetNameType.hero_arena_base.getXlsName();
		String sheetName = XlsSheetNameType.hero_arena_base.getSheetName();
		String info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
		try {
			String xlsPath = GameContext.getPathConfig().getXlsPath();
			List<HeroArenaBaseConfig> list = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, HeroArenaBaseConfig.class);
			this.baseConfig = list.get(0);
			if(null == this.baseConfig){
				this.checkFail(info + ",it's not config.");
			}
			this.baseConfig.checkInit(info);
			//加载匹配规则
			fileName = XlsSheetNameType.hero_arena_match_rule.getXlsName();
			sheetName = XlsSheetNameType.hero_arena_match_rule.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			List<HeroArenaMatchRuleConfig> matchList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, HeroArenaMatchRuleConfig.class);
			int fetchSum = 0;
			for(HeroArenaMatchRuleConfig config : matchList){
				if(null == config){
					continue;
				}
				config.checkInit(info);
				fetchSum += config.getFetchNum();
				this.matchRuleMap.put(config.getGroupId(), config);
			}
			if(fetchSum != this.baseConfig.getGateCount()){
				this.checkFail(info + "the sum of fetchNum is not equal to this gateCount");
			}
			//加载奖励
			fileName = XlsSheetNameType.hero_arena_reward.getXlsName();
			sheetName = XlsSheetNameType.hero_arena_reward.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			List<HeroArenaRewardConfig> rewardList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, HeroArenaRewardConfig.class);
			for(HeroArenaRewardConfig config : rewardList){
				if(null == config){
					continue;
				}
				config.checkInit(info);
				int gateId = config.getGateId();
				if(!this.rewardMap.containsKey(gateId)){
					this.rewardMap.put(gateId, new ArrayList<HeroArenaRewardConfig>());
				}
				this.rewardMap.get(gateId).add(config);
			}
			//验证每一关是否都配置奖励
			for(int id=1; id<= this.baseConfig.getGateCount(); id++){
				if(!this.rewardMap.containsKey(id)){
					this.checkFail(info + "gateId = " + id + ",this gate is not config reward.");
				}
			}
		} catch (Exception e) {
			this.checkFail(info);
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	private HeroArenaRewardConfig getHeroArenaRewardConfig(int gateId, RoleInstance role){
		List<HeroArenaRewardConfig> list = this.getHeroArenaRewardConfigList(gateId);
		if(Util.isEmpty(list)){
			return null;
		}
		for(HeroArenaRewardConfig config : list){
			if(null == config){
				continue;
			}
			if(config.isSuitLevel(role)){
				return config;
			}
		}
		return null;
	}
	
	private List<HeroArenaRewardConfig> getHeroArenaRewardConfigList(int gateId){
		return this.rewardMap.get(gateId);
	}
	
	@Override
	public RoleHeroArenaRecord getRoleHeroArenaRecord(String roleId){
		return this.roleHeroArenaRecordMap.get(roleId);
	}

	@Override
	public C1270_HeroArenaPanelRespMessage getHeroArenaPanelMessage(RoleInstance role) {
		C1270_HeroArenaPanelRespMessage message = new C1270_HeroArenaPanelRespMessage();
		String roleId = role.getRoleId();
		RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(roleId);
		int gateId = record.getFightGateId();
		message.setCurrGate((byte) gateId);
		message.setMaxGate((byte) this.baseConfig.getGateCount());
		message.setCurrCount(record.getRebornNum());
		message.setFreeCount(this.baseConfig.getRebornFreeCount());
		message.setPayCount(this.baseConfig.getRebornPayCount());
		message.setGoldMoney(this.baseConfig.getRebornGoldMoney());
		message.setSelectedHeros(record.getSelectHeros());
		List<HeroArenaHeroItem> selfHeros = new ArrayList<HeroArenaHeroItem>();
		Collection<RoleHero> roleHeros = GameContext.getUserHeroApp().getAllRoleHero(roleId);
		if(!Util.isEmpty(roleHeros)){
			for(RoleHero hero : roleHeros){
				HeroArenaHeroItem item = this.buildHeroArenaHeroItem(hero);
				if(null == item){
					continue;
				}
				//自己的英雄是否死亡
				if(record.isHeroDead(hero.getHeroId())){
					item.setDie(HeroLiveStatus.Dead.getType());
				}
				selfHeros.add(item);
			}
		}
		message.setSelfHeros(selfHeros);
		List<HeroArenaHeroItem> rivalHeros = new ArrayList<HeroArenaHeroItem>();
		//对战英雄从竞技数据里获取
		List<RoleHero> fightHeros = record.getFightHeroList();
		if(!Util.isEmpty(fightHeros)){
			for(RoleHero hero : fightHeros){
				HeroArenaHeroItem item = this.buildHeroArenaHeroItem(hero);
				if(null == item){
					continue;
				}
				rivalHeros.add(item);
			}
		}
		message.setRivalHeros(rivalHeros);
		HeroArenaRewardConfig reward = this.getHeroArenaRewardConfig(gateId, role);
		if(null != reward){
			message.setAwardAttrList(reward.buildAttrAwardList());
			message.setAwardGoodsList(reward.buildAwardGoodsList());
		}
		return message;
	}
	
	@Override
	public int getCurrGateId(RoleInstance role){
		RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getRoleId());
		return record.getFightGateId();
	}
	
	@Override
	public int getMaxGateId(){
		return this.baseConfig.getGateCount();
	}
	
	private HeroArenaHeroItem buildHeroArenaHeroItem(RoleHero hero){
		if(null == hero){
			return null;
		}
		int heroId = hero.getHeroId();
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(heroId);
		if(null == gb){
			return null;
		}
		HeroArenaHeroItem item = new HeroArenaHeroItem();
		item.setHeroId(heroId);
		item.setHeroLevel((short) hero.getLevel());
		item.setImageId(gb.getImageId());
		item.setResId((short) gb.getResId());
		item.setBattleScore(hero.getBattleScore());
		return item;
	}

	@Override
	public void login(RoleInstance role) {
		try {
			String roleId = role.getRoleId();
			RoleHeroArenaRecord record = GameContext.getHeroArenaStorage().getRoleHeroArenaRecord(roleId);
			if(null == record){
				record = new RoleHeroArenaRecord();
				record.setRoleId(roleId);
				record.setUpdateTime(new Date());
			}
			this.roleHeroArenaRecordMap.put(roleId, record);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".login error: ", e);
		}
	}

	@Override
	public void logout(RoleInstance role) {
		try {
			String roleId = role.getRoleId();
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(roleId);
			//修改过的才保存
			if(record.isModified()){
				GameContext.getHeroArenaStorage().saveRoleHeroArenaRecord(record);
			}
			//可战斗英雄信息保存
			List<RoleHero> heros = this.filterFightRoleHeros(role); 
			GameContext.getHeroArenaStorage().saveRoleHeros(roleId, heros);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".logout error: ", e);
		}
	}
	
	/** 筛选出站英雄列表 **/
	private List<RoleHero> filterFightRoleHeros(RoleInstance role){
		List<RoleHero> list = new ArrayList<RoleHero>();
		try {
			String roleId = role.getRoleId();
			Collection<RoleHero> roleHeros = GameContext.getUserHeroApp().getAllRoleHero(roleId);
			if(Util.isEmpty(roleHeros)){
				return list;
			}
			RoleHero onBtHero = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
			int onBtHeroId = onBtHero.getHeroId();
			//按战斗力降序保存
			TreeMap<Integer,RoleHero> bsHeroMap = Maps.newTreeMap(new Comparator<Integer>(){
				@Override
				public int compare(Integer bs0, Integer bs1) {
					return bs1.compareTo(bs0);
				}
			});
			for(RoleHero hero : roleHeros){
				if(null == hero){
					continue;
				}
				if(hero.getHeroId() == onBtHeroId){
					continue;
				}
				bsHeroMap.put(hero.getBattleScore(), hero);
			}
			//第一个是出战英雄
			list.add(onBtHero);
			//后两个是战斗力较高的英雄
			int count = 1;
			for(RoleHero rh : bsHeroMap.values()){
				if(null == rh){
					continue;
				}
				if(count >= 3){
					break;
				}
				list.add(rh);
				count++;
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".filterFightRoleHeros error : ", e);
		}
		return list;
	}

	@Override
	public List<RoleHero> matchFightRoleHeros(String fightRoleId) {
		if(Util.isEmpty(fightRoleId)){
			return null;
		}
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(fightRoleId);
		if(null != role){
			return this.filterFightRoleHeros(role);
		}
		return GameContext.getHeroArenaStorage().getRoleHeros(fightRoleId);
	}

	@Override
	public List<RoleHero> getFightHeroList(RoleInstance role) {
		RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getRoleId());
		return record.getFightHeroList();
	}

	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	@Override
	public List<String> matchRivalRoles(String roleId) {
		List<String> rivals = new ArrayList<String>();
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null == role){
			return rivals;
		}
		Set<String> existRivals = new HashSet<String>();
		List<HeroArenaMatchTemp> tempList = new ArrayList<HeroArenaMatchTemp>();
		//根据区间的配置
		TreeMap<Integer,Map<String,String>> allrivalsMap = this.getMatchMaxRivals(role);
		int lastDifNum = 0;
		for(Entry<Integer,Map<String,String>> entry : allrivalsMap.entrySet()){
			if(null == entry){
				continue;
			}
			int groupId = entry.getKey();
			HeroArenaMatchRuleConfig mr = this.getHeroArenaMatchRuleConfig(groupId);
			if(null == mr){
				continue;
			}
			int count = mr.getFetchNum() + lastDifNum;
			List<HeroArenaMatchTemp> groupList = this.takeRivals(roleId, existRivals, entry.getValue(), count);
			//保存匹配到的对手
			tempList.addAll(groupList);
			int difNum = count - groupList.size();
			if(difNum > 0){
				List<HeroArenaMatchTemp> preList = this.takeRivals(roleId, existRivals, allrivalsMap.get(groupId-1), difNum);
				//保存匹配到的对手
				tempList.addAll(preList);
				//还差对手的数量
				lastDifNum = difNum - preList.size();
			}
		}
		//整体数量如果还是不够的话，再次构建
		int dfNum = this.baseConfig.getGateCount() - tempList.size();
		if(dfNum > 0){
			for(Entry<Integer,Map<String,String>> entry : allrivalsMap.entrySet()){
				if(null == entry){
					continue;
				}
				int groupId = entry.getKey();
				HeroArenaMatchRuleConfig mr = this.getHeroArenaMatchRuleConfig(groupId);
				if(null == mr){
					continue;
				}
				List<HeroArenaMatchTemp> groupList = this.takeRivals(roleId, existRivals, entry.getValue(), dfNum);
				//保存匹配到的对手
				tempList.addAll(groupList);
				dfNum -= groupList.size();
				if(dfNum <= 0){
					break;
				}
			}
		}
		//如果对手数量不够，则返回空列表
		if(tempList.size() < this.getMaxGateId()){
			return rivals;
		}
		//根据对手的战斗力从低到高排序
		Collections.sort(tempList, new Comparator<HeroArenaMatchTemp>(){
			@Override
			public int compare(HeroArenaMatchTemp arg0, HeroArenaMatchTemp arg1) {
				String bs0 = arg0.getBattleScore();
				String bs1 = arg1.getBattleScore();
				if(Util.isEmpty(bs0) || Util.isEmpty(bs1)){
					return 0;
				}
				return bs0.compareTo(bs1);
			}
		});
		for(HeroArenaMatchTemp tmp : tempList){
			if(null == tmp){
				continue;
			}
			rivals.add(tmp.getRoleId());
		}
		return rivals;
	}
	
	private List<HeroArenaMatchTemp> takeRivals(String selfRoleId, Set<String> existRivals, Map<String,String> groupMap, int count){
		List<HeroArenaMatchTemp> list = new ArrayList<HeroArenaMatchTemp>();
		if(Util.isEmpty(groupMap)){
			return list;
		}
		int num = 0;
		Iterator<Entry<String,String>> it = groupMap.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,String> entry = it.next();
			if(null == entry){
				continue;
			}
			//数量够了就结束
			if(num >= count){
				break;
			}
			String tarRoleId = entry.getKey();
			//对手不能重复
			if(tarRoleId.equals(selfRoleId) || existRivals.contains(tarRoleId)){
				continue;
			}
			list.add(new HeroArenaMatchTemp(tarRoleId, entry.getValue()));
			num++;
			existRivals.add(tarRoleId);
			//删除
			it.remove();
		}
		return list;
	}
	
	private TreeMap<Integer,Map<String,String>> getMatchMaxRivals(RoleInstance role){
		TreeMap<Integer,Map<String,String>> treeMap = Maps.newTreeMap();
		String roleId = role.getRoleId();
		int bs = role.getBattleScore();
		int limit = this.baseConfig.getGateCount();
		//从每个区间都分别获取最大数量的对手
		for(HeroArenaMatchRuleConfig config : this.matchRuleMap.values()){
			if(null == config){
				continue;
			}
			String startScore = String.valueOf(bs*config.getMinRatio());
			String endScore = String.valueOf(bs*config.getMaxRatio());
			Map<String,String> map = GameContext.getAsyncPvpApp().getRoleBattleScores(roleId, startScore, endScore, limit);
			if(Util.isEmpty(map)){
				continue;
			}
			treeMap.put(config.getGroupId(), map);
		}
		return treeMap;
	}

	@Override
	public Result selectHeros(RoleInstance role, int[] selectHeros) {
		Result result = new Result();
		try {
			if(null == selectHeros){
				return result.setInfo(this.getText(TextId.Hero_Arena_Req_Param_Error));
			}
			int size = selectHeros.length;
			if(size > Three){
				return result.setInfo(this.getText(TextId.Hero_Arena_Select_Hero_Length));
			}
			String roleId = role.getRoleId();
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(roleId);
			for(int heroId : selectHeros){
				RoleHero rh = GameContext.getUserHeroApp().getRoleHero(roleId, heroId);
				if(null == rh){
					continue;
//					return result.setInfo(this.getText(TextId.Hero_Arena_Req_Param_Error));//TODO:
				}
				if(record.isHeroDead(heroId)){
					return result.setInfo(this.getText(TextId.Hero_Arena_Select_Hero_Dead));
				}
			}
			record.setSelectHeros(selectHeros);
			record.setModified(true);
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".selectHeros error: ", e);
			return result.setInfo(this.getText(TextId.Sys_Error));
		}
	}

	@Override
	public Result fighting(RoleInstance role) {
		Result result = new Result();
		try {
			String roleId = role.getRoleId();
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(roleId);
			int maxGateId = this.getMaxGateId();
			int fightGateId = record.getFightGateId();
			//全部通关
			if(fightGateId > maxGateId || (fightGateId == maxGateId && record.isVictory())){
				return result.setInfo(this.getText(TextId.Hero_Arena_Fight_Has_Pass_All));
			}
			int heroNum = 0;//有效英雄数量
			for(int heroId : record.getSelectHeros()){
				if(heroId <= 0){
					continue;
				}
				//死亡的英雄不起效
				if(record.isHeroDead(heroId)){
					continue;
				}
				heroNum ++;
			}
			if(heroNum < 2){
				return result.setInfo(this.getText(TextId.Hero_Arena_Fight_Hero_Not_Enough));
			}
			Point point = new Point(this.baseConfig.getMapId(), this.baseConfig.getMapX1(), this.baseConfig.getMapY1());
			GameContext.getUserMapApp().changeMap(role, point);
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".fighting error: ", e);
			return result.setInfo(this.getText(TextId.Sys_Error));
		}
	}
	
	public HeroArenaMatchRuleConfig getHeroArenaMatchRuleConfig(int groupId){
		return this.matchRuleMap.get(groupId);
	}

	@Override
	public HeroArenaBaseConfig getHeroArenaBaseConfig() {
		return this.baseConfig;
	}

	@Override
	public void fightDeath(RoleInstance role) {
		try {
			String roleId = role.getRoleId();
			RoleHero rh = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
			if(null == rh){
				return;
			}
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(roleId);
			record.getDieHero().add(rh.getHeroId());
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".fightDeath error: ", e);
		}
	}

	@Override
	public void gameOver(RoleInstance role, int gateId, byte[] pkResults, String rivalRoleName) {
		try {
			String roleId = role.getRoleId();
			//判断战斗胜负
			if(3 != pkResults.length){
				return;
			}
			int victoryNum = 0;
			for(int res : pkResults){
				if(HeroFightStatus.Victory.getType() == res){
					victoryNum ++;
				}
			}
			HeroFightStatus fightStatus = victoryNum >= 2 ? HeroFightStatus.Victory : HeroFightStatus.Failure;
			//通知战斗结果
			C1273_HeroArenaResultNotifyMessage message = new C1273_HeroArenaResultNotifyMessage();
			message.setResult(fightStatus.getType());
			message.setRivalRoleName(rivalRoleName);
			message.setRecords(pkResults);
			HeroArenaRewardConfig reward = this.getHeroArenaRewardConfig(gateId, role);
			if(null != reward){
				message.setAwardAttrList(reward.buildAttrAwardList());
				message.setAwardGoodsList(reward.buildAwardGoodsList());
			}
			this.sendMessage(role, message);
			//胜利发奖
			if(HeroFightStatus.Victory == fightStatus){
				//发奖励
				if(null != reward){
					MailAttriBean attriBean = new MailAttriBean();
					attriBean.setExp(reward.getExp());
					attriBean.setSilverMoney(reward.getSilver());
					attriBean.setZp(reward.getPotential());
					//TODO:徽章属性
					//异步发邮件
					GameContext.getMailApp().sendMailAsync(roleId, reward.getMailTitle(), reward.getMailContent(), 
							MailSendRoleType.System.getName(), OutputConsumeType.hero_arena_Reward.getType(), reward.getGoodsList(), attriBean);
				}
				//修改记录
				RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(roleId);
				record.VictoryUpdate();
			}
			
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".gameOver error: ", e);
		}
	}
	
	private void sendMessage(RoleInstance role, Message message){
		GameContext.getMessageCenter().sendSysMsg(role, message);
	}
	
}
