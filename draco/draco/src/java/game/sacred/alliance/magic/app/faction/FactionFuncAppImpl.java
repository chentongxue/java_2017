//package sacred.alliance.magic.app.faction;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
//import sacred.alliance.magic.app.hint.HintId;
//import sacred.alliance.magic.base.AttributeType;
//import sacred.alliance.magic.base.FactionBuildFuncType;
//import sacred.alliance.magic.base.FactionRecordType;
//import sacred.alliance.magic.base.MoneyType;
//import sacred.alliance.magic.base.OperatorType;
//import sacred.alliance.magic.base.OutputConsumeType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.base.SaveDbStateType;
//import sacred.alliance.magic.base.XlsSheetNameType;
//import sacred.alliance.magic.channel.EmptyChannelSession;
//import sacred.alliance.magic.constant.Cat;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.channel.ChannelSession;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionBuild;
//import sacred.alliance.magic.domain.FactionContribute;
//import sacred.alliance.magic.domain.FactionRecord;
//import sacred.alliance.magic.domain.FactionRole;
//import sacred.alliance.magic.domain.FactionSkill;
//import sacred.alliance.magic.util.DateUtil;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.util.XlsPojoUtil;
//import sacred.alliance.magic.vo.RoleInstance;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.internal.C0074_FactionWarehouseInternalMessage;
//import com.game.draco.message.item.FactionDonateItem;
//import com.game.draco.message.response.C1728_FactionDonateRespMessage;
//import com.google.common.collect.Maps;
//
//public class FactionFuncAppImpl implements FactionFuncApp {
//	private final Logger logger = LoggerFactory.getLogger(this.getClass());
//	private Map<Integer,FactionDonate> factionDonateMap = Maps.newLinkedHashMap();//捐献门派贡献度配置
//	private Map<Integer, FactionBuild> factionCreateBuildMap = Maps.newHashMap();//公会创建时创建的建筑
//	private Map<Integer, List<FactionSalary>> factionSalaryMap = Maps.newHashMap();//公会工资配置
//	private Map<String, FactionSalary> salaryMap = Maps.newHashMap();//等级对应的工资配置
//	private Map<Integer, FactionActive> activeMap = Maps.newHashMap();//等级对应的工资配置
//	private Map<Integer, Map<Integer,FactionSkill>> skillConfigMap = Maps.newHashMap();//等级对应的工资配置
//	private static final ChannelSession emptyChannelSession = new EmptyChannelSession();
//	private static final int DEFAULT_LENGTH = 2;
//	
//	@Override
//	public void start() {
//		
//	}
//
//	@Override
//	public void stop() {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	@Override
//	public void setArgs(Object arg0) {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	@Override
//	public void init(){
//		//加载门派捐献配置
//		this.initFactionDonate();
//		//加载公会创建时创建的建筑信息
//		this.initCreateBuild();
//		//加载公会技能组配置
////		this.initFactionSkillGroup();
//		//加载公会技能配置信息
//		this.initFactionSkillConfig();
//		//加载公会工资配置
//		this.initFactionSalary();
//		//加载门派活动配置
//		this.initFactionActive();
//	}
//	
//	/**
//	 * 加载门派捐献配置
//	 */
//	private void initFactionDonate() {
//		String fileName = XlsSheetNameType.faction_donate.getXlsName();
//		String sheetName = XlsSheetNameType.faction_donate.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			factionDonateMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, FactionDonate.class);
//			if(Util.isEmpty(factionDonateMap)) {
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("factionDonate empty ,the system will shutdown .... ");
//			}
//		}catch (Exception e) {
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ",e);
//		}
//	}
//	
//	/**
//	 * 加载公会创建时创建的建筑信息
//	 */
//	private void initCreateBuild(){
//		Map<Integer, Map<Integer, FactionBuild>> buildMap = GameContext.getFactionApp().getBuildConfigMap();
//		for(Map<Integer, FactionBuild> factionBuildMap: buildMap.values()) {
//			for(FactionBuild build : factionBuildMap.values()) {
//				if(build.isCreate() && !factionCreateBuildMap.containsKey(build.getBuildId())) {
//					factionCreateBuildMap.put(build.getBuildId(), build);
//				}
//			}
//		}
//	}
//	
//	/**
//	 * 加载公会技能配置信息
//	 */
//	private void initFactionSkillConfig() {
//		String fileName = XlsSheetNameType.faction_skill.getXlsName();
//		String sheetName = XlsSheetNameType.faction_skill.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			Map<Integer, Map<Integer,FactionBuild>> factionBuildMap = GameContext.getFactionApp().getBuildConfigMap();
//			for(FactionSkill config : XlsPojoUtil.sheetToList(sourceFile, sheetName, FactionSkill.class)){
//				if(null == config){
//					continue;
//				}
//				
//				int skillId = config.getSkillId();
//				if(!this.skillConfigMap.containsKey(skillId)){
//					this.skillConfigMap.put(skillId, new HashMap<Integer,FactionSkill>());
//				}
//				this.skillConfigMap.get(skillId).put(config.getSkillLevel(), config);
//				
//				int buildId = config.getBuildId();
//				int buildLevel = config.getBuildLevel();
//				int skillLevel = config.getSkillLevel();
//				Map<Integer,FactionBuild> fbMap = factionBuildMap.get(buildId);
//				if(Util.isEmpty(fbMap)) {
//					continue;
//				}
//				FactionBuild fb = fbMap.get(buildLevel);
//				if(null == fb) {
//					continue;
//				}
//				Map<Integer,Integer> map = fb.getSkillMap();
//				if(!map.containsKey(skillId)){
//					map.put(skillId, skillLevel);
//					continue;
//				}
//				if(map.get(skillId) < skillLevel) {
//					map.put(skillId, skillLevel);
//				}
//			}
//		}catch(Exception e){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ",e);
//		}
//	}
//	
//	/**
//	 * 加载技能组配置
//	 */
////	private void initFactionSkillGroup(){
////		String fileName = XlsSheetNameType.faction_skill_group.getXlsName();
////		String sheetName = XlsSheetNameType.faction_skill_group.getSheetName();
////		try{
////			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
////			for(FactionSkillGroup factionSkillGroup : XlsPojoUtil.sheetToList(sourceFile, sheetName, FactionSkillGroup.class)){
////				if(null == factionSkillGroup){
////					continue;
////				}
////				int id = factionSkillGroup.getId();
////				int skillId = factionSkillGroup.getSkillId();
////				if(!this.skillGroupMap.containsKey(id)){
////					this.skillGroupMap.put(id, new HashMap<Integer,FactionSkillGroup>());
////				}
////				this.skillGroupMap.get(id).put(skillId, factionSkillGroup);
////			}
////		}catch(Exception e){
////			Log4jManager.checkFail();
////			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ",e);
////		}
////	}
//	
//	/**
//	 * 加载公会工资配置
//	 */
//	public void initFactionSalary(){
//		String fileName = XlsSheetNameType.faction_salary.getXlsName();
//		String sheetName = XlsSheetNameType.faction_salary.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			for(FactionSalary salary : XlsPojoUtil.sheetToList(sourceFile, sheetName, FactionSalary.class)) {
//				if(null == salary){
//					continue;
//				}
//				int factionLevel = salary.getFactionLevel();
//				if(!this.factionSalaryMap.containsKey(factionLevel)){
//					this.factionSalaryMap.put(factionLevel, new ArrayList<FactionSalary>());
//				}
//				this.factionSalaryMap.get(factionLevel).add(salary);
//			}
//		}catch(Exception e){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ",e);
//		}
//	}
//	
//	/**
//	 * 加载门派活动配置
//	 */
//	private void initFactionActive(){
//		String fileName = XlsSheetNameType.faction_active.getXlsName();
//		String sheetName = XlsSheetNameType.faction_active.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			activeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, FactionActive.class);
//			for(FactionActive active : activeMap.values()){
//				if(null == active){
//					continue;
//				}
//				//验证并初始化配置
//				active.init();
//			}
//		}catch(Exception e){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ",e);
//		}
//	}
//	
//	@Override
//	public C1728_FactionDonateRespMessage factionDonate(int id, RoleInstance role){
//		C1728_FactionDonateRespMessage resp = new C1728_FactionDonateRespMessage();
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//		if(null == faction || null == factionRole){
//			resp.setInfo(Status.Faction_Not_Own.getTips());
//			return resp;
//		}
//		
//		FactionDonate fd = factionDonateMap.get(id);
//		if(null == fd) {
//			resp.setInfo(Status.Faction_Donate_Null.getTips());
//			return resp;
//		}
//		
//		byte moneyType = fd.getMoneyType();
//		int money = fd.getMoney();
//		int contribute = fd.getContribute();
//		int factionMoney = fd.getFactionMoney();
//		
//		Result factionRoleResult = fd.canDonate(role, factionRole);
//		if(!factionRoleResult.isSuccess()) {
//			resp.setInfo(factionRoleResult.getInfo());
//			return resp;
//		}
//		
//		Map<Integer, Integer> donateMap = role.getFactionDonateMap();
//		int roleCount = 0;
//		if(donateMap.containsKey(id)) {
//			roleCount = donateMap.get(id);
//		}
//		donateMap.put(id, roleCount + 1);
//		
//		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.get(moneyType), OperatorType.Decrease, money, OutputConsumeType.faction_donate);
//		
//		Result re = new Result();
//		if(moneyType == MoneyType.rmb.getType() || faction.canDonate()){
//			re = GameContext.getFactionApp().changeContributeNum(role, OperatorType.Add, contribute);
//			Result changeMoneyResult = this.changeFactionMoney(faction, OperatorType.Add, factionMoney, OutputConsumeType.faction_money_donate, role.getRoleId());
//			if(!changeMoneyResult.isSuccess()) {
//				resp.setInfo(changeMoneyResult.getInfo());
//				return resp;
//			}
//		}else{
//			re = this.changeContributeNum(role, OperatorType.Add, contribute);
//			resp.setMaxCounts((byte)1);//到达每日最大次数
//		}
//		
//		if(!re.isSuccess()) {
//			resp.setInfo(re.getInfo());
//			return resp;
//		}
//		
//		if(moneyType == MoneyType.rmb.getType()) {
//			// 公会捐献记录
//			FactionRecord factionRecord = new FactionRecord();
//			factionRecord.setType(FactionRecordType.Faction_Record_Donate.getType());
//			factionRecord.setFactionId(faction.getFactionId());
//			factionRecord.setData1(money);
//			factionRecord.setData2(String.valueOf(contribute));
//			factionRecord.setData3(role.getRoleName());
//			GameContext.getFactionFuncApp().createFactionRecord(factionRecord);
//		}else{
//			int donateCount = faction.getDonateCount();
//			faction.setDonateCount(++donateCount);
//		}
//		resp.setAddFactionExp(contribute);
//		resp.setAddFactionMoney(factionMoney);
//		resp.setType(Result.SUCCESS);
//		return resp;
//	}
//	
//	/**
//	 * 门派金钱改变
//	 * @param faction
//	 * @param operatorType
//	 * @param value
//	 * @return
//	 */
//	public Result changeFactionMoney(Faction faction, OperatorType operatorType, int value, OutputConsumeType type, String roleId) {
//		Result result = new Result();
//		if(0 == value){
//			return result.success();
//		}
//		try {
//			if(null == faction){
//				return result.setInfo(Status.Faction_Not_Exist.getTips());
//			}
//			synchronized(faction.getFactionMoneyLock()){
//				int chanageValue = 0;
//				if(OperatorType.Add == operatorType){
//					chanageValue = value;
//				}else if(OperatorType.Decrease == operatorType){
//					chanageValue = -value;
//				}
//				
//				//门派金钱变化
//				int factionMoney = faction.getFactionMoney();
//				factionMoney += chanageValue;
//				if(factionMoney < 0){
//					return result.setInfo(Status.Faction_Money_Not_Enough.getTips());
//				}
//				faction.setFactionMoney(factionMoney);
//				faction.setSaveDbStateType(SaveDbStateType.Update);
//			}
//			this.printFactionMoneyLog(roleId, faction.getFactionId(), faction.getFactionName(), value, type);
//			return result.success();
//		}catch(Exception e){
//			this.logger.error("FactionFuncApp.changeFactionMoney error:", e);
//		}
//		return result.setInfo(Status.Faction_FAILURE.getTips());
//	}
//
//	/**
//	 * 获取捐献信息
//	 * @param roleLevel
//	 * @return
//	 */
//	@Override
//	public List<FactionDonateItem> getFactionDonateInfo(RoleInstance role) {
//		List<FactionDonateItem> list = new ArrayList<FactionDonateItem>();
//		try{
//			FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//			if(null == factionRole){
//				return list;
//			}
//			FactionDonateItem item = null;
//			for(FactionDonate fd : factionDonateMap.values()) {
//				if(null == fd) {
//					continue;
//				}
//				int id = fd.getId();
//				int maxCount = fd.getMaxCounts();
//				int donateCount = 0;
//				Map<Integer, Integer> donateMap = role.getFactionDonateMap();
//				if(donateMap.containsKey(id)){
//					donateCount = donateMap.get(id);
//					if(donateCount > maxCount) {
//						donateCount = maxCount;
//					}
//				}
//				item = new FactionDonateItem();
//				item.setId((byte)id);
//				item.setContribute(fd.getContribute());
//				item.setMaxCounts((byte)fd.getMaxCounts());
//				item.setFreeCounts((byte)donateCount);
//				item.setMoneyType(fd.getMoneyType());
//				item.setMoneyNum(fd.getMoney());
//				list.add(item);
//			}
//		}catch(Exception e){
//			this.logger.error("FactionFuncApp.getFactionDonateInfo error:", e);
//		}
//		return list;
//	}
//
//	@Override
//	public Map<Integer, FactionBuild> getFactionCreateBuilding() {
//		return factionCreateBuildMap;
//	}
//
//	@Override
//	public void upgradeFactionSkill(Faction faction, FactionBuild build) {
//		try{
//			if(null == build){
//				return;
//			}
//			Map<Integer, Integer> skillMap = build.getSkillMap();
//			if(Util.isEmpty(skillMap)){
//				return;
//			}
//			faction.getFactionSkillMap().clear();
//			faction.getFactionSkillMap().putAll(skillMap);
//		}catch(Exception e){
//			logger.error("FactionFuncApp upgradeFactionSkill error:" + e);
//		}
//	}
//
//	@Override
//	public Result createFactionBuild(RoleInstance role) {
//		Result result = new Result();
//		if(null == factionCreateBuildMap || factionCreateBuildMap.size() <= 0) {
//			return result.success();
//		}
//		for(FactionBuild factionBuild : factionCreateBuildMap.values()) {
//			try{
//				result = GameContext.getFactionApp().createBuilding(role, factionBuild.getBuildId());
//				if(!result.isSuccess()){
//					return result;
//				}
//			}catch(Exception e){
//				logger.error("FactionFuncApp.createFactionBuild error:", e);
//				return result.failure();
//			}
//		}
//		return result.success();
//	}
//	
//	@Override
//	public void checkBuild(RoleInstance role, Faction faction){
//		try{
//			Map<Integer,FactionBuild> buildMap = faction.getBuildingMap();
//			for(FactionBuild factionBuild : factionCreateBuildMap.values()) {
//				if(!buildMap.containsKey(factionBuild.getBuildId())) {
//					GameContext.getFactionApp().createBuilding(role, factionBuild.getBuildId());
//				}
//			}
//		}catch(Exception e){
//			this.logger.error("FactionFuncApp.checkBuildAndSkill error:", e);
//		}
//	}
//	
//	@Override
//	public FactionSkill getFactionSkill(RoleInstance role, int skillId, int skillLevel) {
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		if(null == faction){
//			return null;
//		}
//		Map<Integer,FactionSkill> skillMap = this.skillConfigMap.get(skillId); 
//		if(Util.isEmpty(skillMap)) {
//			return null;
//		}
//		return skillMap.get(skillLevel);
//	}
//
//	@Override
//	public int getFactionBuildLevel(RoleInstance role, String buildingId) {
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		if(null == faction){
//			return 0;
//		}
//		FactionBuild factionBuild = faction.getBuildingMap().get(Integer.parseInt(buildingId));
//		if(null == factionBuild){
//			return 0;
//		}
//		return factionBuild.getLevel();
//	}
//	
//	@Override
//	public int getFactionIntegral(RoleInstance role) {
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		if(null == faction){
//			return 0;
//		}
//		return faction.getIntegral();
//	}
//	
//	@Override
//	public int getFactionRoleContribute(RoleInstance role) {
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//		if(null == faction || null == factionRole){
//			return 0;
//		}
//		return factionRole.getContribution();
//	}
//
//	@Override
//	public Result factionSalary(RoleInstance role){
//		Result result = new Result();
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//		if(null == faction || null == factionRole){
//			return result.setInfo(Status.Faction_Not_Own.getTips());
//		}
//		
//		int factionLevel = faction.getFactionLevel();
//		int roleLevel = role.getLevel();
//		String key = factionLevel + Cat.underline + roleLevel;
//		FactionSalary factionSalary = salaryMap.get(key);
//		if(null == factionSalary) {
//			factionSalary = getFactionSalary(roleLevel, factionLevel);
//			if(null == factionSalary){
//				return result.setInfo(Status.Faction_Active_Err.getTips());
//			}
//		}
//		
//		result = factionSalary.canSalaryReceive(role);
//		if(!result.isSuccess()) {
//			return result;
//		}
//		
//		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsForBag(role, factionSalary.getGoodsId(), factionSalary.getGoodsNum(), OutputConsumeType.faction_salary);
//		if(!goodsResult.isSuccess()){
//			return goodsResult;
//		}
//		role.setFactionActiveTime(new Date());
//		role.setFactionSalaryCount((byte)(role.getFactionSalaryCount() + 1));
//		this.hintChange(role, HintId.Faction_Salary);
//		return result.success();
//	}
//	
//	@Override
//	public FactionSalary getFactionSalary(RoleInstance role){
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//		if(null == faction || null == factionRole){
//			return null;
//		}
//		
//		int factionLevel = faction.getFactionLevel();
//		int roleLevel = role.getLevel();
//		String key = factionLevel + Cat.underline + roleLevel;
//		
//		FactionSalary factionSalary = salaryMap.get(key);
//		if(factionSalary != null) {
//			return factionSalary;
//		}
//		
//		factionSalary = getFactionSalary(roleLevel, factionLevel);
//		if(null == factionSalary) {
//			return null;
//		}
//		if(!salaryMap.containsKey(key)) {
//			salaryMap.put(key, factionSalary);
//		}
//		return factionSalary;
//	}
//	
//	private FactionSalary getFactionSalary(int roleLevel, int factionLevel) {
//		FactionSalary factionSalary = null;
//		List<FactionSalary> list = factionSalaryMap.get(factionLevel);
//		if(Util.isEmpty(list)) {
//			return null;
//		}
//		
//		for(FactionSalary salary : list){
//			if(roleLevel >= salary.getRoleLevelMin() && roleLevel <= salary.getRoleLevelMax()) {
//				factionSalary = salary;
//				break;
//			}
//		}
//		return factionSalary;
//	}
//
//	@Override
//	public void createFactionRecord(FactionRecord factionRecord) {
//		try{
//			GameContext.getBaseDAO().insert(factionRecord);
//		}catch(Exception e){
//			logger.error("FactionFuncApp.createFactionRecord error:", e);
//		}
//	}
//
//	@Override
//	public List<FactionRecord> getFactionRecord(String factionId, int startRow, int rows) {
//		if(Util.isEmpty(factionId) || 0 == rows){
//			return null;
//		}
//		return GameContext.getFactionDAO().getFactionRecordList(factionId, startRow, rows);
//	}
//	
//	@Override
//	public void clearFactionRecord() {
//		try {
//			GameContext.getFactionDAO().deleteRecordBeforeOneMonth();
//		} catch (RuntimeException e) {
//			this.logger.error("FactionFuncApp.clearFactionRecord", e);
//		}
//	}
//
//	@Override
//	public FactionActive getFactionActive(int type) {
//		return activeMap.get(type);
//	}
//
//	@Override
//	public Result changeFactionMoney(RoleInstance role,
//			OperatorType operatorType, int value, OutputConsumeType type) {
//		Result result = new Result();
//		if(0 == value){
//			return result.success();
//		}
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//		if(null == faction || null == factionRole){
//			return result.failure();
//		}
//		result = changeFactionMoney(faction, operatorType, value, type, role.getRoleId());
//		return result;
//	}
//	
//	@Override
//	public int getWarehouseCapacity(RoleInstance role) {
//		int capacity = 0;
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		if(null == faction){
//			return capacity;
//		}
//		
//		Map<Integer,FactionBuild> buildMap = faction.getBuildingMap();
//		if(Util.isEmpty(buildMap)) {
//			return capacity;
//		}
//		
//		for(FactionBuild fb : buildMap.values()) {
//			if(fb.getType() == FactionBuildFuncType.Faction_Warehouse) {
//				capacity = fb.getFunction();
//				break;
//			}
//		}
//		return capacity;
//	}
//
//	@Override
//	public FactionBuild getFactionBuildById(RoleInstance role, int buildId) {
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		FactionBuild factionBuild = null;
//		if(null == faction){
//			return factionBuild;
//		}
//		Map<Integer,FactionBuild> buildMap = faction.getBuildingMap();
//		if(Util.isEmpty(buildMap)) {
//			return factionBuild;
//		}
//		FactionBuild fb = buildMap.get(buildId);
//		if(null == fb) {
//			return factionBuild;
//		}
//		
//		Map<Integer, Map<Integer,FactionBuild>> buildConfigMap = GameContext.getFactionApp().getBuildConfigMap();
//		if(Util.isEmpty(buildConfigMap)) {
//			return factionBuild;
//		}
//		return buildConfigMap.get(buildId).get(fb.getLevel());
//	}
//
//	@Override
//	public FactionBuild getFactionBuildByType(RoleInstance role, FactionBuildFuncType type) {
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		FactionBuild factionBuild = null;
//		if(null == faction){
//			return factionBuild;
//		}
//		Map<Integer,FactionBuild> buildMap = faction.getBuildingMap();
//		if(Util.isEmpty(buildMap)) {
//			return factionBuild;
//		}
//		
//		for(FactionBuild fb : buildMap.values()) {
//			FactionBuild factionBuildConfig = buildMap.get(fb.getBuildId());
//			if(null == factionBuildConfig){
//				continue;
//			}
//			if(factionBuildConfig.getType() == type) {
//				factionBuild = factionBuildConfig;
//				break;
//			}
//		}
//		return factionBuild;
//	}
//	
//	@Override
//	public Result changeContributeNum(RoleInstance role, OperatorType operatorType, int value) {
//		Result result = new Result();
//		if(0 == value){
//			return result.success();
//		}
//		Faction faction = GameContext.getFactionApp().getFaction(role);
//		FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//		if(null == faction || null == factionRole){
//			return result.setInfo( GameContext.getI18n().getText(TextId.FACTION_NOT_HAVE_FACTION));
//		}
//		int chanageValue = 0;
//		//原有门派贡献--从门派成员上取值
//		int contribute = factionRole.getContribution();
//		if(OperatorType.Add == operatorType){
//			chanageValue = value;
//		}else if(OperatorType.Decrease == operatorType){
//			chanageValue = -value;
//		}
//		contribute += chanageValue;
//		if(contribute < 0){
//			contribute = 0;
//		}
//		//修改角色身上的贡献度
//		String factionId = faction.getFactionId();
//		FactionContribute fcb = role.getFactionContribute();
//		if(null == fcb){
//			fcb = new FactionContribute();
//			fcb.setRoleId(role.getRoleId());
//			fcb.setFactionId(factionId);
//			fcb.setSaveDbStateType(SaveDbStateType.Insert);
//			role.getFactionContributeMap().put(factionId, fcb);
//		}
//		fcb.setContribute(contribute);
//		
//		if(SaveDbStateType.Insert != fcb.getSaveDbStateType()){
//			fcb.setSaveDbStateType(SaveDbStateType.Update);
//		}
//		factionRole.setContribution(contribute);
//		//同步门派贡献度
//		role.getBehavior().notifyAttribute();
//		if(OperatorType.Add == operatorType){
//			fcb.setTotalContribute(fcb.getTotalContribute() + chanageValue);
//			factionRole.setTotalContribution(factionRole.getTotalContribution() + chanageValue);
//		}
//		return result.success();
//	}
//	
//	@Override
//	public void expansionWarehouse(Faction faction, FactionBuild build) {
//		try{
//			for(RoleInstance role: GameContext.getFactionApp().getAllOnlineFactionRole(faction)){
//				if(null == role){
//					continue;
//				}
//				if(null == role.getWarehousePack()) {
//					continue;
//				}
//				
//				if(role.getWarehoseCapacity() >= build.getFunction()) {
//					continue;
//				}
//				
//				C0074_FactionWarehouseInternalMessage internalReqMsg = new C0074_FactionWarehouseInternalMessage();
//				internalReqMsg.setRoleId(role.getRoleId());
//				internalReqMsg.setCapacity(build.getFunction());
//				GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(), 
//						internalReqMsg, emptyChannelSession, true);
//			}
//		}catch(Exception e){
//			this.logger.error("FactionFuncApp.clearFactionRecord", e);
//		}
//	}
//
//	@Override
//	public Set<HintId> getHintIdSet(RoleInstance role) {
//		Set<HintId> set = new HashSet<HintId>();
//		if(this.hasFactionSalary(role)){
//			set.add(HintId.Faction_Salary);
//		}
//		return set;
//	}
//
//	private boolean hasFactionSalary(RoleInstance role) {
//		if(!role.hasFaction()){
//			return false;
//		}
//		FactionSalary factionSalary = this.getFactionSalary(role);
//		if(null == factionSalary){
//			return false;
//		}
//		return factionSalary.canSalaryReceive(role).isSuccess();
//	}
//
//	@Override
//	public void hintChange(RoleInstance role, HintId hintId) {
//		try {
//			GameContext.getHintApp().hintChange(role, hintId, this.hasFactionSalary(role));
//		} catch (Exception e) {
//			this.logger.error("FactionFuncApp.hintChange error: ", e);
//		}
//	}
//
//	@Override
//	public void roleLoginInitDonate(RoleInstance role) {
//		try{
//			String factionDonate = role.getFactionDonate();
//			if(Util.isEmpty(factionDonate)) {
//				return;
//			}
//			if(!DateUtil.sameDay(new Date(), role.getFactionActiveTime())){
//				return;
//			}
//			String[] donateArr = factionDonate.split(Cat.semicolon);
//			for(String arr:donateArr) {
//				String[] countArr = arr.split(Cat.comma);
//				if(countArr.length < DEFAULT_LENGTH) {
//					continue;
//				}
//				role.getFactionDonateMap().put(Integer.parseInt(countArr[0]), Integer.parseInt(countArr[1]));
//			}
//		}catch(Exception e){
//			logger.error("FactionFuncApp.roleLoginInitDonate error:",e);
//		}
//	}
//
//	@Override
//	public void roleOffLineUniteDonate(RoleInstance role) {
//		try{
//			Map<Integer, Integer> roleDonateMap = role.getFactionDonateMap();
//			if(Util.isEmpty(roleDonateMap)) {
//				role.setFactionDonate("");
//				return;
//			}
//			StringBuffer sb = new StringBuffer();
//			String cat = "";
//			for(Integer id : roleDonateMap.keySet()){
//				if(null == id) {
//					continue;
//				}
//				sb.append(cat);
//				sb.append(id);
//				sb.append(Cat.comma);
//				sb.append(roleDonateMap.get(id));
//				cat = Cat.semicolon;
//			}
//			role.setFactionDonate(sb.toString());
//		}catch (Exception e) {
//			logger.error("FactionFuncApp.roleOffLineUniteDonate error:",e);
//		}
//	}
//	
//	private void printFactionMoneyLog(String roleId, String factionId, String factionName, int factionMoney, OutputConsumeType type){
//		try{
//			int roleIntId = -1;
//			if(!Util.isEmpty(roleId)){
//				roleIntId = Integer.parseInt(roleId);
//			}
//			StringBuffer sb = new StringBuffer();
//			sb.append(factionId);
//			sb.append(Cat.pound);
//			sb.append(factionName);
//			sb.append(Cat.pound);
//			sb.append(roleIntId);
//			sb.append(Cat.pound);
//			sb.append(factionMoney);
//			sb.append(Cat.pound);
//			sb.append(type.getType());
//			Log4jManager.FACTION_MONEY.info(sb.toString());
//		}catch(Exception e){
//			logger.error("printFactionMoneyLog error",e);
//		}
//	}
//}
