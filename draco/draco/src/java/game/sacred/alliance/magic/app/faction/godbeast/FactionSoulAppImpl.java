//package sacred.alliance.magic.app.faction.godbeast;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import sacred.alliance.magic.base.FactionPowerType;
//import sacred.alliance.magic.base.FactionRecordType;
//import sacred.alliance.magic.base.OperatorType;
//import sacred.alliance.magic.base.OutputConsumeType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.base.XlsSheetNameType;
//import sacred.alliance.magic.constant.Cat;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.domain.RoleGoods;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.util.XlsPojoUtil;
//import sacred.alliance.magic.vo.RoleInstance;
//
//import com.game.draco.GameContext;
//import com.game.draco.app.npc.domain.NpcTemplate;
//import com.game.draco.app.skill.vo.Skill;
//import com.google.common.collect.Maps;
//
//public class FactionSoulAppImpl implements FactionSoulApp{
//	private final Logger logger = LoggerFactory.getLogger(this.getClass());
//	private Map<Integer,FactionSoulInfo> factionSoulMap = Maps.newHashMap();//神兽配置
//	private Map<Integer,Map<Integer, FactionSoulFeed>> feedMap = Maps.newHashMap();//喂养配置
//	private Map<Integer,Map<Integer, FactionSoulFlyConfig>> flyMap = Maps.newHashMap();//飞升配置
//	private Map<Integer,Map<String, List<FactionSoulInspireConfig>>> inspireMap = Maps.newHashMap();//神兽鼓舞配置
//	private Map<Short,Map<Integer, FactionSoulInspireBuffConfig>> inspireBuffMap = Maps.newHashMap();//神兽鼓舞buff配置
//	
//	@Override
//	public void start() {
//		
//	}
//	
//	@Override
//	public void setArgs(Object arg0) {
//		
//	}
//	
//	@Override
//	public void init(){
//		this.loadFeed();
//		this.loadFactionSoulInfo();
//		this.loadFactionSoulAttri();
//		this.loadSoulFlyConfig();
//		this.loadInspireConfig();
//		this.loadInspireBuffConfig();
//	}
//	
//	@Override
//	public FactionSoulInfo getFactionSoulInfo(int soulId){
//		if(null == factionSoulMap || 0 == factionSoulMap.size()){
//			return null;
//		}
//		return factionSoulMap.get(soulId);
//	}
//	
////	
////	private void sendFactionSoulListRespMessage(RoleInstance role,int soulId,FactionSoulAttri soulAttri){
////		FactionSoulListRespMessage resp = new FactionSoulListRespMessage();
////		Faction faction = role.getFaction();
////		if(null == faction){
////			return ;
////		}
////		List<FactionSoulItem> itemList = new ArrayList<FactionSoulItem>();
////		this.packItemList(role, faction, this.getFactionSoulInfo(soulId), itemList);
////		resp.setItemList(itemList);
////		role.getBehavior().sendMessage(resp);
////	}
////	
////	private void packItemList(RoleInstance role,Faction faction,FactionSoulInfo info,List<FactionSoulItem> itemList){
////		FactionSoulItem item = new FactionSoulItem();
////		int soulId = info.getId();
////		FactionSoulFeedObj feedObj = faction.getFactionSoulFeedObj(role.getRoleId(),soulId);
////		byte level = 1;
////		if(null != feedObj){
////			item.setCommon(feedObj.getCommon());
////			item.setSenior(feedObj.getSenior());
////		}
////		FactionSoulRecord record = faction.getFactionSoulRecord(soulId);
////		item.setOpen((byte)info.getFactionLevel());//开启条件
////		if(null != record){
////			item.setCurrGrowValue(record.getGrowValue());
////			level = (byte) record.getLevel();
////			item.setState(record.getState());
////			item.setOpen((byte)0);//开启状态
////		}
////		item.setId(soulId);
////		FactionSoulAttri attri = info.getFactionSoulAttri(level);
////		if(null != attri){
////			item.setMaxGrowValue(attri.getGrowValue());
////			item.setDesc(attri.getDesc());
////		}
////		item.setFiveAttri(info.getFiveAttri());
////		item.setMaxCommon((byte) feedNumObj.getNumber());
////		item.setMaxSenior((byte) feedNumObj.getPayNumber());
////		item.setName(info.getName());
////		item.setRedId(info.getResId());
////		item.setSkillList(this.getFactionSoulSkill(role,info,record));
////		itemList.add(item);
////	}
////	
////	//封装神兽技能列表
////	private List<FactionSoulSkillItem> getFactionSoulSkill(RoleInstance role ,FactionSoulInfo info,FactionSoulRecord record){
////		List<FactionSoulSkillItem> skillList = new ArrayList<FactionSoulSkillItem>();
////		for(SkillListItem item : info.getSkillList()){
////			FactionSoulSkillItem skillItem = new FactionSoulSkillItem();
////			Skill skill = GameContext.getSkillApplication().getSkill(item.getSkill());
////			if(null == skill){
////				continue ;
////			}
////			SkillDetail skillDetail = skill.getSkillDetail(item.getSkillLevel());
////			if(null == skillDetail){
////				continue ;
////			}
////			if(SkillAttackType.NormalAttack.getType() == skillDetail.getAttackType()){
////				continue ;
////			}
////			skillItem.setImgId(skillDetail.getIconId());
////			skillItem.setSkillDec(skillDetail.getDesc());
////			int level = null == record?item.getSkillLevel():record.getLevel();
////			skillItem.setUseState((byte) (info.isUseSkill(item.getSkill(),level)?1:0));
////			skillItem.setSkillName(skill.getName());
////			skillItem.setApplyType(skill.getSkillApplyType().getType());
////			skillList.add(skillItem);
////		}
////		return skillList;
////	}
//	
//	@Override
//	public void stop() {
//		
//	}
//	
//	/**
//	 * 加载飞升配置
//	 */
//	private void loadSoulFlyConfig(){
//		String path = GameContext.getPathConfig().getXlsPath();
//		String fileName = XlsSheetNameType.god_beast_fly.getXlsName();
//		String sheetName = XlsSheetNameType.god_beast_fly.getSheetName();
//		String sourceFile = path + fileName;
//		List<FactionSoulFlyConfig> flyConfigList = XlsPojoUtil.sheetToList(sourceFile,sheetName, FactionSoulFlyConfig.class);
//		if (flyConfigList == null || flyConfigList.size() <= 0) {
//			Log4jManager.CHECK.error("load FactionSoulFlyConfig is null");
//			Log4jManager.checkFail();
//			return ;
//		}
//		
//		List<Short> list = new ArrayList<Short>();
//		for(FactionSoulFlyConfig config : flyConfigList){
//			if(null == config){
//				continue;
//			}
//			int beastId = config.getId();
//			if(!this.flyMap.containsKey(beastId)){
//				this.flyMap.put(beastId, new HashMap<Integer,FactionSoulFlyConfig>());
//				list = new ArrayList<Short>();
//			}
//			list.add(config.getSkillId());
//			List<Short> skillList = new ArrayList<Short>();
//			skillList.addAll(list);
//			config.setSkillList(skillList);
//			this.flyMap.get(beastId).put(config.getFlyNum(), config);
//		}
//	}
//	
//	/**
//	 * 神兽属性配置
//	 */
//	private void loadFactionSoulAttri(){
//		String path = GameContext.getPathConfig().getXlsPath();
//		String fileName = XlsSheetNameType.god_beast_attribute.getXlsName();
//		String sheetName = XlsSheetNameType.god_beast_attribute.getSheetName();
//		String sourceFile = path + fileName;
//		List<FactionSoulAttri> list = XlsPojoUtil.sheetToList(sourceFile,sheetName, FactionSoulAttri.class);
//		if (list == null || list.size() <= 0) {
//			Log4jManager.CHECK.error("load loadGodBeastAttri is null");
//			Log4jManager.checkFail();
//			return ;
//		}
//		
//		for(FactionSoulAttri attri : list) {
//			NpcTemplate npc = GameContext.getNpcApp().getNpcTemplate(attri.getNpcId());
//			if(null == npc) {
//				continue;
//			}
//			attri.initShowAttr(npc);
//			FactionSoulInfo info = factionSoulMap.get(attri.getId());
//			if(null == info) {
//				continue;
//			}
//			info.initAttriMap(attri);
//		}		
//	}
//	
//	/**
//	 * 加载神兽配置
//	 */
//	private void loadFactionSoulInfo(){
//		String path = GameContext.getPathConfig().getXlsPath();
//		String fileName = XlsSheetNameType.god_beast.getXlsName();
//		String sheetName = XlsSheetNameType.god_beast.getSheetName();
//		String sourceFile = path + fileName;
//		factionSoulMap = XlsPojoUtil.sheetToGenericMap(sourceFile,sheetName, FactionSoulInfo.class);
//		if (factionSoulMap == null || factionSoulMap.size() <= 0) {
//			Log4jManager.CHECK.error("load loadGodBeastInfo is null");
//			Log4jManager.checkFail();
//			return ;
//		}
//	}
//	
//	/**
//	 * 加载喂养配置
//	 */
//	private void loadFeed(){
//		String path = GameContext.getPathConfig().getXlsPath();
//		String fileName = XlsSheetNameType.god_beast_feed.getXlsName();
//		String sheetName = XlsSheetNameType.god_beast_feed.getSheetName();
//		String sourceFile = path + fileName;
//		
//		for(FactionSoulFeed config : XlsPojoUtil.sheetToList(sourceFile, sheetName, FactionSoulFeed.class)){
//			if(null == config){
//				continue;
//			}
//			config.init();
//			int beastId = config.getId();
//			if(!this.feedMap.containsKey(beastId)){
//				this.feedMap.put(beastId, new HashMap<Integer,FactionSoulFeed>());
//			}
//			this.feedMap.get(beastId).put(config.getLevel(), config);
//		}
//	}
//	
//	/**
//	 * 加载鼓舞配置
//	 */
//	private void loadInspireConfig(){
//		String path = GameContext.getPathConfig().getXlsPath();
//		String fileName = XlsSheetNameType.god_beast_inspire.getXlsName();
//		String sheetName = XlsSheetNameType.god_beast_inspire.getSheetName();
//		String sourceFile = path + fileName;
//		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
//		try{
//			List<FactionSoulInspireConfig> list = XlsPojoUtil.sheetToList(sourceFile,sheetName, FactionSoulInspireConfig.class);
//			if (Util.isEmpty(list)) {
//				Log4jManager.CHECK.error("load FactionSoulInspireConfig is null");
//				Log4jManager.checkFail();
//				return ;
//			}
//			
//			for(FactionSoulInspireConfig config : list){
//				if(null == config){
//					continue;
//				}
//				config.check(info);
//				int beastId = config.getId();
//				if(!this.inspireMap.containsKey(beastId)){
//					this.inspireMap.put(beastId, new HashMap<String, List<FactionSoulInspireConfig>>());
//				}
//				Map<String, List<FactionSoulInspireConfig>> map = inspireMap.get(beastId);
//				
//				String key = getInspireKey(config.getFlyLevel(), config.getLevel());
//				if(!map.containsKey(key)){
//					map.put(key, new ArrayList<FactionSoulInspireConfig>());
//				}
//				map.get(key).add(config);
//			}
//		}catch(Exception e){
//			logger.error("loadInspireConfig error",e);
//		}
//	}
//	
//	private String getInspireKey(int flyLevel, int level){
//		return flyLevel + Cat.underline + level;
//	}
//	
//	/**
//	 * 加载神兽鼓舞buff配置
//	 */
//	private void loadInspireBuffConfig(){
//		String path = GameContext.getPathConfig().getXlsPath();
//		String fileName = XlsSheetNameType.god_beast_inspire_buff.getXlsName();
//		String sheetName = XlsSheetNameType.god_beast_inspire_buff.getSheetName();
//		String sourceFile = path + fileName;
//		String info = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
//		try{
//			List<FactionSoulInspireBuffConfig> list = XlsPojoUtil.sheetToList(sourceFile,sheetName, FactionSoulInspireBuffConfig.class);
//			if (Util.isEmpty(list)) {
//				Log4jManager.CHECK.error("load FactionSoulInspireBuffConfig is null");
//				Log4jManager.checkFail();
//				return ;
//			}
//			
//			for(FactionSoulInspireBuffConfig config : list){
//				if(null == config){
//					continue;
//				}
//				config.check(info);
//				short buffId = config.getBuffId();
//				if(!this.inspireBuffMap.containsKey(buffId)){
//					this.inspireBuffMap.put(buffId, new HashMap<Integer,FactionSoulInspireBuffConfig>());
//				}
//				inspireBuffMap.get(buffId).put(config.getBuffLevel(), config);
//			}
//		}catch(Exception e){
//			logger.error("loadInspireBuffConfig error",e);
//		}
//	}
//
//	@Override
//	public Result createFactionSoul(Faction faction){
//		Result result = new Result();
//		try{
//			for(FactionSoulInfo info : factionSoulMap.values()){
//				FactionSoulAttri attri = info.getSoulAttriByFactionLevel();
//				if(null == attri){
//					break ;
//				}
//				FactionSoulRecord record = FactionSoulRecord.createFactionSoulRecord(faction, attri.getId(), attri.getLevel());
//				if(null == faction.getFactionSoulRecord()){
//					faction.setFactionSoulRecord(record);
//					GameContext.getBaseDAO().insert(record);
//				}
//			}
//		}catch(Exception e){
//			this.logger.error("FactionSoulApp.createFactionSoul error:", e);
//			return result.failure();
//		}
//		return result.success();
//	}
//	
//	@Override
//	public C1730_FactionSoulFeedRespMessage feed(RoleInstance role){
//		C1730_FactionSoulFeedRespMessage resp = new C1730_FactionSoulFeedRespMessage();
//		Status status = this.feedCondition(role);
//		if(!status.isSuccess()){
//			resp.setInfo(status.getTips());
//			return resp;
//		}
//		Faction faction = role.getFaction();
//		return this.feedSoul(role, faction);
//	}
//	
//	private Status feedCondition(RoleInstance role){
//		Faction faction = role.getFaction();
//		if(null == faction){
//			return Status.Faction_Not_Own;
//		}
//		
//		FactionSoulRecord record = faction.getFactionSoulRecord();
//		if(null == record){
//			return Status.Faction_Soul_Null;
//		}
//		int soulId = record.getSoulId();
//		
//		Map<Integer, FactionSoulFeed> factionSoulFeedMap = feedMap.get(soulId);
//		if(null == factionSoulFeedMap || 0 == factionSoulFeedMap.size()) {
//			return Status.Faction_Soul_Null;
//		}
//		
//		FactionSoulFeed factionSoulFeed = factionSoulFeedMap.get(record.getLevel());
//		if(null == factionSoulFeed){
//			return Status.Faction_Soul_Null;
//		}
//		
//		List<RoleGoods> roleGoodsList = role.getRoleBackpack().getRoleGoodsByGoodsId(factionSoulFeed.getGoodsId());
//		if(null == roleGoodsList || 0 == roleGoodsList.size() || factionSoulFeed.getGoodsNum() > roleGoodsList.size()) {
//			return Status.GOODS_NO_ENOUGH;
//		}
//		
//		FactionSoulInfo info = this.getFactionSoulInfo(soulId);
//		if(null == info){
//			return Status.Faction_Soul_Null;
//		}
//		FactionSoulAttri soulAttri = info.getFactionSoulAttri((byte) record.getLevel());
//		if(null == soulAttri){
//			return Status.Faction_Soul_No_Level;
//		}
//		
//		FactionSoulAttri nextSoulAttri = info.getFactionSoulAttri((byte)(record.getLevel() + 1));
//		if(null == nextSoulAttri && soulAttri.isFull(record.getGrowValue())){
//			return Status.Faction_Soul_No_Feed;
//		}
//		return Status.SUCCESS;
//	}
//	
//	private C1730_FactionSoulFeedRespMessage feedSoul(RoleInstance role,Faction faction){
//		C1730_FactionSoulFeedRespMessage resp = new C1730_FactionSoulFeedRespMessage();
//		FactionSoulRecord record = faction.getFactionSoulRecord();
//		int soulId = record.getSoulId();
//		int soulLevel = record.getLevel();
//		Map<Integer, FactionSoulFeed> factionSoulFeedMap = feedMap.get(soulId);
//		FactionSoulFeed factionSoulFeed = factionSoulFeedMap.get(record.getLevel());
//		FactionSoulInfo info = this.getFactionSoulInfo(soulId);
//		FactionSoulAttri soulAttri = info.getFactionSoulAttri((byte) record.getLevel());
//		if(null == soulAttri){
//			resp.setInfo(Status.Faction_Soul_No_Level.getTips());
//			return resp;
//		}
//		//删除物品
//		int goodsId = factionSoulFeed.getGoodsId();
//		int goodsNum = factionSoulFeed.getGoodsNum();
//		int growValue = factionSoulFeed.getGrowValue();
//		int crit = 1;
//		Integer critNum = factionSoulFeed.getWeightCalct();
//		if(critNum != null && critNum.intValue() > 1) {
//			crit = critNum.intValue();
//			resp.setCrit((byte)1);//暴击
//			resp.setCritNum((byte)crit);
//		}
//		growValue = growValue * crit;
//		FactionSoulAttri nextLevelSoulAttri = info.getFactionSoulAttri((byte) (record.getLevel()+1));
//		boolean isUpLevel = false;
//		int grow = 0;
//		synchronized(faction.getFactionSoulFeedLock()){
//			if(soulLevel != record.getLevel()) {
//				resp.setInfo(Status.Faction_Soul_Feed_Failure.getTips());
//				return resp;
//			}
//			grow = growValue + record.getGrowValue();
//			if(soulAttri.isUpLevel(grow)) {
//				if(null != nextLevelSoulAttri) {
//					grow -= nextLevelSoulAttri.getGrowValue();
//					record.setLevel(nextLevelSoulAttri.getLevel());
//					isUpLevel = true;
//				}else{
//					grow = soulAttri.getGrowValue();
//				}
//			}
//			record.setGrowValue(grow>0?grow:0);
//		}
//		
//		GameContext.getUserGoodsApp().deleteForBag(role, goodsId, goodsNum, OutputConsumeType.faction_soul_feed);
//		
//		if(isUpLevel){
//			updateFactionSoulRecord(record);
//			//发送升级消息
//			FactionSoulFeed upLevelFeed = factionSoulFeedMap.get(record.getLevel());
//			if(null == upLevelFeed) {
//				upLevelFeed = factionSoulFeed;
//			}
//			//神兽升级记录
//			FactionRecord factionRecord = new FactionRecord();
//			factionRecord.setType(FactionRecordType.Faction_Record_Soul_Upgrade.getType());
//			factionRecord.setFactionId(faction.getFactionId());
//			factionRecord.setData1(record.getLevel());
//			GameContext.getFactionFuncApp().createFactionRecord(factionRecord);
//		}
//		resp.setGrowValue(growValue);
//		GameContext.getFactionApp().changeContributeNum(role, OperatorType.Add,  factionSoulFeed.getContribute());
//		resp.setType((byte)1);
//		return resp;
//	}
//	
//	private void updateFactionSoulRecord(FactionSoulRecord record) {
//		try{
//			GameContext.getBaseDAO().update(record);
//		}catch (Exception e) {
//			logger.error("FactionSoulApp.updateFactionSoulRecord error:", e);
//		}
//	}
//
//	@Override
//	public void updateFactionSoul(Faction faction) {
//		FactionSoulRecord record = faction.getFactionSoulRecord();
//		updateFactionSoulRecord(record);
//	}
//	
//	@Override
//	public void initFactionSoul(Faction faction){
//		try{
//			FactionSoulRecord record = GameContext.getBaseDAO().selectEntity(FactionSoulRecord.class, "factionId", faction.getFactionId());
//			if(null == record){
//				return ;
//			}
//			faction.setFactionSoulRecord(record);
//		}catch(Exception e){
//			logger.error("FactionSoulApp.initFactionSoul error:", e);
//		}
//	}
//	
//	
//	public Result canFly(RoleInstance role,int soulId){
//		Result result = new Result();
//		Faction faction = role.getFaction();
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Own.getTips());
//		}
//		
//		if(!GameContext.getFactionApp().getPowerTypeSet(role).contains(FactionPowerType.SoulFly)){
//			return result.setInfo(Status.Faction_Skill_Upgrade_No_Position.getTips());
//		}
//		
//		FactionSoulRecord record = faction.getFactionSoulRecord();
//		if(null == record){
//			return result.setInfo(Status.Faction_Soul_Null.getTips());
//		}
//		
//		Map<Integer, FactionSoulFlyConfig> soulFlyConfigMap = flyMap.get(soulId);
//		FactionSoulFlyConfig config = soulFlyConfigMap.get(record.getFlyNum() + 1);
//		if(null == config) {
//			return result.setInfo(Status.Faction_Soul_Fly_Max.getTips());
//		}
//		
//		if(record.getLevel() < config.getFlyLevel()) {
//			return result.setInfo(Status.Faction_Soul_Not_Enough.getTips());
//		}
//		
//		//List<RoleGoods> roleGoodsList = role.getRoleBackpack().getRoleGoodsByGoodsId(config.getFlyGoodsId());
//		int goodsSize = role.getRoleBackpack().countByGoodsId(config.getFlyGoodsId());
//		if(config.getFlyGoodsNum() > goodsSize) {
//			return result.setInfo(Status.GOODS_NO_ENOUGH.getTips());
//		}
//		
//		return result.success();
//	}
//
//	@Override
//	public Result factionSoulFly(RoleInstance role, int soulId) {
//		Result result = new Result();
//		try{
//			result = canFly(role, soulId);
//			if(!result.isSuccess()) {
//				return result;
//			}
//			
//			Faction faction = role.getFaction();
//			FactionSoulRecord record = faction.getFactionSoulRecord();
//			int flyNum = record.getFlyNum();
//			Map<Integer, FactionSoulFlyConfig> soulFlyConfigMap = flyMap.get(soulId);
//			FactionSoulFlyConfig config = soulFlyConfigMap.get(record.getFlyNum() + 1);
//			
//			synchronized (faction.getFactionSoulFlyLock()) {
//				if(flyNum != record.getFlyNum()) {
//					return result.setInfo(Status.Faction_Soul_Fly_Failure.getTips());
//				}
//				record.setFlyNum(record.getFlyNum() + 1);
//				updateFactionSoulRecord(record);
//			}
//			//删除物品
//			GameContext.getUserGoodsApp().deleteForBag(role, config.getFlyGoodsId(), config.getFlyGoodsNum(), OutputConsumeType.faction_soul_fly);
//			
//			//神兽飞升记录
//			FactionRecord factionRecord = new FactionRecord();
//			factionRecord.setType(FactionRecordType.Faction_Record_Soul_Fly.getType());
//			factionRecord.setFactionId(faction.getFactionId());
//			factionRecord.setData1(record.getFlyNum());
//			GameContext.getFactionFuncApp().createFactionRecord(factionRecord);
//		}catch(Exception e){
//			logger.error("FactionSoulApp.factionSoulFly error:", e);
//			return result.setInfo(Status.Faction_FAILURE.getTips());
//		}
//		return result.success();
//	}
//	
//	@Override
//	public FactionSoulFeed getFactionSoulFeed(int soulId, int level){
//		return feedMap.get(soulId).get(level);
//	}
//	
//	@Override
//	public FactionSoulFlyConfig getFactionSoulFly(int soulId, int flyNum){
//		return flyMap.get(soulId).get(flyNum);
//	}
//
//	@Override
//	public int getFactionSoulMaxFly(int soulId) {
//		return flyMap.get(soulId).size();
//	}
//
//	@Override
//	public List<FactionSoulSkillItem> getFactionSoulSkill(int soulId, int flyNum) {
//		Map<Integer, FactionSoulFlyConfig> soulMap = flyMap.get(soulId);
//		List<FactionSoulSkillItem> skillList = new ArrayList<FactionSoulSkillItem>();
//		if(null == soulMap) {
//			return skillList;
//		}
//		
//		FactionSoulFlyConfig config = soulMap.get(flyNum);
//		if(null == config) {
//			return skillList;
//		}
//		
//		for(Short skillId:config.getSkillList()){
//			FactionSoulSkillItem item = new FactionSoulSkillItem();
//			Skill skill = GameContext.getSkillApp().getSkill(skillId);
//			item.setSkillId(skillId);
//			item.setSkillName(skill.getName());
//			item.setLevel((byte)1);
//			item.setImgId(skill.getIconId());
//			item.setApplyType(skill.getSkillApplyType().getType());
//			skillList.add(item);
//		}
//		return skillList;
//	}
//
//	@Override
//	public List<FactionSoulSkillItem> getNextFactionSoulSkill(int soulId, int flyNum) {
//		Map<Integer, FactionSoulFlyConfig> soulMap = flyMap.get(soulId);
//		List<FactionSoulSkillItem> skillList = new ArrayList<FactionSoulSkillItem>();
//		if(null == soulMap) {
//			return skillList;
//		}
//		
//		FactionSoulFlyConfig config = soulMap.get(flyNum + 1);
//		if(null == config) {
//			return skillList;
//		}
//		Short skillId = config.getSkillId();
//		FactionSoulSkillItem item = new FactionSoulSkillItem();
//		Skill skill = GameContext.getSkillApp().getSkill(skillId);
//		item.setSkillId(skillId);
//		item.setSkillName(skill.getName());
//		item.setLevel((byte)1);
//		item.setImgId(skill.getIconId());
//		item.setApplyType(skill.getSkillApplyType().getType());
//		skillList.add(item);
//		return skillList;
//	}
//
//	@Override
//	public List<FactionSoulInspireConfig> getSoulInspire(String factionId) {
//		List<FactionSoulInspireConfig> list = new ArrayList<FactionSoulInspireConfig>();
//		try {
//			Faction faction = GameContext.getFactionApp().getFaction(factionId);
//			if(null == faction){
//				return list;
//			}
//			FactionSoulRecord record = faction.getFactionSoulRecord();
//			if(null == record){
//				return list;
//			}
//			Map<String, List<FactionSoulInspireConfig>> soulInspreMap = this.inspireMap.get(record.getSoulId());
//			if(Util.isEmpty(soulInspreMap)){
//				return list;
//			}
//			String key = getInspireKey(record.getFlyNum(), record.getLevel());
//			return soulInspreMap.get(key);
//		} catch (Exception e) {
//			logger.error("getSoulInspire error",e);
//		}
//		return list;
//	}
//	
//	@Override
//	public FactionSoulInspireBuffConfig getSoulInspireBuff(short buffId, int level) {
//		try{
//			Map<Integer,FactionSoulInspireBuffConfig> map = this.inspireBuffMap.get(buffId);
//			if(Util.isEmpty(map)){
//				return null;
//			}
//			return map.get(level);
//		}catch(Exception e){
//			logger.error("getSoulInspire error",e);
//		}
//		return null;
//	}
//	
//	@Override
//	public FactionSoulInspireType getInspireBuffType(short buffId){
//		try{
//			Map<Integer,FactionSoulInspireBuffConfig> map = this.inspireBuffMap.get(buffId);
//			if(Util.isEmpty(map)){
//				return null;
//			}
//			FactionSoulInspireBuffConfig config = map.get(1);
//			if(null == config){
//				return null;
//			}
//			return FactionSoulInspireType.get(config.getInspireType());
//		}catch(Exception e){
//			logger.error("getInspireBuffType error",e);
//		}
//		return null;
//	}
//	
//	@Override
//	public List<FactionSoulBuffItem> getBuffItems(FactionSoulRecord recored){
//		List<FactionSoulBuffItem> list = new ArrayList<FactionSoulBuffItem>();
//		List<FactionSoulInspireConfig> buffList = this.getBuffList(recored);
//		if(Util.isEmpty(buffList)){
//			return list;
//		}
//		FactionSoulBuffItem item = null;
//		for(FactionSoulInspireConfig config:buffList){
//			if(null == config){
//				continue;
//			}
//			item = new FactionSoulBuffItem();
//			item.setBuffId(config.getBuffId());
//			item.setBuffName(config.getBuffName());
//			list.add(item);
//		}
//		return list;
//	}
//	
//	private List<FactionSoulInspireConfig> getBuffList(FactionSoulRecord recored){
//		List<FactionSoulInspireConfig> list = new ArrayList<FactionSoulInspireConfig>();
//		String key = getInspireKey(recored.getFlyNum(), recored.getLevel());
//		Map<String,List<FactionSoulInspireConfig>> map = this.inspireMap.get(recored.getSoulId());
//		if(Util.isEmpty(map)){
//			return list;
//		}
//		return map.get(key);
//	}
//}
