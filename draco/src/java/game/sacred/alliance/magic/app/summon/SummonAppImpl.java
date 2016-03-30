package sacred.alliance.magic.app.summon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.app.summon.vo.SummonResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.FrequencyType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.SummonType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.condition.Condition;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.SummonDbInfo;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.exchange.domain.ExchangeDbInfo;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.NpcFunctionItem;

public class SummonAppImpl implements SummonApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Integer, Summon> allSummonMap = new HashMap<Integer, Summon>();
	private Map<Integer, Condition> allConditionMap = new HashMap<Integer, Condition>();;
	private Map<String, List<Integer>> summonNpcMap = new HashMap<String, List<Integer>>();
	
	private Map<Integer, List<NpcBorn>> summonRuleMap = new HashMap<Integer,List<NpcBorn>>();
	private Map<Integer, SummonGroup> summonGroupMap = new HashMap<Integer,SummonGroup>();
	private Map<Short, List<Summon>> exitMapResetMap = new HashMap<Short, List<Summon>>();

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadSummon();
		this.loadSummonCondition();
		this.initSummonRule();
		this.initSummonGroup();
		this.init();
	}

	@Override
	public void stop() {
		
	}
	
	private void loadSummon() {
		//加载兑换项
		String fileName = XlsSheetNameType.summon.getXlsName();
		String sheetName = XlsSheetNameType.summon.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allSummonMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, Summon.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
//		if(Util.isEmpty(this.allSummonMap)){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
//		}
	}
	
	private void loadSummonCondition(){
		//加载条件类项
		String fileName = XlsSheetNameType.summon_condition.getXlsName();
		String sheetName = XlsSheetNameType.summon_condition.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allConditionMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, Condition.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
//		if(Util.isEmpty(this.allConditionMap)){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
//		}
	}
	
	private List<SummonRule> loadRefreshRule(){
		String fileName =  XlsSheetNameType.summon_rule.getXlsName();
		String sheetName = XlsSheetNameType.summon_rule.getSheetName();
		String path = GameContext.getPathConfig().getXlsPath();
		String sourceFile = path + fileName;
		return XlsPojoUtil.sheetToList(sourceFile, sheetName, SummonRule.class);
	}
	
	private void initSummonRule(){
		//加载AI刷怪规则
		for(SummonRule summonRule : this.loadRefreshRule()) {
			int ruleId = summonRule.getRuleId();
			List<NpcBorn> summonList = summonRuleMap.get(ruleId);
			if(null == summonList){
				summonList = new ArrayList<NpcBorn>();
				summonList.add(summonRule.getNpcBorn());
				summonRuleMap.put(ruleId, summonList);
			}else{
				summonList.add(summonRule.getNpcBorn());
			}
		}
	}
	
	private void initSummonGroup(){
		String fileName =  XlsSheetNameType.summon_group.getXlsName();
		String sheetName = XlsSheetNameType.summon_group.getSheetName();
		try{
			String path = GameContext.getPathConfig().getXlsPath();
			String sourceFile = path + fileName;
		
			summonGroupMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, SummonGroup.class);
			
			for(SummonGroup summonGroup : summonGroupMap.values()) {
				summonGroup.init();
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	private void init() {
		//根据条件类型来初始化条件实例
		for(Integer id: this.allConditionMap.keySet()){
			Condition condition = this.allConditionMap.get(id);
			if(condition == null)
				continue;
			condition.getConditionTypeInstance();
			condition.init(condition.getCompareType());
		}
		
		//根据兑换的条件，物品初始化实例
		List<Integer> ids = new ArrayList<Integer>();
		for(Summon summon : this.allSummonMap.values()){
			if(summon == null)
				continue;
			if(!summon.init()){
				ids.add(summon.getId());
				continue;
			}
			String[] conditionIds = Util.splitString(summon.getConditionIds());
			for(int i=0; i<conditionIds.length; i++){
				summon.getConditionList().add(this.allConditionMap.get(Integer.valueOf(conditionIds[i])));
			}
			
			String npcId = summon.getNpcId();
			if(!summonNpcMap.containsKey(npcId)) {
				summonNpcMap.put(npcId, new ArrayList<Integer>());
			}
			summonNpcMap.get(npcId).add(summon.getId());
			
			short enterResetCopyId = summon.getEnterResetCopyId();
			if(summon.getFrequencyType() == FrequencyType.FREQUENCY_TYPE_COPY.getType() ) {
				if(!exitMapResetMap.containsKey(enterResetCopyId)) {
					exitMapResetMap.put(enterResetCopyId, new ArrayList<Summon>());
				}
				exitMapResetMap.get(enterResetCopyId).add(summon);
			}
		}
		//删除过期的兑换
		if(ids.size() != 0){
			for(Integer id : ids){
				this.allSummonMap.remove(id);
			}
		}
	}
	
	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role, NpcInstance npc) {
		List<NpcFunctionItem> list = null;
		String npcId = npc.getNpcid();
		List<Integer> npcList = summonNpcMap.get(npcId);
		if(Util.isEmpty(npcList)){
			return list;
		}
		for(Integer key : npcList) {
			Summon summon = allSummonMap.get(key);
			if(summon == null || !summon.getNpcId().equals(npcId)) {
				continue;
			}
			if(null == list){
				list = new ArrayList<NpcFunctionItem>();
			}
			NpcFunctionItem item = new NpcFunctionItem();
				//如果兑换的子项长度是1直接显示该兑换项
			if(!summon.isMeetConditionsAndDis(role)){
				continue;
			}
			if(summon.isTimeOpen() && summon.isOutDate()){
				continue;
			}
			item.setTitle(summon.getName());
			item.setCommandId(SummonHelper.SUMMON_CMD);
			item.setParam(SummonHelper.formatSummonParam(summon.getId(), npcId));
			list.add(item);
		}
		return list;
	}

	@Override
	public SummonResult canSummon(RoleInstance role, Summon summon, boolean popAttrDialog) {
		try{
			return summon.isMeet(role,popAttrDialog);
		}catch(Exception e) {
			this.logger.error("",e);
		}
		SummonResult statusResult = new SummonResult().failure();
		return statusResult.setStatus(Status.Summon_Can_Not_Summon);
	}

	@Override
	public Result summon(RoleInstance role, Summon summon) {
		Result result = new GoodsResult();
		
		result = GameContext.getUserGoodsApp().deleteForBagByMap(role, summon.getConsumeGoods(), OutputConsumeType.goods_summon_consume);
		if (!result.isSuccess()) {
			return result;
		}

		boolean needNotify = false;
		// 消耗钱
		int consumSilver = summon.getConsumeSilver();
		if (consumSilver > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.gameMoney, OperatorType.Decrease,
					consumSilver, OutputConsumeType.goods_summon_consume);
			needNotify = true;
		}
		int consumGold = summon.getConsumeGold();
		if (consumGold > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.goldMoney, OperatorType.Decrease, consumGold,
					OutputConsumeType.goods_summon_consume);
			needNotify = true;
		}
		if (needNotify) {
			role.getBehavior().notifyAttribute();
		}

		result = summonNpc(role, summon.getGroupId(), summon.getId(), summon.getType());
		return result;
	}
	
	private List<NpcBorn> getNpcBornList(int groupId){
		List<NpcBorn> list = new ArrayList<NpcBorn>();
		if(groupId == 0) {
			return list;
		}
		SummonGroup summonGroup = summonGroupMap.get(groupId);
		if(null == summonGroup) {
			return list;
		}
		Integer id = summonGroup.getWeightRuleId();
		if(null == id) {
			return list;
		}
		int ruleId = id.intValue();
		if(ruleId == 0) {
			return list;
		}
		list = summonRuleMap.get(ruleId);
		if(Util.isEmpty(list)) {
			return list;
		}
		return list;
	}
	
	private Result summonNpc(RoleInstance role, int groupId, int summonId, int type){
		Result result = new Result();
		try{
			List<NpcBorn> summonRuleList = getNpcBornList(groupId);
			if(type == SummonType.SUMMON_ROLE.getType() /*|| type == SummonType.SUMMON_CAMP_WAR.getType()*/) {
				for(NpcBorn npcBorn : summonRuleList) {
					NpcInstance npcInstance = role.getMapInstance().summonCreateNpc(npcBorn);
					npcInstance.setSummonId(summonId);
					npcInstance.setSummonRoleId(role.getRoleId());
				}
			}
//			else{
//				Faction faction = GameContext.getFactionApp().getFaction(role);
//				FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//				if(null == faction || null == factionRole){
//					return result.setInfo(Status.Faction_Not_Own.getTips());
//				}
//				FactionSoulRecord re = faction.getFactionSoulRecord();
//				if(null == re) {
//					return result.setInfo(Status.Faction_FAILURE.getTips());
//				}
//				FactionSoulInfo factionSoulInfo = GameContext.getFactionSoulApp().getFactionSoulInfo(re.getSoulId());
//				if(null == factionSoulInfo) {
//					return result.setInfo(Status.Faction_Soul_Null.getTips());
//				}
//				
//				FactionSoulAttri factionSoulAttri = factionSoulInfo.getAttriMap().get((byte)re.getLevel());
//				if(null == factionSoulAttri) {
//					return result.setInfo(Status.Faction_Soul_Null.getTips());
//				}
//				String npcId = factionSoulAttri.getNpcId();
//				FactionSoulFlyConfig factionSoulFlyConfig = GameContext.getFactionSoulApp().getFactionSoulFly(re.getSoulId(), re.getFlyNum());
//				if(null == factionSoulFlyConfig) {
//					return result.setInfo(Status.Faction_Soul_Null.getTips());
//				}
//				List<Short> skillList = factionSoulFlyConfig.getSkillList();
//				NpcBorn npcBorn = summonRuleList.get(0);
//				NpcInstance npcInstance = role.getMapInstance().summonCreateNpc(npcId, npcBorn.getBornmapgxbegin(), npcBorn.getBornmapgybegin(), skillList, factionSoulFlyConfig.getResId());
//				npcInstance.setSummonId(summonId);
//				npcInstance.setSummonRoleId(role.getRoleId());
//			}
		}catch(Exception e){
			logger.error("summonNpc error",e);
		}
		return result.success();
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context) {
		try{
			List<SummonDbInfo> roleSummon = GameContext.getBaseDAO().selectList(SummonDbInfo.class, "targetId", role.getRoleId());
			if(null==roleSummon || roleSummon.size()==0){
				return 1;
			}
			Map<Integer, SummonDbInfo> summonDbInfoMap = role.getSummonDbInfo();
			for(SummonDbInfo summonDbInfo : roleSummon){
				if(null == summonDbInfo){
					continue;
				}
				summonDbInfo.setExistRecord(true);
				summonDbInfoMap.put(summonDbInfo.getId(), summonDbInfo);
			}
		}catch(Exception e){
			logger.error("loadRoleSummon error",e);
			return 0;
		}
		
		return 1;
	}
	
