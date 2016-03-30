package com.game.draco.app.hero.arena;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.python.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapHeroArenaInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.hero.arena.config.HeroArenaBaseConfig;
import com.game.draco.app.hero.arena.config.HeroArenaGateConfig;
import com.game.draco.app.hero.arena.config.HeroArenaMatchRuleConfig;
import com.game.draco.app.hero.arena.config.HeroArenaMustRewardGroupConfig;
import com.game.draco.app.hero.arena.config.HeroArenaRewardConfig;
import com.game.draco.app.hero.arena.config.HeroArenaRewardGroupConfig;
import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;
import com.game.draco.app.hero.arena.vo.HeroRewardResult;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.domain.RoleHeroStatus;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.vip.type.VipPrivilegeType;
import com.game.draco.message.item.GoodsLiteNamedExItem;
import com.game.draco.message.item.HeroArenaHeroItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C1343_HeroArenaResultNotifyMessage;
import com.game.draco.message.response.C1347_HeroArenaPanelRespMessage;
import com.google.common.collect.Maps;

public class HeroArenaAppImpl implements HeroArenaApp {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Integer,HeroArenaRewardConfig> rewardMap = Maps.newHashMap();
	private Map<Integer,List<HeroArenaRewardGroupConfig>> rewardGroupMap = Maps.newHashMap();
	private Map<Integer,List<HeroArenaMustRewardGroupConfig>> mustRewardGroupMap = Maps.newHashMap();
	private TreeMap<Integer,HeroArenaMatchRuleConfig> matchRuleMap = Maps.newTreeMap();
	private List<HeroArenaGateConfig> gateConfigList = Lists.newArrayList();
	private Map<Integer,RoleHeroArenaRecord> roleHeroArenaRecordMap = Maps.newConcurrentMap();
	private HeroArenaBaseConfig baseConfig = null;
	public static final float BASE_RATIO = 10000f;
	private final static int DEFAULT_MATCH_GROUPID = -1 ;
	
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
		String info = "";
		String fileName = "";
		String sheetName = "";
		String xlsPath = "";
		try {
			//加载基本配置
			fileName = XlsSheetNameType.hero_arena_base.getXlsName();
			sheetName = XlsSheetNameType.hero_arena_base.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			xlsPath = GameContext.getPathConfig().getXlsPath();
			baseConfig = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, HeroArenaBaseConfig.class);
			if(null == baseConfig){
				this.checkFail(info + ",it's not config.");
			}
			//加载基本配置
			fileName = XlsSheetNameType.hero_arena_gate.getXlsName();
			sheetName = XlsSheetNameType.hero_arena_gate.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			xlsPath = GameContext.getPathConfig().getXlsPath();
			gateConfigList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, HeroArenaGateConfig.class);
			if(Util.isEmpty(gateConfigList)){
				this.checkFail(info + ",it's not config.");
			}else{
				//修改地图逻辑类型
				for(HeroArenaGateConfig config:gateConfigList){
					String mapId = config.getMapId() ;
					MapConfig mapConfig = GameContext.getMapApp().getMapConfig(mapId);
					if(null == mapConfig){
						this.checkFail(info + " map not exist,mapId=" + mapId);
						continue ;
					}
					if(!mapConfig.changeLogicType(MapLogicType.heroArena)){
						this.checkFail(info + " map logic type error,mapId=" + mapId);
						continue ;
					}
				}
			}
			
			//加载匹配规则
			fileName = XlsSheetNameType.hero_arena_match_rule.getXlsName();
			sheetName = XlsSheetNameType.hero_arena_match_rule.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			List<HeroArenaMatchRuleConfig> matchList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, HeroArenaMatchRuleConfig.class);
			for(HeroArenaMatchRuleConfig config : matchList){
				if(null == config){
					continue;
				}
				config.checkInit(info);
				this.matchRuleMap.put(config.getGroupId(), config);
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
				this.rewardMap.put(config.getGateId(),config);
			}
			
			//奖励组
			//加载奖励
			fileName = XlsSheetNameType.hero_arena_reward_group.getXlsName();
			sheetName = XlsSheetNameType.hero_arena_reward_group.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			List<HeroArenaRewardGroupConfig> rewardGroupList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, HeroArenaRewardGroupConfig.class);
			for(HeroArenaRewardGroupConfig config : rewardGroupList){
				if(null == config){
					continue;
				}
				if(rewardGroupMap.containsKey(config.getGroupId())){
					rewardGroupMap.get(config.getGroupId()).add(config);
				}else{
					List<HeroArenaRewardGroupConfig> list = Lists.newArrayList();
					list.add(config);
					rewardGroupMap.put(config.getGroupId(),list);
				}
			}
			
			//加载奖励
			fileName = XlsSheetNameType.hero_arena_reward_must.getXlsName();
			sheetName = XlsSheetNameType.hero_arena_reward_must.getSheetName();
			info = "load excel error: fileName = " + fileName + ", sheetName = " + sheetName + ".";
			List<HeroArenaMustRewardGroupConfig> mustRewardGroupList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, HeroArenaMustRewardGroupConfig.class);
			for(HeroArenaMustRewardGroupConfig config : mustRewardGroupList){
				if(null == config){
					continue;
				}
				if(mustRewardGroupMap.containsKey(config.getGroupId())){
					mustRewardGroupMap.get(config.getGroupId()).add(config);
				}else{
					List<HeroArenaMustRewardGroupConfig> list = Lists.newArrayList();
					list.add(config);
					mustRewardGroupMap.put(config.getGroupId(),list);
				}
			}
			
			//验证每一关是否都配置奖励
			for(int id=1; id<= gateConfigList.size(); id++){
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
		HeroArenaRewardConfig baseConfig = this.getHeroArenaRewardConfigList(gateId);
		if(baseConfig == null){
			return null;
		}
		return baseConfig;
	}
	
	private HeroArenaRewardConfig getHeroArenaRewardConfigList(int gateId){
		return this.rewardMap.get(gateId);
	}
	
	@Override
	public RoleHeroArenaRecord getRoleHeroArenaRecord(int roleId){
		return this.roleHeroArenaRecordMap.get(roleId);
	}

	@Override
	public Message getHeroArenaPanelMessage(RoleInstance role) {
		try {
			C1347_HeroArenaPanelRespMessage message = new C1347_HeroArenaPanelRespMessage();
			//商店id提前
			message.setShopId(baseConfig.getShopId());
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			if(record == null || record.getFightRoleId() == null){
				return new C0003_TipNotifyMessage(this.getText(TextId.Hero_Arena_Fight_null)); 
			}
			AsyncPvpRoleAttr fightRoleAttr = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(record.getFightRoleId());
			if(fightRoleAttr != null){
				message.setRoleName(fightRoleAttr.getRoleName());
				message.setLevel(fightRoleAttr.getLevel());
			}
			int gateId = record.getFightGateId();
			int maxGateId = gateConfigList.size();
			if(gateId > maxGateId){
				gateId = maxGateId;
			}
			
			int vipNum = GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), VipPrivilegeType.HERO_ARENA_BUY_RESET_TIMES.getType(),"");
			//剩余重置次数
			int freeNum = getFreeNum(role);
			short surplusNum = (short)((freeNum + vipNum) - record.getResetNum());
			if(surplusNum < 0){
				surplusNum = 0;
			}
			message.setSurplusNum(surplusNum);
			message.setCurrGate((byte) gateId);
			message.setMaxGate((byte) maxGateId);
			message.setState(record.getState());
			
			//判断是否是第一次
			boolean flag = false;
			for(int selectHeroId : record.getSelectHeros()){
				if(selectHeroId > 0){
					flag = true;
					break;
				}
			}
			
			if(!flag){
				RoleHeroStatus status = GameContext.getUserHeroApp().getRoleHeroStatus(role.getRoleId());			

				Set<Integer> switchHero = status.getSwitchHeroSet();
				int i=0;
				for(int sId : switchHero){
					record.getSelectHeros()[i] = sId;
					i++;
				}
			}
			message.setSelectedHeros(record.getSelectHeros());
			List<HeroArenaHeroItem> selfHeros = new ArrayList<HeroArenaHeroItem>();
			Collection<RoleHero> roleHeros = GameContext.getUserHeroApp().getAllRoleHero(role.getRoleId());
			if(!Util.isEmpty(roleHeros)){
				for(RoleHero hero : roleHeros){
					short hpRate = 0 ;
					if(record.isHeroDead(hero.getHeroId())){
						hpRate = 0 ;
					}else{
						if(record.getHeroMap().containsKey(hero.getHeroId())){
							hpRate = record.getHeroMap().get(hero.getHeroId());
						}else{
							hpRate = RoleHero.HP_RATE_FULL ;
						}
					}
					HeroArenaHeroItem item = this.buildHeroArenaHeroItem(role,hero);
					if(null == item){
						continue;
					}
					item.setHpRate(hpRate);
					//自己的英雄是否死亡
					if(hpRate == 0){
						item.setDie(HeroLiveStatus.Dead.getType());
					}
					selfHeros.add(item);
				}
			}
			this.sortHeroArenaHeroItems(selfHeros);//排序
			message.setSelfHeros(selfHeros);
			List<HeroArenaHeroItem> rivalHeros = new ArrayList<HeroArenaHeroItem>();
			//对战英雄从竞技数据里获取
			List<RoleHero> fightHeros = record.getFightHeroList();
			
			if(!Util.isEmpty(fightHeros)){
			
				int tempHeroId = -1;
				int tempIndex = 0;
				for(RoleHero hero : fightHeros){
					if(record.getTargetHeroMap().containsKey(hero.getHeroId())){
						short hpRate = record.getTargetHeroMap().get(hero.getHeroId());
						if(hpRate == 0){
							tempHeroId = hero.getHeroId();
							tempIndex++;
						}
					}
				}
				
				if(tempIndex != fightHeros.size()){
					tempHeroId = -1;
				}
				
				if(tempHeroId != -1){
					for(Entry<Integer,Short> tempTarget : record.getTargetHeroMap().entrySet()){
						if(tempTarget.getKey() == tempHeroId){
							tempTarget.setValue((short)2000);
							break;
						}
					}
				}
				
				for(RoleHero hero : fightHeros){
					if(record.getTargetHeroMap().containsKey(hero.getHeroId())){
						short hpRate = record.getTargetHeroMap().get(hero.getHeroId());
						hero.setHpRate(hpRate);
					}else{
						hero.setHpRate(RoleHero.HP_RATE_FULL);
					}
					HeroArenaHeroItem item = this.buildHeroArenaHeroItem(null,hero);
					if(null == item){
						continue;
					}
					if(item.getHpRate() == 0){
						item.setDie(HeroLiveStatus.Dead.getType());
					}
					GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, hero.getHeroId());
					if(goodsHero != null){
						item.setAttrSeriesId(goodsHero.getSeriesId());
						item.setAttrGearId(goodsHero.getGearId());
					}
					
					rivalHeros.add(item);
				}
			}
			this.sortHeroArenaHeroItems(rivalHeros);//排序
			message.setRivalHeros(rivalHeros);
			return message;
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".login error: ", e);
			return new C0003_TipNotifyMessage(this.getText(TextId.Sys_Error));
		}
	}
	
	/**
	 * 英雄列表排序：1.存活>死亡 2.英雄模板ID小的在前
	 * @param items
	 */
	private void sortHeroArenaHeroItems(List<HeroArenaHeroItem> items){
		Collections.sort(items, new Comparator<HeroArenaHeroItem>(){
			@Override
			public int compare(HeroArenaHeroItem o1, HeroArenaHeroItem o2) {
				if(o1.getDie() < o2.getDie()){
					return -1;
				}
				if(o1.getDie() > o2.getDie()){
					return 1;
				}
				if(o1.getHeroId() < o2.getHeroId()){
					return -1;
				}
				if(o1.getHeroId() > o2.getHeroId()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	@Override
	public int getCurrGateId(RoleInstance role){
		RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
		return record.getFightGateId();
	}
	
	private HeroArenaHeroItem buildHeroArenaHeroItem(RoleInstance role,RoleHero hero){
		if(null == hero){
			return null;
		}
		int heroId = hero.getHeroId();
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(heroId);
		if(null == gb){
			return null;
		}
		
		HeroArenaHeroItem item = new HeroArenaHeroItem();
		item.setQuality(hero.getQuality());
		item.setStar(hero.getStar());
		item.setHeroId(heroId);
		item.setHpRate(hero.getHpRate());
		if(role != null){
			int hp = (int)(role.getMaxHP()*(hero.getHpRate()/(float)RoleHero.HP_RATE_FULL)) ;
			item.setCurHp(hp);
		}
		item.setHeroLevel((short) hero.getLevel());
		item.setImageId(gb.getImageId());
		item.setResId((short) gb.getResId());
		item.setBattleScore(GameContext.getHeroApp().getBattleScore(hero));
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, heroId);
		if(goodsHero != null){
			item.setAttrSeriesId(goodsHero.getSeriesId());
			item.setAttrGearId(goodsHero.getGearId());
		}
		return item;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try {
			RoleHeroArenaRecord record = GameContext.getHeroArenaStorage().getRoleHeroArenaRecord(role.getRoleId());
			if(null == record){
				record = new RoleHeroArenaRecord();
				record.setRoleId(role.getRoleId());
				record.setUpdateTime(new Date());
			}
			this.roleHeroArenaRecordMap.put(role.getIntRoleId(), record);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".login error: ", e);
			return 0;
		}
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			//修改过的才保存
			if(record.isModified()){
				GameContext.getHeroArenaStorage().saveRoleHeroArenaRecord(record);
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".logout error: ", e);
			return 0;
		}
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}
	
	@Override
	public List<RoleHero> matchFightRoleHeros(String fightRoleId) {
		if(Util.isEmpty(fightRoleId)){
			return null;
		}
		return GameContext.getHeroApp().getRoleSwitchableHeroList(fightRoleId) ;
	}

	@Override
	public List<RoleHero> getFightHeroList(RoleInstance role) {
		RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
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
		Map<String,String> defaultGroupMap = allrivalsMap.remove(DEFAULT_MATCH_GROUPID) ;
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
		int dfNum = gateConfigList.size() - tempList.size();
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
		if (tempList.size() < gateConfigList.size() && null != defaultGroupMap) {
			// 容错
			List<HeroArenaMatchTemp> groupList = this.takeRivals(roleId,
					existRivals, defaultGroupMap, gateConfigList.size()
							- tempList.size());
			// 保存匹配到的对手
			tempList.addAll(groupList);
		}
		//如果对手数量不够，则返回空列表
		if(tempList.size() < gateConfigList.size()){
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
				Integer score0 = Integer.valueOf(bs0);
				Integer score1 = Integer.valueOf(bs1);
				return score0.compareTo(score1);
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
		//String roleId = role.getRoleId();
		int bs = role.getBattleScore();
		int limit = gateConfigList.size();
		int total = 0 ;
		//从每个区间都分别获取最大数量的对手
		for(HeroArenaMatchRuleConfig config : this.matchRuleMap.values()){
			if(null == config){
				continue;
			}
			String startScore = "";
			String endScore = "";
			if(config.getMinRatio() > 0){
				float minValue = bs * (config.getMinRatio() / BASE_RATIO);
				startScore = String.valueOf((int) minValue);
			}
			if(config.getMaxRatio() > 0){
				float maxValue = bs * (config.getMaxRatio() / BASE_RATIO);
				endScore = String.valueOf((int) maxValue);
			}
			Map<String,String> map = GameContext.getAsyncPvpApp().randomRoleBattleScores(null, startScore, endScore, limit);
			if(Util.isEmpty(map)){
				continue;
			}
			total += map.size();
			treeMap.put(config.getGroupId(), map);
		}
		if(total >= limit){
			return treeMap ;
		}
		//从全部区间中获得玩家
		Map<String,String> map = GameContext.getAsyncPvpApp().randomRoleBattleScores(null, "", "", limit);
		if(Util.isEmpty(map)){
			return treeMap ;
		}
		treeMap.put(DEFAULT_MATCH_GROUPID, map);
		return treeMap;
	}

	@Override
	public Result selectHeros(RoleInstance role, int[] selectHeros) {
		Result result = new Result();
		try {
			if(this.hasInHeroArenaMap(role)){
				return result.setInfo(this.getText(TextId.Hero_Arena_Select_Hero_In_Map));
			}
			if(null == selectHeros){
				return result.setInfo(this.getText(TextId.Hero_Arena_Req_Param_Error));
			}
			
			int size = 0;
			for(int heroId : selectHeros){
				if(heroId > 0){
					size++;
				}
			}
			if(size < 1){
				return result.setInfo(GameContext.getI18n().messageFormat(TextId.Hero_Arena_Fight_Hero_Not_Enough,1));
			}
			
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			for(int heroId : selectHeros){
				RoleHero rh = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), heroId);
				if(null == rh){
					continue;
				}
				if(record.isHeroDead(heroId)){
					return result.setInfo(this.getText(TextId.Hero_Arena_Select_Hero_Dead));
				}
			}
			record.setSelectHeros(selectHeros);
			record.setModified(true);
			
			//进入战斗
			result = fighting(role);	
			
//			GameContext.getHeroApp().systemUpdateSwitchableHero(role, selectHeros);
			
			return result;
			
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".selectHeros error: ", e);
			return result.setInfo(this.getText(TextId.Sys_Error));
		}
	}

	@Override
	public Result fighting(RoleInstance role) {
		Result result = new Result();
		try {
			if(this.hasInHeroArenaMap(role)){
				return result.setInfo(this.getText(TextId.Hero_Arena_Fight_In_Map));
			}
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			//对手列表为空，说明没匹配到，不能进入战斗
			if(Util.isEmpty(record.getRivals())){
				return result.setInfo(this.getText(TextId.Hero_Arena_Fight_Rivals_Null));
			}
			
			if(Util.isEmpty(record.getFightHeroList())){
				return result.setInfo(this.getText(TextId.Hero_Arena_Fight_Rivals_Null));
			}
			
			int maxGateId = gateConfigList.size();
			int fightGateId = record.getFightGateId();
			if(record.getState() > 1){
				return result.setInfo(this.getText(TextId.HERO_ARENA_GATE_FINISH));
			}
			
			HeroArenaGateConfig gateConfig = getHeroArenaGateConfig(fightGateId);
			//全部通关
			if(fightGateId > maxGateId || baseConfig == null){
				return result.setInfo(this.getText(TextId.Hero_Arena_Fight_Has_Pass_All));
			}
			for(int heroId : record.getSelectHeros()){
				if(heroId <= 0){
					continue;
				}
				//死亡的英雄不起效
				if(record.isHeroDead(heroId)){
					continue;
				}
			}
			
			Point point = new Point(gateConfig.getMapId(), gateConfig.getMapX1(), gateConfig.getMapY1());
			GameContext.getUserMapApp().changeMap(role, point);
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".fighting error: ", e);
			return result.setInfo(this.getText(TextId.Sys_Error));
		}
	}
	
	private boolean hasInHeroArenaMap(RoleInstance role){
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance){
			return false;
		}
		return mapInstance instanceof MapHeroArenaInstance;
	}
	
	public HeroArenaMatchRuleConfig getHeroArenaMatchRuleConfig(int groupId){
		return this.matchRuleMap.get(groupId);
	}

	@Override
	public HeroArenaGateConfig getHeroArenaGateConfig(int gateId) {
		for(HeroArenaGateConfig gateConfig : gateConfigList){
			if(gateId == gateConfig.getGateId()){
				return gateConfig;
			}
		}
		return null;
	}

	@Override
	public void fightDeath(RoleInstance role) {
		try {
			RoleHero rh = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
			if(null == rh){
				return;
			}
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			record.getDieHero().add(rh.getHeroId());
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".fightDeath error: ", e);
		}
	}

	@Override
	public void gameOver(RoleInstance role, int gateId, HeroFightStatus fightStatus, String rivalRoleName) {
		try {
			//通知战斗结果
			C1343_HeroArenaResultNotifyMessage message = new C1343_HeroArenaResultNotifyMessage();
			message.setResult(fightStatus.getType());
//			message.setRivalRoleName(rivalRoleName);
//			message.setRecords(pkResults);
			this.sendMessage(role, message);
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			if(fightStatus.getType() == HeroFightStatus.Victory.getType()){
				record.setState((byte)1);
				boolean flag = isMaxGateId(role, gateId);
				if(flag){
					this.broadcast(role);
				}
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".gameOver error: ", e);
		}
	}
	
	private boolean isMaxGateId(RoleInstance role, int gateId){
		if(gateId >= gateConfigList.size()){
			return true;
		}
		return false;
	}
	
	private void sendMessage(RoleInstance role, Message message){
		GameContext.getMessageCenter().sendSysMsg(role, message);
	}
	
	/**
	 * 世界广播
	 * @param role
	 */
	private void broadcast(RoleInstance role) {
		try {
			String broadcastInfo = this.baseConfig.getBroadCastTips(role);
			if (Util.isEmpty(broadcastInfo)) {
				return ;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.Arena, ChannelType.Publicize_Personal, broadcastInfo, null, null);
		} catch (Exception ex) {
			logger.error("strengthen broadcast error", ex);
		}
	}
	
	@Override
	public Result heroReborn(RoleInstance role) {
		Result result = new Result();
		try {
			if(this.hasInHeroArenaMap(role)){
				return result.setInfo(this.getText(TextId.Hero_Arena_Fight_In_Map));
			}
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			if(!record.hasDieHero()){
				return result.setInfo(this.getText(TextId.Hero_Arena_Reborn_Null));
			}
			//清除死亡英雄列表
			record.rebornDieHeros();
			return result.success();
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".heroReborn error: ", e);
			return result.setInfo(this.getText(TextId.Sys_Error));
		}
	}

	@Override
	public boolean isHeroDead(RoleInstance role, int heroId) {
		try {
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			return record.isHeroDead(heroId);
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".isHeroDie error: ", e);
			return false;
		}
	}

	@Override
	public HeroRewardResult reward(RoleInstance role) {
		HeroRewardResult result = new HeroRewardResult();
		try {
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			if(record.getState() == (byte)1){
				record.setState((byte)2);
				HeroArenaRewardConfig reward = this.getHeroArenaRewardConfig(record.getFightGateId(), role);
				//发奖励
				if(null != reward){
					
					int rewardRate = GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), VipPrivilegeType.HERO_ARENA_REWARD_INCR.getType(),"");
					float rate = 0;
					if(rewardRate !=0){
						rate = rewardRate/BASE_RATIO;
					}
					//添加属性
					GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.exp,
							OperatorType.Add, reward.getExp() + (int)rate*reward.getExp(),
							OutputConsumeType.hero_arena_Reward);
					
					GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.gameMoney,
							OperatorType.Add, reward.getSilver() + (int)rate*reward.getSilver(),
							OutputConsumeType.hero_arena_Reward);
					
					GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.heroCoin,
							OperatorType.Add, reward.getHeroCoin() + (int)(rate*reward.getHeroCoin()),
							OutputConsumeType.hero_arena_Reward);
					
					GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.potential,
							OperatorType.Add, reward.getPotential() + (int)rate*reward.getPotential(),
							OutputConsumeType.hero_arena_Reward);
					
					//通知用户属性变化
					role.getBehavior().notifyAttribute();
					
					//添加物品
					List<GoodsOperateBean> goodsList = Lists.newArrayList();
					if(!Util.isEmpty(reward.getGroupId()) && !Util.isEmpty(reward.getRandInt())){
						String [] groupArr = reward.getGroupId().split(",");
						for(String groupId : groupArr){
							int gId = Integer.parseInt(groupId);
							if(rewardGroupMap.containsKey(gId)){
								List<HeroArenaRewardGroupConfig> rewardGroupList = rewardGroupMap.get(gId);
								String [] randArr = reward.getRandInt().split(",");
								for(int i=0;i<groupArr.length;i++){
									Map<Integer,Integer> weightMap = calcGoodsWeightMap(Integer.parseInt(groupArr[i]));
									Set<Integer> setGoodsId = Util.getWeightCalct(Integer.parseInt(randArr[i]), weightMap);
									for(HeroArenaRewardGroupConfig group : rewardGroupList){
										if(setGoodsId.contains(group.getGoodsId())){
											goodsList.add(new GoodsOperateBean(group.getGoodsId(), group.getGoodsNum(), BindingType.get(group.getBinded())));
										}
									}
								}
							}
						}
					}
					
					//添加必出物品
					if(mustRewardGroupMap.containsKey(reward.getMustGroupId())){
						List<HeroArenaMustRewardGroupConfig> mustRewardGroupList = mustRewardGroupMap.get(reward.getMustGroupId());
						for(HeroArenaMustRewardGroupConfig must : mustRewardGroupList){
							goodsList.add(new GoodsOperateBean(must.getGoodsId(), must.getGoodsNum(), BindingType.get(must.getBinded())));
						}
					}
					
					// 向背包中添加物品
					AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp()
							.addSomeGoodsBeanForBag(role, goodsList,OutputConsumeType.hero_arena_Reward);
					// 背包满了则发邮件
					List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
					if(!Util.isEmpty(putFailureList)){
						//异步发邮件
						GameContext.getMailApp().sendMailAsync(role.getRoleId(), reward.getMailTitle(), reward.getMailContent(), 
								MailSendRoleType.System.getName(), OutputConsumeType.hero_arena_Reward.getType(),putFailureList);
					}
					
					List<GoodsLiteNamedExItem> list = new ArrayList<GoodsLiteNamedExItem>();
					for (GoodsOperateBean goodsOperateBean : goodsList) {
						// 获取名称
						GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
								goodsOperateBean.getGoodsId());
						GoodsLiteNamedExItem goodsItem = goodsBase.getGoodsLiteNamedExItem();
						goodsItem.setBindType(goodsOperateBean.getBindType().getType());
						goodsItem.setNum((short) goodsOperateBean.getGoodsNum());

						list.add(goodsItem);
					}
					
					//添加奖励显示数据
					result.setAwardGoodsList(list);
					result.setAwardAttrList(reward.buildAttrAwardList(role.getRoleId()));
				}
				int maxGateId = gateConfigList.size();
				if(record.getFightGateId() < maxGateId){
					record.setState((byte)0);
					record.victoryUpdate();
					result.setInfo(getText(TextId.HERO_ARENA_REWARD_FINISH_NEXT));
				}else{
					result.setInfo(getText(TextId.HERO_ARENA_REWARD_FINISH));
				}
				result.success();
			}else if(record.getState() == (byte)0){
				result.setInfo(getText(TextId.HERO_ARENA_REWARD_NOFINISH_ERR));
			}else if(record.getState() == (byte)2){
				result.setInfo(getText(TextId.HERO_ARENA_REWARD_FINISH_ERR));
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".isHeroDie error: ", e);
			
		}
		// 刷新红点提示
		if (result.isSuccess() && !GameContext.getActiveApp().hasHint(role)) {
			GameContext.getHintApp().hintChange(role, HintType.active, false);
		}
		return result;
	}
	
	/**
	 * 遍历所有物品获得权重map KEY=物品ID,VALUE=权重
	 * @return
	 */
	private Map<Integer,Integer> calcGoodsWeightMap(int groupId){
		Map<Integer,Integer> goodsWeightMap = new HashMap<Integer, Integer>();
		if(rewardGroupMap.containsKey(groupId)){
			List<HeroArenaRewardGroupConfig> list = rewardGroupMap.get(groupId);
			for(HeroArenaRewardGroupConfig group : list){
				goodsWeightMap.put(group.getGoodsId(), group.getWeight());
			}
		}
		return goodsWeightMap;
	}
	
	@Override
	public Result resetHeroArena(RoleInstance role){
		Result result = new Result();
		try{
			RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
			int vipNum = GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), VipPrivilegeType.HERO_ARENA_BUY_RESET_TIMES.getType(),"");
			int freeNum = getFreeNum(role);
			
			//剩余重置次数
			if(freeNum + vipNum <= record.getResetNum()){
				result.setInfo(getText(TextId.HERO_ARENA_RESET_ERR));
				return result;
			}
			record.setResetNum((short)(record.getResetNum() + 1));
			record.setState((byte)0);
			result.success();
			result.setInfo(getText(TextId.HERO_ARENA_RESET_SUCCESS));
//			record.setResetTime(new Date());
			record.resetHeroArena();
			
		}catch(Exception e){
			logger.error("resetHeroArena is Error",e);
		}
		return result;
	}
	
	/**
	 * 获取免费次数
	 * @param resetTime
	 * @return
	 */
	private int getFreeNum(RoleInstance role){
		int freeNum = 0;
		Date now = new Date();
		if(!DateUtil.sameDay(role.getCreateTime(), now)){
			freeNum += 1;
		}
		return freeNum;
	}
	
	@Override
	public boolean isPlay(RoleInstance role){
		
		RoleHeroArenaRecord record = this.getRoleHeroArenaRecord(role.getIntRoleId());
		if(record == null){
			return true;
		}
		
		if(!isMaxGateId(role,record.getFightGateId())){
			return true;
		}
		
		int vipNum = GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), VipPrivilegeType.HERO_ARENA_BUY_RESET_TIMES.getType(),"");
		int freeNum = getFreeNum(role);
		
		//剩余重置次数
		if(freeNum + vipNum > record.getResetNum()){
			return true;
		}
		return false;
	}
	
}