//	@Override
//	public void loadFactionSummon(Faction faction) {
//		try{
//			List<SummonDbInfo> factionSummon = GameContext.getBaseDAO().selectList(SummonDbInfo.class, "targetId", faction.getFactionId());
//			if(null==factionSummon || factionSummon.size()==0){
//				return;
//			}
//			Map<Integer, SummonDbInfo> summonDbInfoMap = faction.getSummonDbInfo();
//			for(SummonDbInfo summonDbInfo : factionSummon){
//				if(null == summonDbInfo){
//					continue;
//				}
//				summonDbInfo.setExistRecord(true);
//				summonDbInfoMap.put(summonDbInfo.getId(), summonDbInfo);
//			}
//		}catch(Exception e){
//			logger.error("loadFactionSummon error",e);
//		}
//	}
	

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			Map<Integer, SummonDbInfo> summonDbInfoMap = role.getSummonDbInfo();
			if(Util.isEmpty(summonDbInfoMap)){
				return 1;
			}
			for(SummonDbInfo summonDbInfo : summonDbInfoMap.values()){
				if(null == summonDbInfo){
					continue;
				}
				
				Summon summon = this.getSummonById(summonDbInfo.getId());
				if(null == summon) {
					continue;
				}
				summon.resetSummon(summonDbInfo);
				if(summonDbInfo.isExistRecord()){
					if(summonDbInfo.getTimes() == 0) {
						GameContext.getBaseDAO().delete(ExchangeDbInfo.class, "id", summonDbInfo.getId(), "roleId", summonDbInfo.getTargetId());
					}else{
						GameContext.getBaseDAO().update(summonDbInfo);
					}
				}else{
					if(summonDbInfo.getTimes() > 0) {
						GameContext.getBaseDAO().insert(summonDbInfo);
					}
				}
			}
		} catch (Exception ex) {
			GameContext.getExchangeApp().offlineLog(role);
			Log4jManager.OFFLINE_ERROR_LOG.error(
					"SummonApp.offline error,roleId=" + role.getRoleId() + ",userId="
							+ role.getUserId(), ex);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void offlineLog(RoleInstance role) {
		try{
			Map<Integer, SummonDbInfo> summonDbInfoMap = role.getSummonDbInfo();
			if(summonDbInfoMap.size() == 0){
				return;
			}
			for(SummonDbInfo summonDbInfo : summonDbInfoMap.values()){
				if(null == summonDbInfo){
					continue;
				}
				StringBuffer sb = new StringBuffer();
				sb.append(summonDbInfo.getId());
				sb.append(Cat.pound);
				sb.append(summonDbInfo.getTargetId());
				sb.append(Cat.pound);
				sb.append(summonDbInfo.getTimes());
				sb.append(Cat.pound);
				sb.append(DateUtil.getTimeByDate(summonDbInfo.getLastExTime()));
				sb.append(Cat.pound);
				sb.append(DateUtil.getTimeByDate(summonDbInfo.getExpiredTime()));
				sb.append(Cat.pound);
				Log4jManager.OFFLINE_SUMMON_DB_LOG.info(sb.toString());
			}
		}catch(Exception e){
			logger.error("saveRoleSummonLog error:",e);
		}
	}
	
	public void summonDeath(NpcInstance dieNpc, RoleInstance ownerInstance){
		String roleId = dieNpc.getSummonRoleId();
		if(null == roleId) {
			return;
		}
		int summonId = dieNpc.getSummonId();
		if(summonId <= 0) {
			return;
		}
		Summon summon = this.allSummonMap.get(summonId);
		if(null == summon) {
			return;
		}
		RoleInstance summonInstance = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		rewardFall(dieNpc, ownerInstance, summonInstance, summon);
	}
	
	private void rewardFall(NpcInstance dieNpc, RoleInstance ownerInstance, RoleInstance summonInstance, Summon summon) {
		try{
			if(!summon.isAwardSummonRole()) {
				GameContext.getFallApp().fallBox(dieNpc, ownerInstance, OutputConsumeType.monster_fall);
				return;
			}
			//发放全员物品奖励
			GameContext.getFallApp().summonFallBox(dieNpc, summon, ownerInstance, summonInstance);
			//发放全员奖励(除物品)
			for(AbstractRole itemRole : ownerInstance.getMapInstance().getRoleList()){
				boolean needNotify = false;
				int gainSilver = summon.getAllGainSilver();
				if (gainSilver > 0) {
					GameContext.getUserAttributeApp().changeRoleMoney(itemRole,
							AttributeType.gameMoney, OperatorType.Add, gainSilver,
							OutputConsumeType.goods_summon_output);
					needNotify = true;
				}
				int gainExp = summon.getAllGainExp();
				if (gainExp > 0) {
					GameContext.getUserAttributeApp().changeAttribute(itemRole,
							AttributeType.exp, OperatorType.Add, gainExp,
							OutputConsumeType.goods_summon_output);
					needNotify = true;
				}
				if (needNotify) {
					itemRole.getBehavior().notifyAttribute();
				}
			}
		}catch(Exception e){
			logger.error("saveRoleSummonLog error:",e);
		}
	}

	@Override
	public Summon getSummonById(int summonId) {
		return allSummonMap.get(summonId);
	}
	
	@Override
	public void resetSummonByCopyId(RoleInstance role, short copyId) {
		try {
			List<Summon> list = exitMapResetMap.get(copyId);
			if(Util.isEmpty(list)) {
				return;
			}
			for(Summon item : list) {
				if(null == item) {
					continue;
				}
				int itemId = item.getId();
				SummonDbInfo info = role.getSummonDbInfo().get(itemId);
				if(null == info) {
					continue;
				}
				info.setTimes((byte)0);
			}
		} catch (Exception e) {
			logger.error("SummonApp resetExchangeByType error:",e);
		}
	}

}
