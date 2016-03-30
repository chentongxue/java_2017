//package sacred.alliance.magic.app.faction;
//
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import sacred.alliance.magic.app.chat.ChannelType;
//import sacred.alliance.magic.app.chat.ChatApp;
//import sacred.alliance.magic.app.chat.ChatSysName;
//import sacred.alliance.magic.app.config.FactionConfig;
//import sacred.alliance.magic.app.faction.integral.IntegralChannel;
//import sacred.alliance.magic.app.faction.integral.IntegralResult;
//import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
//import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
//import sacred.alliance.magic.base.FactionBuildFuncType;
//import sacred.alliance.magic.base.FactionDescType;
//import sacred.alliance.magic.base.FactionIntegralLogType;
//import sacred.alliance.magic.base.FactionPositionType;
//import sacred.alliance.magic.base.FactionPowerType;
//import sacred.alliance.magic.base.FactionRecordType;
//import sacred.alliance.magic.base.OperatorType;
//import sacred.alliance.magic.base.OutputConsumeType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.base.SaveDbStateType;
//import sacred.alliance.magic.base.XlsSheetNameType;
//import sacred.alliance.magic.channel.EmptyChannelSession;
//import sacred.alliance.magic.component.id.IdFactory;
//import sacred.alliance.magic.component.id.IdType;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.channel.ChannelSession;
//import sacred.alliance.magic.core.exception.ServiceException;
//import sacred.alliance.magic.dao.BaseDAO;
//import sacred.alliance.magic.dao.impl.FactionDAOImpl;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionBuild;
//import sacred.alliance.magic.domain.FactionContribute;
//import sacred.alliance.magic.domain.FactionIntegralLog;
//import sacred.alliance.magic.domain.FactionRecord;
//import sacred.alliance.magic.domain.FactionRole;
//import sacred.alliance.magic.domain.GoodsBase;
//import sacred.alliance.magic.module.cache.Cache;
//import sacred.alliance.magic.module.cache.CacheEvent;
//import sacred.alliance.magic.module.cache.CacheListener;
//import sacred.alliance.magic.module.cache.SimpleCache;
//import sacred.alliance.magic.util.CheckNameUtil;
//import sacred.alliance.magic.util.Converter;
//import sacred.alliance.magic.util.DateUtil;
//import sacred.alliance.magic.util.FIFOLinkedHashMap;
//import sacred.alliance.magic.util.ListPage;
//import sacred.alliance.magic.util.ListPageDisplay;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.util.StringUtil;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.util.XlsPojoUtil;
//import sacred.alliance.magic.vo.MapContainer;
//import sacred.alliance.magic.vo.MapCopyContainer;
//import sacred.alliance.magic.vo.MapInstance;
//import sacred.alliance.magic.vo.RoleInstance;
//
//import com.game.draco.GameContext;
//import com.game.draco.app.mail.domain.Mail;
//import com.game.draco.app.mail.type.MailSendRoleType;
//import com.game.draco.app.npc.NpcInstanceFactroy;
//import com.game.draco.app.npc.domain.NpcInstance;
//import com.game.draco.app.npc.domain.NpcTemplate;
//import com.game.draco.base.CampType;
//import com.game.draco.message.internal.C0071_RoleWarehouseMailInternalMessage;
//import com.game.draco.message.internal.C0072_FactionRoleAddInternalMessage;
//import com.game.draco.message.internal.C0073_FactionRoleRemoveInternalMessage;
//import com.game.draco.message.internal.C0076_FactionChangePostionInternalMessage;
//import com.game.draco.message.internal.C0078_FactionWarehouseResetInternalMessage;
//import com.game.draco.message.push.C0003_TipNotifyMessage;
//import com.game.draco.message.push.C1719_FactionRoleHeadShowNotifyMessage;
//import com.game.draco.message.push.C1739_FactionApplyJoinNotifyMessage;
//import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;
//import com.game.draco.message.response.C1721_FactionSelfPowerListRespMessage;
//import com.google.common.collect.Maps;
//
//public class FactionAppImpl implements FactionApp {
//	
////	private static final byte ZERO = 0;
////	private static final byte ONE = 1;
////	private static final byte TWO = 2;
//	private static final ChannelSession emptyChannelSession = new EmptyChannelSession();
//	private static final byte Level_One = 1;
//	public static final String ID = "fac_";
//	private final Logger logger = LoggerFactory.getLogger(this.getClass());
//	private BaseDAO baseDAO;
//	private FactionDAOImpl factionDAO;
//	private IdFactory<String> idFactory ;
//	private FactionConfig factionConfig;
//	private OnlineCenter onlineCenter;
//	private ChatApp chatApp;
//	private Map<String, Faction> factionMap = Maps.newHashMap();//门派列表
//	private Cache<String, Map<Integer,FactionRole>> factionRoleCache;//缓存门派成员列表
//	private Map<String,FIFOLinkedHashMap<Integer,FactionRole>> applyMap = Maps.newHashMap();//申请加入门派的列表
//	private Map<Integer,Map<Integer,FactionBuild>> buildConfigMap = Maps.newHashMap();//门派建筑信息
//	//公会PVP地图列表
//	private Set<String> factionPvpMapSet = null ;
//	/** KEY=公会等级,VALUE=等级配置信息 */
//	private Map<Byte,FactionUpgrade> factionUpgradeMap = new HashMap<Byte,FactionUpgrade>();
//	/** KEY=权限类型,VALUE=权限配置信息 */
//	private Map<FactionPowerType,FactionPower> factionPowerMap = new HashMap<FactionPowerType,FactionPower>();
//	/** KEY=门派说明类型,VALUE=门派说明配置信息 */
//	private Map<FactionDescType,FactionDescribe> describeMap = new HashMap<FactionDescType,FactionDescribe>();
//	//职位拥有的权限
//	private Map<FactionPositionType,Set<FactionPowerType>> positionPowerSet = new HashMap<FactionPositionType,Set<FactionPowerType>>();
//	//门派建筑NPC出生信息
//	private Map<String, Integer> buildNpcMap = Maps.newHashMap();
//	private FactionCreateConfig factionCreateConfig = null;
//	@Override
//	public boolean isFactionPvpMap(String mapId){
//		if(Util.isEmpty(mapId) || Util.isEmpty(factionPvpMapSet)){
//			return false ;
//		}
//		return this.factionPvpMapSet.contains(mapId);
//	}
//	
//	/**
//	 * 加载门派配置表
//	 */
//	private void loadFactionConfig(){
//		String fileName = "";
//		String sheetName = "";
//		try{
//			String xlsPath = GameContext.getPathConfig().getXlsPath();
//			//加载门派PVP地图
//			fileName = XlsSheetNameType.faction_pvp_map.getXlsName();
//			sheetName = XlsSheetNameType.faction_pvp_map.getSheetName();
//			this.factionPvpMapSet = XlsPojoUtil.sheetToStringSet(xlsPath + fileName, sheetName);
//			//加载门派升级配置
//			fileName = XlsSheetNameType.faction_upgrade.getXlsName();
//			sheetName = XlsSheetNameType.faction_upgrade.getSheetName();
//			List<FactionUpgrade> upgradeList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, FactionUpgrade.class);
//			for(FactionUpgrade upgrade : upgradeList){
//				if(null == upgrade){
//					continue;
//				}
//				this.factionUpgradeMap.put(upgrade.getLevel(), upgrade);
//			}
//			if(Util.isEmpty(this.factionUpgradeMap)){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",no have any config!");
//			}
//			//职位拥有的权限
//			for(FactionPositionType position : FactionPositionType.values()){
//				this.positionPowerSet.put(position, new HashSet<FactionPowerType>());
//			}
//			//加载门派权限配置
//			fileName = XlsSheetNameType.faction_power.getXlsName();
//			sheetName = XlsSheetNameType.faction_power.getSheetName();
//			List<FactionPower> powerList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, FactionPower.class);
//			for(FactionPower power : powerList){
//				if(null == power){
//					continue;
//				}
//				byte type = power.getType();
//				FactionPowerType powerType = FactionPowerType.get(type);
//				if(null == powerType){
//					Log4jManager.checkFail();
//					Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName
//							+",type="+type+",this type is not exist!");
//					continue;
//				}
//				this.factionPowerMap.put(powerType, power);
//				//职位拥有的权限
//				if(power.isLeaderHold()){
//					this.positionPowerSet.get(FactionPositionType.Leader).add(powerType);
//				}
//				if(power.isDeputyHold()){
//					this.positionPowerSet.get(FactionPositionType.Deputy).add(powerType);
//				}
//				if(power.isEliteHold()){
//					this.positionPowerSet.get(FactionPositionType.Elite).add(powerType);
//				}
//				if(power.isMemberHold()){
//					this.positionPowerSet.get(FactionPositionType.Member).add(powerType);
//				}
//			}
//			if(Util.isEmpty(this.factionPowerMap)){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",no have any config!");
//			}
//			//加载门派说明配置
//			fileName = XlsSheetNameType.faction_describe.getXlsName();
//			sheetName = XlsSheetNameType.faction_describe.getSheetName();
//			List<FactionDescribe> descList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, FactionDescribe.class);
//			for(FactionDescribe desc : descList){
//				if(null == desc){
//					continue;
//				}
//				byte type = desc.getType();
//				FactionDescType descType = FactionDescType.get(type);
//				if(null == descType){
//					Log4jManager.checkFail();
//					Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName
//							+",type="+type+",this type is not exist!");
//					continue;
//				}
//				this.describeMap.put(descType, desc);
//			}
//			
//			//加载门派创建配置
//			fileName = XlsSheetNameType.faction_create.getXlsName();
//			sheetName = XlsSheetNameType.faction_create.getSheetName();
//			List<FactionCreateConfig> createList = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, FactionCreateConfig.class);
//			if(Util.isEmpty(createList)) {
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",FactionCreateConfig is not exist!");
//			}
//			this.factionCreateConfig = createList.get(0);
//			if(null == factionCreateConfig) {
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",FactionCreateConfig is not exist!");
//			}
//			int goodsId = factionCreateConfig.getGoodsId();
//			GoodsBase consumeGoods = GameContext.getGoodsApp().getGoodsBase(goodsId);
//			if(null == consumeGoods) {
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",consumeGoods is not exist!goodsId = " + goodsId);	
//			}
//			factionCreateConfig.setConsumeGoods(consumeGoods);
//			
//			int impeachGoodsId = factionCreateConfig.getImpeachGoodsId();
//			GoodsBase impeachGoods = GameContext.getGoodsApp().getGoodsBase(impeachGoodsId);
//			if(null == impeachGoods) {
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",impeachGoods is not exist!goodsId = " + goodsId);	
//			}
//		}catch(Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName,ex);
//		}
//	}
//	
//	
//	@Override
//	public void start() {
//		//加载门派配置表
//		this.loadFactionConfig();
//		//加载门派建筑的创建、升级配置
//		this.initBuildConfigMap();
//		//加载门派贡献度，初始技能，初始建筑配置
//		GameContext.getFactionFuncApp().init();
//		//加载门派神兽配置
//		GameContext.getFactionSoulApp().init();
//		//初始化门派信息
//		this.initFactionMap();
//		//门派成员列表的缓存
//		this.factionRoleCache = new SimpleCache<String, Map<Integer,FactionRole>>();
//		this.factionRoleCache.setTimeToLiveMillisecond(this.factionConfig.getCacheMillis());
//		this.factionRoleCache.start();
//		this.factionRoleCache.clear();
//		this.factionRoleCache.addCacheListener(new CacheListener() {
//			@Override
//			public void entryRemoved(CacheEvent event) {
//				if (null == event) {
//					return;
//				}
//				//门派信息入库
//				try {
//					String factionId = (String) event.getKey();
//					Faction faction = getFaction(factionId);
//					if(null != faction && SaveDbStateType.Update == faction.getSaveDbStateType()){
//						baseDAO.update(faction);
//					}
//				} catch (RuntimeException e1) {
//					logger.error("FactionApp.start() factionRoleCache update faction error: ", e1);
//				}
//				//在线的门派成员更新入库
//				Map<Integer,FactionRole> roleMap = (Map<Integer,FactionRole>) event.getValue();
//				if(roleMap.isEmpty()){
//					return;
//				}
//				for(FactionRole factionRole : roleMap.values()){
//					try {
//						if(null == factionRole){
//							continue;
//						}
//						int roleId = factionRole.getRoleId();
//						RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//						//更新在线的帮众信息
//						if(null == role){
//							continue;
//						}
//						factionRole.setRoleLevel(role.getLevel());
//						baseDAO.update(factionRole);
//					} catch (Exception e) {
//						logger.error("update factionRoleCache ", e);
//					}
//				}
//			}
//			@Override
//			public void entryAccessed(CacheEvent event) {
//			}
//			@Override
//			public void entryAdded(CacheEvent event) {
//			}
//			@Override
//			public void entryCleared(CacheEvent event) {
//			}
//			@Override
//			public void entryExpired(CacheEvent event) {
//			}
//			@Override
//			public void entryUpdated(CacheEvent event) {
//			}
//		});
//	}
//	
//	@Override
//	public void stop() {
//		
//	}
//	
//	@Override
//	public void setArgs(Object arg0) {
//		
//	}
//	
//	/**
//	 * 加载门派，初始化门派信息
//	 */
//	private void initFactionMap(){
//		try{
//			List<Faction> selectList = this.baseDAO.selectAll(Faction.class);
//			for(Faction faction : selectList){
//				if(null == faction){
//					continue;
//				}
//				byte level = faction.getFactionLevel();
//				FactionUpgrade upgrade = this.factionUpgradeMap.get(level);
//				if(null == upgrade){
//					Log4jManager.checkFail();
//					Log4jManager.CHECK.error("FactionApp.initFactionMap error: factionLevel=" + level + ",no have faction upgrade config!");
//					continue;
//				}
//				faction.setMaxMemberNum(upgrade.getMaxMemberNum());
//				faction.setMaxIntegral(upgrade.getMaxIntegral());
//				faction.setMaxDonateCount(upgrade.getMaxDonateCount());
//				//******************************************************
//				//以前神州的时候贡献是所有人的之和，现在是门派经验，升级的时候会减少。
//				/*//门派总贡献容错
//				int contribution = this.factionDAO.getContributionSum(faction.getFactionId());
//				faction.setContribution(contribution);*/
//				//******************************************************
//				//加载已有的公会建筑
//				this.initFactionBuild(faction);
//				int ownRoleId = faction.getLeaderId();
//				RoleInstance ownRole = GameContext.getUserRoleApp().getRoleByRoleId(String.valueOf(ownRoleId));
//				if(null != ownRole){
//					//检测未有的公会建筑和技能是否应该加载到公会
//					GameContext.getFactionFuncApp().checkBuild(ownRole, faction);
//				}
//				//加载门派神兽
//				GameContext.getFactionSoulApp().initFactionSoul(faction);
//				//加载门派召唤
//				GameContext.getSummonApp().loadFactionSummon(faction);
//				this.factionMap.put(faction.getFactionId(), faction);
//			}
//		}catch(Exception e){
//			logger.error("FactionApp.initFactionMap error: ", e);
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("FactionApp.initFactionMap error: ", e);
//		}
//	}
//	
//	@Override
//	public void factionRoleOffline(RoleInstance role){
//		try {
//			//更新角色的门派贡献表
//			this.updateFactionContribute(role);
//			//更新门派成员表
//			FactionRole factionRole = this.getFactionRole(role);
//			if(null == factionRole){
//				return;
//			}
//			factionRole.setRoleLevel(role.getLevel());
//			factionRole.setOfflineTime(new Date());
//			this.baseDAO.update(factionRole);
//		} catch (RuntimeException e) {
//			this.logger.error("FactionApp.factionRoleOffline error: ", e);
//		}
//	}
//	
//	/**
//	 * 角色的门派贡献值入库
//	 * @param role
//	 */
//	private void updateFactionContribute(RoleInstance role){
//		try {
//			FactionContribute fcb = role.getFactionContribute();
//			if(null == fcb){
//				return;
//			}
//			SaveDbStateType state = fcb.getSaveDbStateType();
//			if(SaveDbStateType.Initialize == state){
//				return;
//			}
//			if(SaveDbStateType.Insert == state){
//				this.baseDAO.insert(fcb);
//				fcb.setSaveDbStateType(SaveDbStateType.Update);
//			}else if(SaveDbStateType.Update == state){
//				this.baseDAO.update(fcb);
//			}
//		} catch (RuntimeException e) {
//			this.logger.error("FactionApp.factionRoleOffline update contribute error: ", e);
//		}
//	}
//	
//	@Override
//	public void factionMemberLog() {
//		if(0 == this.factionRoleCache.size()){
//			return ;
//		}
//		for(String factionId : this.factionRoleCache.getKeyes()){
//			Map<Integer,FactionRole> roleMap = this.factionRoleCache.get(factionId);
//			if(Util.isEmpty(roleMap)){
//				continue;
//			}
//			for(FactionRole factionRole : roleMap.values()){
//				try{
//					if(null == factionRole){
//						continue;
//					}
//					factionRole.offlineLog();
//				}catch(Exception e){
//					this.logger.error("factionMemberLog error: ", e);
//				}
//			}
//		}
//	}
//
//	@Override
//	public boolean saveAllFactionRoles(){
//		if(0 == this.factionRoleCache.size()){
//			return true;
//		}
//		for(String factionId : this.factionRoleCache.getKeyes()){
//			Map<Integer,FactionRole> roleMap = this.factionRoleCache.get(factionId);
//			if(Util.isEmpty(roleMap)){
//				continue;
//			}
//			for(FactionRole factionRole : roleMap.values()){
//				try{
//					if(null == factionRole){
//						continue;
//					}
//					int roleId = factionRole.getRoleId();
//					RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//					//更新在线的帮众信息
//					if(null == role){
//						continue;
//					}
//					factionRole.setRoleLevel(role.getLevel());
//					baseDAO.update(factionRole);
//				}catch(Exception e){
//					this.logger.error("FactionApp.saveAllFactionRoles error: ", e);
//				}
//			}
//		}
//		return true;
//	}
//	
//	@Override
//	public void saveAllFaction() {
//		for(Faction faction : this.factionMap.values()){
//			if(null == faction){
//				continue;
//			}
//			if(SaveDbStateType.Update != faction.getSaveDbStateType()){
//				continue;
//			}
//			try {
//				this.baseDAO.update(faction);
//				//更新公会神兽
//				GameContext.getFactionSoulApp().updateFactionSoul(faction);
//				//更新公会召唤
//				GameContext.getSummonApp().saveSummon(faction.getSummonDbInfo());
//			} catch (RuntimeException e) {
//				this.logger.error("FactionApp.saveAllFaction error: ", e);
//			}
//		}
//	}
//	
//	@Override
//	public void factionLog() {
//		for(Faction faction : this.factionMap.values()){
//			if(null == faction){
//				continue;
//			}
//			faction.offlineLog();
//		}
//	}
//
//	@Override
//	public Collection<RoleInstance> getAllOnlineFactionRole(Faction faction) {
//		List<RoleInstance> onlineRoleList = new ArrayList<RoleInstance>();
//		if(null == faction){
//			return onlineRoleList;
//		}
//		this.initFactionRoleCache(faction);
//		Map<Integer,FactionRole> factionRoleMap = this.factionRoleCache.get(faction.getFactionId());
//		if(Util.isEmpty(factionRoleMap)){
//			return onlineRoleList;
//		}
//		for(int roleId : factionRoleMap.keySet()){
//			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//			if(null == role){
//				continue;
//			}
//			onlineRoleList.add(role);
//		}
//		return onlineRoleList;
//	}
//	
//	private Faction searchFactionByName(String factionName){
//		for(Faction faction : this.factionMap.values()){
//			if(null == faction){
//				continue;
//			}
//			if(faction.getFactionName().equals(factionName)){
//				return faction;
//			}
//		}
//		return null;
//	}
//
//	@Override
//	public Faction getFaction(String factionId){
//		if(Util.isEmpty(factionId)){
//			return null ;
//		}
//		return this.factionMap.get(factionId);
//	}
//	
//	@Override
//	public Faction getFaction(RoleInstance role){
//		return this.getFaction(role.getFactionId());
//	}
//	
//	@Override
//	public Result checkCreateCondition(RoleInstance role){
//		Result result = new Result();
//		if(role.hasFaction()){
//			return result.setInfo(Status.Faction_Role_Own.getTips());
//		}
//		if(role.getLevel() < factionCreateConfig.getRoleLevel()){
//			String str = MessageFormat.format(Status.Faction_RoleLevel_NotEnough.getTips(), factionCreateConfig.getRoleLevel());
//			return result.setInfo(str);
//		}
//		int consumeGoodsId = factionCreateConfig.getGoodsId();
//		if(role.getRoleBackpack().countByGoodsId(consumeGoodsId) < 1){
//			String str = MessageFormat.format(Status.Faction_Create_Goods_NotEnough.getTips(), factionCreateConfig.getConsumeGoods().getName());
//			return result.setInfo(str);
//		}
//		return result.success();
//	}
//	
//	@Override
//	public Result createFaction(RoleInstance role, String factionName,String factionDesc) throws ServiceException {
//		try {
//			Result result = this.checkCreateCondition(role);
//			if(!result.isSuccess()){
//				return result;
//			}
//			result.failure();
//			if(StringUtil.nullOrEmpty(factionName)){
//				return result.setInfo(Status.Faction_Name_Null.getTips());
//			}
//			//factionName = factionName.trim();
//			factionName = StringUtil.replaceNewLine(factionName);
//			
//			//判断是否有@#号等特殊字符
//			if(StringUtil.haveSpecialChar(factionName)){
//				return result.setInfo(Status.Faction_Name_Illegal.getTips());
//			}
//			// 判断是否有以s/S+数字以尾
//			if(CheckNameUtil.isMatchChangeName(factionName)){
//				return result.setInfo(Status.Faction_Name_Illegal.getTips());
//			}
//			//玩家才需要文字过滤
//			/*if(!GameContext.getIllegalWordsService().isCNorENorFigure(factionName)){
//				return result.setInfo(Status.Faction_Name_NotInRange.getTips());
//			}*/
//			//过滤敏感词
//			String illegalChar = GameContext.getIllegalWordsService().findIllegalChar(factionName);
//			if(null != illegalChar){
//				return result.setInfo(Status.Faction_Name_Illegal.getTips());
//			}
//			//过滤禁用词
//			String forbidChar = GameContext.getIllegalWordsService().findForbiddenChar(factionName);
//			if(null != forbidChar){
//				return result.setInfo(Status.Faction_Name_Forbidden.getTips());
//			}
//			if(factionName.length() > factionConfig.getFactionNameLength()){
//				return result.setInfo(Status.Faction_Name_TooLonger.getTips());
//			}
//			//过滤、替换逗号
//			factionDesc = GameContext.getIllegalWordsService().doFilter(factionDesc);
//			Util.replaceComma(factionDesc);
//			if(factionDesc.indexOf("*") != -1){
//				return result.setInfo(Status.Faction_Desc_Illegal.getTips());
//			}
//			if(factionDesc.trim().length() > factionConfig.getFactionDescLength()){
//				return result.setInfo(Status.Faction_Desc_TooLonger.getTips());
//			}
//			if("".equals(factionDesc)) {
//				factionDesc = Status.Faction_Desc_Default.getTips();
//			}
//			if(null != this.searchFactionByName(factionName)){
//				return result.setInfo(Status.Faction_Exist.getTips());
//			}
//			//创建门派对象
//			String factionId = idFactory.nextId(IdType.FACTIONID);
//			Faction faction = new Faction();
//			faction.setFactionId(factionId);
//			faction.setFactionName(factionName);
//			faction.setFactionLevel(Level_One);
//			faction.setLeaderId(role.getIntRoleId());
//			faction.setLeaderName(role.getRoleName());
//			faction.setCreateDate(new Date());
//			faction.setFactionDesc(factionDesc);
//			faction.setMemberNum(1);
//			FactionUpgrade upgrade = this.factionUpgradeMap.get(Level_One);
//			faction.setMaxMemberNum(upgrade.getMaxMemberNum());
//			faction.setMaxIntegral(upgrade.getMaxIntegral());
//			faction.setMaxDonateCount(upgrade.getMaxDonateCount());
//			faction.setFactionCamp(role.getCampId());
//			//门派信息入库，更新门派缓存数据
//			this.baseDAO.insert(faction);
//			
//			//创建人信息入库
//			int roleId = role.getIntRoleId();
//			FactionRole factionRole = new FactionRole();
//			factionRole.setRoleId(roleId);
//			factionRole.setFactionId(factionId);
//			factionRole.setRoleName(role.getRoleName());
//			factionRole.setRoleLevel(role.getLevel());
//			factionRole.setCareer(role.getCareer());
//			factionRole.setSex(role.getSex());
//			factionRole.setPosition(FactionPositionType.Leader.getType());
//			factionRole.setCreateDate(new Date());
//			factionRole.setUserId(role.getUserId());
//			this.baseDAO.insert(factionRole);
//			
//			//创建公会神兽
//			Result soulResult = GameContext.getFactionSoulApp().createFactionSoul(faction);
//			if(!soulResult.isSuccess()) {
//				logger.error("FactionApp.createFactionSoul create failure:" + soulResult.getInfo());
//				throw new ServiceException("FactionAppImpl.createFactionSoul exception:" + soulResult.getInfo());
//			}
//			
//			//更新角色信息,（先不更新数据库）
//			role.setFactionId(factionId);
//			this.factionMap.put(factionId, faction);
//			//创建公会时创建公会建筑
//			Result bulidResult = GameContext.getFactionFuncApp().createFactionBuild(role);
//			if(!bulidResult.isSuccess()) {
//				logger.error("FactionApp.createFactionBuild create failure:" + bulidResult.getInfo());
//				role.setFactionId("");
//				this.factionMap.remove(factionId);
//				throw new ServiceException("FactionAppImpl.createFactionBuild exception:" + bulidResult.getInfo());
//			}
//			
//			int consumeGoodsId = factionCreateConfig.getGoodsId();
//			GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBag(role, consumeGoodsId, 1, OutputConsumeType.faction_create_consume);
//			if(!goodsResult.isSuccess()) {
//				String str = MessageFormat.format(Status.Faction_Create_Goods_NotEnough.getTips(), factionCreateConfig.getConsumeGoods().getName());
//				return result.setInfo(str);
//			}
//			
//			role.getBehavior().notifyAttribute();
//			//通知角色头顶显示
//			this.notifyFactionRoleHeadShowChange(roleId);
//			//更新成员列表缓存
//			this.addFactionRoleToCache(factionId, factionRole);
//			
//			//重新计算仓库容量
//			C0078_FactionWarehouseResetInternalMessage internalReqMsg = new C0078_FactionWarehouseResetInternalMessage();
//			internalReqMsg.setRole(role);
//			GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(), 
//					internalReqMsg, emptyChannelSession, true);
//			
//			return result.success();
//		} catch (Exception e) {
//			this.logger.error("FactionApp.createFaction", e);
//			throw new ServiceException("FactionAppImpl.creatFaction exception",e);
//		}
//	}
//	
//	/** 通知角色头顶显示门派名称和职位 */
//	private void notifyFactionRoleHeadShowChange(int roleId){
//		try{
//			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//			if(null == role){
//				return;
//			}
//			//获取不到地图实例
//			MapInstance mapInstance = role.getMapInstance();
//			if(null == mapInstance){
//				return;
//			}
//			Faction faction = this.getFaction(role);
//			FactionRole factionRole = this.getFactionRole(role);
//			C1719_FactionRoleHeadShowNotifyMessage message = new C1719_FactionRoleHeadShowNotifyMessage();
//			message.setRoleId(role.getIntRoleId());
//			message.setFactionName("<" + faction.getFactionName()+ ">"+ faction.getPositionNick(factionRole.getPosition()));
//			String color = GameContext.getFactionConfig().getViewColor();
//			message.setFactionColor((int) Long.parseLong(color != null ? color : "ffffffff", 16));
//			mapInstance.broadcastMap(null, message);
//		}catch(Exception e){
//			this.logger.error("FactionApp.notifyFactionRoleHeadShowChange error: ", e);
//		}
//	}
//	/** 离开门派的时候，通知地图内角色头顶显示 */
//	private void notifyFactionRoleHeadShowClear(RoleInstance role){
//		try {
//			MapInstance mapInstance = role.getMapInstance();
//			if(null == mapInstance){
//				return;
//			}
//			C1719_FactionRoleHeadShowNotifyMessage message = new C1719_FactionRoleHeadShowNotifyMessage();
//			message.setRoleId(role.getIntRoleId());
//			mapInstance.broadcastMap(null, message);
//		} catch (Exception e) {
//			this.logger.error("FactionApp.notifyFactionRoleHeadShowClear error: ", e);
//		}
//	}
//
//	@Override
//	public ListPageDisplay<Faction> getFactionList(int currPage, int size) {
//		List<Faction> list = new ArrayList<Faction>();
//		list.addAll(this.factionMap.values());
//		this.sortFaction(list);
//		ListPage<Faction> listPage = new ListPage<Faction>(list,size);
//		return listPage.getObjectsDsiplay(currPage);
//	}
//	
//	/**
//	 * 排序(门派贡献值)
//	 * @param list
//	 */
//	private void sortFaction(List<Faction> list){
//		Collections.sort(list, new Comparator<Faction>() {
//			public int compare(Faction info1, Faction info2) {
//				if(info1.getFactionLevel() > info2.getFactionLevel()) {
//					return -1;
//				}
//				if(info1.getFactionLevel() == info2.getFactionLevel()) {
//					if(info1.getContribution() > info2.getContribution()) {
//						return -1;
//					}
//					if(info1.getContribution() < info2.getContribution()) {
//						return 1;
//					}
//				}
//				if(info1.getFactionLevel() < info2.getFactionLevel()) {
//					return 1;
//				}
//				return 0;
//			}
//		});
//	}
//
//	@Override
//	public Result applyJoinFaction(RoleInstance role, String factionId) {
//		Result result = new Result();
//		
//		Date roleLeaveTime = role.getLeaveFactionTime();
//		if(null != roleLeaveTime){
//			int timeDiff = (int)DateUtil.dateDiffMinute(new Date(), roleLeaveTime);
//			int cd = this.factionCreateConfig.getJoinCd();
//			if(timeDiff < cd){
//				return result.setInfo(GameContext.getI18n().messageFormat(TextId.Faction_Apply_Cd_Not_Enough, cd, cd - timeDiff));
//			}
//		}
//		
//		int joinNeedLevel = factionCreateConfig.getJoinRoleLevel();
//		if(role.getLevel() < joinNeedLevel){
//			String str = MessageFormat.format(Status.Faction_RoleLevel_NotEnough.getTips(), joinNeedLevel);
//			return result.setInfo(str);
//		}
//		if(role.hasFaction()){
//			return result.setInfo(Status.Faction_Role_Own.getTips());
//		}
//		Faction faction = this.getFaction(factionId);
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Exist.getTips());
//		}
//		if(role.getCampId() != faction.getFactionCamp()) {
//			return result.setInfo(Status.Faction_Camp_Err.getTips());
//		}
//		if(faction.isFull()){
//			return result.setInfo(Status.Faction_Member_Full.getTips());
//		}
//		//将申请信息放入缓存队列
//		FactionRole factionRole = new FactionRole();
//		factionRole.setRoleId(role.getIntRoleId());
//		factionRole.setFactionId(factionId);
//		factionRole.setRoleName(role.getRoleName());
//		factionRole.setRoleLevel(role.getLevel());
//		factionRole.setCareer(role.getCareer());
//		factionRole.setSex(role.getSex());
//		factionRole.setCreateDate(new Date());//设置申请时间
//		factionRole.setOfflineTime(role.getLastOffTime());
//		//以前在此门派的贡献度
//		factionRole.setContribution(role.getFactionContributeValue(factionId));
//		factionRole.setTotalContribution(role.getFactionTotalContributeValue(factionId));
//		factionRole.setUserId(role.getUserId());
//		FIFOLinkedHashMap<Integer, FactionRole> queue = this.applyMap.get(factionId);
//		if(null == queue){
//			queue = new FIFOLinkedHashMap<Integer,FactionRole>(this.factionConfig.getApplyQueueLenth());
//			queue.put(factionRole.getRoleId(), factionRole);
//		} else {
//			queue.put(factionRole.getRoleId(), factionRole);
//		}
//		this.applyMap.put(factionId, queue);
//		//给有权限的在线成员发送入会申请
//		sendApplyMessage(factionId, role);
//		return result.success();
//	}
//	
//	@Override
//	public List<FactionRole> getApplyJoinList(String factionId){
//		List<FactionRole> list = new ArrayList<FactionRole>();
//		FIFOLinkedHashMap<Integer, FactionRole> queue = this.applyMap.get(factionId);
//		if(null == queue){
//			return list;
//		}
//		for(FactionRole factionRole : queue.values()){
//			if(null == factionRole){
//				continue;
//			}
//			RoleInstance role = this.onlineCenter.getRoleInstanceByRoleId(String.valueOf(factionRole.getRoleId()));
//			FactionRole fr = factionRole;
//			if(null != role){
//				fr.setRoleLevel(role.getLevel());
//			}
//			list.add(fr);
//		}
//		return list;
//	}
//	
//	@Override
//	public Result acceptApplyJoin(RoleInstance leader, int roleId) throws ServiceException{
//		try {
//			Result result = new Result();
//			Faction faction = this.getFaction(leader);
//			if(null == faction){
//				return result.setInfo(Status.Faction_Not_Own.getTips());
//			}
//			if(!this.getPowerTypeSet(leader).contains(FactionPowerType.Dispose_Apply_Join)){
//				return result.setInfo(GameContext.getI18n().getText(TextId.Faction_Role_No_Position));
//			}
//			if(faction.isFull()){
//				return result.setInfo(Status.Faction_Member_Full.getTips());
//			}
//			FactionRole factionRole = this.removeApplyJoin(faction.getFactionId(), roleId);
//			if(null == factionRole){
//				return result.setInfo(Status.Faction_ApplyRole_Null.getTips());
//			}
//			
//			C0072_FactionRoleAddInternalMessage internalReqMsg = new C0072_FactionRoleAddInternalMessage();
//			internalReqMsg.setRoleId(String.valueOf(roleId));
//			internalReqMsg.setFaction(faction);
//			internalReqMsg.setFactionRole(factionRole);
//			internalReqMsg.setOperaRoleId(leader.getRoleId());
//			GameContext.getUserSocketChannelEventPublisher().publish(factionRole.getUserId(), 
//					internalReqMsg, emptyChannelSession, true);
//			return result.success();
//		} catch (RuntimeException e) {
//			this.logger.error("FactionApp.acceptApplyJoin error: ", e);
//			throw new ServiceException("FactionAppImpl.acceptApplyJoin exception", e);
//		}
//	}
//	
//	@Override
//	public Result refuseApplyJoin(RoleInstance leader, int roleId){
//		Result result = new Result();
//		Faction faction = this.getFaction(leader);
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Own.getTips());
//		}
//		if(!this.getPowerTypeSet(leader).contains(FactionPowerType.Dispose_Apply_Join)){
//			return result.setInfo(GameContext.getI18n().getText(TextId.Faction_Role_No_Position));
//		}
//		FactionRole factionRole = this.removeApplyJoin(faction.getFactionId(), roleId);
//		if(null == factionRole){
//			return result.setInfo(Status.Faction_ApplyRole_Null.getTips());
//		}
//		//给申请人发邮件提示
//		String title = GameContext.getI18n().getText(TextId.Faction_REFUSE_APPLY);
//		String context = GameContext.getI18n().messageFormat(TextId.Faction_REFUSE_APPLY_CONTENT, faction.getFactionName());
//		this.sendMail(roleId, title, context);
//		//在线则发浮动提示
//		GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(roleId), new C0003_TipNotifyMessage(context));
//		return result.success();
//	}
//	
//	/** 
//	 * 发邮件通知目标角色
//	 * @param roleId 角色ID
//	 * @param title 邮件标题
//	 * @param context 邮件内容
//	 */
//	private void sendMail(int roleId, String title, String context){
//		try{
//			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
//			mail.setTitle(title);
//			mail.setSendRole(MailSendRoleType.Faction.getName());
//			mail.setContent(context);
//			mail.setRoleId(String.valueOf(roleId));
//			GameContext.getMailApp().sendMail(mail); 
//		}catch(Exception e){
//			this.logger.error("FactionAppImpl.sendMail", e);
//		}
//	}
//	
//	/**
//	 * 删除申请加入门派的角色信息
//	 * */
//	private FactionRole removeApplyJoin(String factionId, int roleId){
//		FIFOLinkedHashMap<Integer, FactionRole> queue = this.applyMap.get(factionId);
//		if(null == queue){
//			return null;
//		}
//		if(!queue.containsKey(roleId)){
//			return null;
//		}
//		return queue.remove(roleId);
//	}
//	
//	@Override
//	public Result exitFaction(RoleInstance role) throws ServiceException{
//		Result result = new Result();
//		try{
//			Faction faction = this.getFaction(role);
//			if(null == faction){
//				return result.setInfo(Status.Faction_Not_Exist.getTips());
//			}
//			//如果是帮主并且有其他门派成员时，不可退出门派
//			FactionPositionType position = this.getPositionType(role);
//			boolean isPresident = FactionPositionType.Leader == position;
//			if(isPresident && faction.getMemberNum() > 1){
//				return result.setInfo(Status.Faction_Exit_HasMember.getTips());
//			}
//			//如果门派只有一个人，并且参加了门派战，不能退出门派
//			if(faction.getMemberNum() == 1){
//				boolean hasFactionWar = GameContext.getFactionWarApp().hasFactionWar(faction.getFactionId());
//				if(hasFactionWar){
//					return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_EXIT_FACTION_CAN_NOT));
//				}
//			}
//			this.deleteFactionRole(role);
//			//帮众叛离时，给帮主发邮件提示
//			String factionName = faction.getFactionName();
//			String roleId = role.getRoleId();
//			if(!isPresident){
//				String title = GameContext.getI18n().getText(TextId.FACTION_EXIT_MAIL_TITLE);
//				String context = GameContext.getI18n().messageFormat(TextId.FACTION_EXIT_MAIL_CONTENT, role.getRoleName(), factionName);
//				this.sendMail(faction.getLeaderId(), title, context);
//				GameContext.getMessageCenter().sendByRoleId(null, roleId, new C0003_TipNotifyMessage(context));
//			}
//			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
//			message.setMsgContext(GameContext.getI18n().messageFormat(TextId.FACTION_EXIT_ROLE_MSG, factionName));
//			GameContext.getMessageCenter().sendByRoleId(null, roleId, message);
//			//离开公会记录
//			FactionRecord factionRecord = new FactionRecord();
//			factionRecord.setType(FactionRecordType.Faction_Record_Role_Leave.getType());
//			factionRecord.setFactionId(faction.getFactionId());
//			factionRecord.setData2(role.getRoleName());
//			GameContext.getFactionFuncApp().createFactionRecord(factionRecord);
//			
//			String str = GameContext.getI18n().messageFormat(TextId.FACTION_EXIT, role.getRoleName());
//			this.sendFactionMessage(roleId, faction, str);
//			
//			C0071_RoleWarehouseMailInternalMessage internalReqMsg = new C0071_RoleWarehouseMailInternalMessage();
//			internalReqMsg.setRoleId(roleId);
//			GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(), 
//					internalReqMsg, emptyChannelSession, true);
//			
//			return result.success();
//		} catch(Exception e){
//			logger.error("FactionApp.exitFaction error:" + e);
//			throw new ServiceException("FactionAppImpl.exitFaction exception",e);
//		}
//	}
//	
//	private void sendFactionMessage(String roleId, Faction faction, String text){
//		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
//		if(null != role){
//			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.System, text, null, role);
//		}
//		//门派频道内广播
//		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Faction, text, null, faction);
//	}
//	
//	@Override
//	public Result removeFactionRole(RoleInstance leader, int roleId) throws ServiceException{
//		try{
//			Result result = new Result();
//			//不能踢出自己
//			if(leader.getIntRoleId() == roleId){
//				return result.setInfo(Status.Faction_Remove_Self.getTips());
//			}
//			if(!this.getPowerTypeSet(leader).contains(FactionPowerType.Remove_Member)){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_KICK_POSITION_NOT_HAS));
//			}
//			Faction faction = this.getFaction(leader);
//			this.initFactionRoleCache(faction);
//			String factionId = leader.getFactionId();
//			if(!this.factionRoleCache.get(factionId).containsKey(roleId)){
//				return result.setInfo(Status.Faction_Member_Not_Exist.getTips());
//			}
//			RoleInstance targRole = this.getRoleInstance(roleId);
//			Result operateResult = FactionPositionType.isGreaterThan(this.getPositionType(leader), this.getPositionType(targRole));
//			if(!operateResult.isSuccess()){
//				return operateResult;
//			}
//			this.deleteFactionRole(targRole);
//			//给被驱逐的帮众发邮件提示
//			String title = GameContext.getI18n().getText(TextId.FACTION_KICK_MAIL_TITLE);
//			String factionName = faction.getFactionName();
//			String context = GameContext.getI18n().messageFormat(TextId.FACTION_KICK_MAIL_CONTENT, factionName);
//			this.sendMail(roleId, title, context);
//			GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(roleId), new C0003_TipNotifyMessage(context));
//			
//			//离开公会记录
//			FactionRecord factionRecord = new FactionRecord();
//			factionRecord.setType(FactionRecordType.Faction_Record_Role_Kick.getType());
//			factionRecord.setFactionId(faction.getFactionId());
//			factionRecord.setData2(targRole.getRoleName());
//			factionRecord.setData3(leader.getRoleName());
//			GameContext.getFactionFuncApp().createFactionRecord(factionRecord);
//			
//			String str = GameContext.getI18n().messageFormat(TextId.FACTION_REMOVE, targRole.getRoleName());
//			this.sendFactionMessage(targRole.getRoleId(), faction, str);
//			
//			C0071_RoleWarehouseMailInternalMessage internalReqMsg = new C0071_RoleWarehouseMailInternalMessage();
//			internalReqMsg.setRoleId(targRole.getRoleId());
//			GameContext.getUserSocketChannelEventPublisher().publish(targRole.getUserId(), 
//					internalReqMsg, emptyChannelSession,true);
//			return result.success();
//		} catch(Exception e){
//			logger.error("FactionApp.removeFactionRole error:" + e);
//			throw new ServiceException("FactionAppImpl.removeFactionRole exception", e);
//		}
//	}
//	
//	/**
//	 * 删除门派成员
//	 * @param role
//	 * @throws ServiceException
//	 */
//	private void deleteFactionRole(RoleInstance role) throws ServiceException{
//		try{
//			String factionId = role.getFactionId();
//			Faction faction = this.getFaction(factionId);
//			if(null == faction){
//				return;
//			}
//			int roleId = role.getIntRoleId();
//			this.initFactionRoleCache(faction);
//			
//			//从门派战中移除此人，不发放奖励
//			GameContext.getFactionWarApp().exitFaction(role);
//			
//			//先将角色的门派贡献值入库
//			this.updateFactionContribute(role);
//			//从缓存中清除
//			this.factionRoleCache.get(factionId).remove(roleId);
//			//从数据库中删除，并更新角色信息
//			this.baseDAO.delete(FactionRole.class, "roleId", roleId);
//			//以下加入到单用户单线程中，避免与登录时并发。new一个内部message
//			//*****************************************************
//			C0073_FactionRoleRemoveInternalMessage internalReqMsg = new C0073_FactionRoleRemoveInternalMessage();
//			internalReqMsg.setRoleId(role.getRoleId());
//			GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(), 
//					internalReqMsg, emptyChannelSession, true);
//			//*****************************************************
//			
//			//更新门派信息
//			int num = faction.getMemberNum();
//			//如果是门派最后一个人，同时删除门派信息
//			if(1 == num){
//				this.baseDAO.delete(Faction.class, "factionId", factionId);
//				this.factionMap.remove(factionId);
//				//调用排行榜门派下线
//				GameContext.getRankApp().factionOffRank(faction);
//			}else{
//				faction.setMemberNum(num-1);
//				this.baseDAO.update(faction);
//			}
//			//同步门派总贡献
////			this.synchContribution(faction, -factionRole.getContribution());
//			this.notifyFactionRoleHeadShowClear(role);
//			//玩家离开门派时，如果在门派地图直接踢出
//			this.factionMapKickRole(role);
//		} catch(Exception e){
//			logger.error("FactionApp.deleteFactionRole error:" + e);
//			throw new ServiceException("FactionAppImpl.deleteFactionRole exception", e);
//		}
//	}
//	
//	/**
//	 * 获取角色信息，若角色不在线则查库
//	 * @param roleId
//	 * @return
//	 */
//	private RoleInstance getRoleInstance(int roleId){
//		RoleInstance role = this.onlineCenter.getRoleInstanceByRoleId(String.valueOf(roleId));
//		if(null == role){
//			role = this.baseDAO.selectEntity(RoleInstance.class, "roleId", roleId);
//		}
//		return role;
//	}
//	
//	/**
//	 * 添加门派成员
//	 */
//	public Result addFactionRole(RoleInstance role, Faction faction, FactionRole factionRole) throws ServiceException{
//		String factionId = faction.getFactionId();
//		int roleId = factionRole.getRoleId();
//		try {
//			Result result = new Result();
//			
//			Date roleLeaveTime = role.getLeaveFactionTime();
//			if(null != roleLeaveTime){
//				int timeDiff = (int)DateUtil.dateDiffMinute(new Date(), roleLeaveTime);
//				int cd = this.factionCreateConfig.getJoinCd();
//				if(timeDiff < cd){
//					return result.setInfo(GameContext.getI18n().messageFormat(TextId.Faction_Join_Cd_Not_Enough, cd, cd - timeDiff));
//				}
//			}
//			
//			synchronized (faction.getFactionRoleLock()) {
//				Map<Integer,FactionRole> frMap = this.factionRoleCache.get(factionId);
//				if(Util.isEmpty(frMap)) {
//					return result.setInfo(Status.Faction_Member_Full.getTips());
//				}
//				if(frMap.size() >= faction.getMaxMemberNum()) {
//					return result.setInfo(Status.Faction_Member_Full.getTips());
//				}
//				frMap.put(roleId, factionRole);
//			}
//			
//			factionRole.setRoleLevel(role.getLevel());
//			factionRole.setCreateDate(new Date());
//			factionRole.setPosition(FactionPositionType.Member.getType());
//			
//			this.baseDAO.insert(factionRole);
//			//更新门派信息，并入库
//			int num = faction.getMemberNum() + 1;
//			faction.setMemberNum(num);
//			this.baseDAO.update(faction);
//			//门派ID
//			role.setFactionId(factionId);
//			this.notifyFactionRoleHeadShowChange(roleId);
//			//在系统频道给新成员发消息
//			String selfMsg = GameContext.getI18n().messageFormat(TextId.FACTION_ADD_ROLE_MSG, faction.getFactionName());
//			this.chatApp.sendSysMessage(ChatSysName.System, ChannelType.System, selfMsg, null, role);
//			//门派频道内广播有新成员加入
//			String factionMsg = role.getRoleName() + GameContext.getI18n().getText(TextId.FACTION_ADD_FACTION_MSG);
//			this.chatApp.sendSysMessage(ChatSysName.System, ChannelType.Faction, factionMsg, null, faction);
//			this.changePosition(faction);
//			
//			//重新计算仓库容量
//			C0078_FactionWarehouseResetInternalMessage internalReqMsg = new C0078_FactionWarehouseResetInternalMessage();
//			internalReqMsg.setRole(role);
//			GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(), 
//					internalReqMsg, emptyChannelSession, true);
//			return result.success();
//		} catch (RuntimeException e) {
//			this.logger.error("FactionApp.addFactionRole error: ", e);
//			synchronized (faction.getFactionRoleLock()) {
//				this.factionRoleCache.get(factionId).remove(roleId);
//			}
//			role.setFactionId("");
//			throw new ServiceException("FactionAppImpl.addFactionRole exception", e);
//		}
//	}
//	
//	@Override
//	public Map<Integer,FactionRole> getFactionRoleMap(String factionId){
//		if(Util.isEmpty(factionId)){
//			return null;
//		}
//		Faction faction = this.getFaction(factionId);
//		if(null == faction){
//			return null;
//		}
//		this.initFactionRoleCache(faction);
//		return this.factionRoleCache.get(factionId);
//	}
//	
//	@Override
//	public List<FactionRole> getFactionRoleList(String factionId){
//		List<FactionRole> frList = new ArrayList<FactionRole>();
//		Map<Integer,FactionRole> frMap = this.getFactionRoleMap(factionId);
//		if(Util.isEmpty(frMap)){
//			return frList;
//		}
//		frList.addAll(frMap.values());
//		this.sortFactionRoleByPosition(frList);
//		return frList;
//	}
//	
//	/**
//	 * 根据贡献度排序
//	 * @return
//	 */
//	private List<FactionRole> getFactionRoleListByCon(String factionId){
//		List<FactionRole> frList = new ArrayList<FactionRole>();
//		Map<Integer,FactionRole> frMap = this.getFactionRoleMap(factionId);
//		if(Util.isEmpty(frMap)){
//			return frList;
//		}
//		frList.addAll(frMap.values());
//		this.sortFactionRoles(frList);
//		return frList;
//	}
//	
//	private void sortFactionRoleByPosition(List<FactionRole> frList){
//		Collections.sort(frList, new Comparator<FactionRole>() {
//			public int compare(FactionRole fr1, FactionRole fr2) {
//				if(fr1.getPosition() < fr2.getPosition()) {
//					return -1;
//				}
//				if(fr1.getPosition() > fr2.getPosition()) {
//					return 1;
//				}
//				return 0;
//			}
//		});
//	}
//	
//	/**
//	 * 门派成员排序
//	 * 帮主牌第一位 其他人按贡献度排序
//	 * @param frList
//	 */
//	private void sortFactionRoles(List<FactionRole> frList){
//		Collections.sort(frList, new Comparator<FactionRole>() {
//			public int compare(FactionRole fr1, FactionRole fr2) {
//				//按贡献度高低排序
//				if(fr1.getTotalContribution() > fr2.getTotalContribution()){
//					return -1;
//				}
//				if(fr1.getTotalContribution() < fr2.getTotalContribution()){
//					return 1;
//				}
//				//贡献度相同的，按加入门派时间排序
//				long t1 = fr1.getCreateDate().getTime();
//				long t2 = fr2.getCreateDate().getTime();
//				if(t1 < t2){
//					return -1;
//				}
//				if(t1 > t2){
//					return 1;
//				}
//				return 0;
//			}
//		});
//	}
//	
//	/**
//	 * 加载门派成员
//	 * 从帮众缓存取成员信息时，最好先调用下该方法
//	 */
//	private void initFactionRoleCache(Faction faction){
//		if(null == faction){
//			return;
//		}
//		String factionId = faction.getFactionId();
//		if(!Util.isEmpty(this.factionRoleCache.get(factionId))){
//			return;
//		}
//		synchronized(faction.getFactionRoleLock()) {
//			if(!Util.isEmpty(this.factionRoleCache.get(factionId))){
//				return;
//			}
//			this.factionRoleCache.put(factionId, new HashMap<Integer,FactionRole>());
//			List<FactionRole> selectList = this.baseDAO.selectList(FactionRole.class, "factionId", factionId);
//			for(FactionRole fr : selectList){
//				if(null == fr){
//					continue;
//				}
//				this.factionRoleCache.get(factionId).put(fr.getRoleId(), fr);
//				if(fr.getPositionType() != FactionPositionType.Member) {
//					faction.getPositionRoleMap().put(fr.getRoleId(), fr);
//				}
//			}
//		}
//	}
//	
//	/**
//	 * 向帮众缓存增加一条成员信息（在缓存不为空的时候）
//	 */
//	private void addFactionRoleToCache(String factionId, FactionRole factionRole){
//		if(null == this.factionRoleCache.get(factionId)){
//			return;
//		}
//		this.factionRoleCache.get(factionId).put(factionRole.getRoleId(), factionRole);
//	}
//	
//	@Override
//	public FactionRole searchFactionRole(String factionId,int roleId){
//		Map<Integer,FactionRole> map = this.factionRoleCache.get(factionId);
//		if(null == map){
//			return this.baseDAO.selectEntity(FactionRole.class, "roleId", roleId);
//		}
//		return map.get(roleId);
//	}
//	
//	@Override
//	public FactionRole getFactionRole(RoleInstance role){
//		Faction faction = this.getFaction(role);
//		if(null == faction){
//			return null;
//		}
//		this.initFactionRoleCache(faction);
//		return this.factionRoleCache.get(faction.getFactionId()).get(role.getIntRoleId());
//	}
//	
//	@Override
//	public Result demisePresident(RoleInstance leader, int roleId) throws ServiceException{
//		try {
//			Result result = new Result();
//			if(!leader.hasFaction()){
//				return result.setInfo(Status.Faction_Not_Own.getTips());
//			}
//			String factionId = leader.getFactionId();
//			Faction faction = this.getFaction(factionId);
//			this.initFactionRoleCache(faction);
//			//验证角色是否是这个门派的帮主
//			int leaderId = leader.getIntRoleId();
//			FactionRole oldLeader = this.factionRoleCache.get(factionId).get(leaderId);
//			if(faction.getLeaderId() != leaderId ||
//					FactionPositionType.Leader != FactionPositionType.getPosition(oldLeader.getPosition())){
//				return result.setInfo(Status.Faction_Demise_Not_Leader.getTips());
//			}
//			RoleInstance role = this.getRoleInstance(roleId);
//			if(null == role){
//				return result.setInfo(Status.Faction_Demise_Error.getTips());
//			}
//			String roleFactionId = role.getFactionId();
//			if(Util.isEmpty(roleFactionId)){
//				return result.setInfo(Status.Faction_Member_Not_Exist.getTips());
//			}
//			if(!roleFactionId.equals(factionId)){
//				return result.setInfo(Status.Faction_Member_Not_Exist.getTips());
//			}
//			FactionRole newLeader = this.factionRoleCache.get(factionId).get(roleId);
//			if(null == newLeader){
//				return result.setInfo(Status.Faction_FAILURE.getTips());
//			}
//			//修改新帮主的职位
//			newLeader.setPosition(FactionPositionType.Leader.getType());
//			this.baseDAO.update(newLeader);
//			//修改老帮主的职位
//			oldLeader.setPosition(FactionPositionType.Member.getType());
//			this.baseDAO.update(oldLeader);
//			//设置新帮主
//			String roleName = role.getRoleName();
//			faction.setLeaderId(roleId);
//			faction.setLeaderName(roleName);
//			this.baseDAO.update(faction);
//			this.notifyFactionRoleHeadShowChange(leaderId);
//			this.notifyFactionRoleHeadShowChange(roleId);
//			//重新排职位--同步门派总贡献
//			this.changePosition(faction);
//			//禅让成功，门派频道发消息
//			String oldLeaderName = leader.getRoleName();
//			String info = GameContext.getI18n().messageFormat(TextId.FACTION_DEMISE_PRESIDENT_MSG, oldLeaderName, roleName);
//			this.chatApp.sendSysMessage(ChatSysName.Faction, ChannelType.Faction, info, null, faction);
//			//给新帮主浮动提示
//			boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(roleId));
//			String tips = GameContext.getI18n().messageFormat(TextId.FACTION_DEMISE_PRESIDENT_MAIL_MSG,oldLeaderName ,faction.getFactionName());
//			if(isOnline) {
//				GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(roleId), new C0003_TipNotifyMessage(tips));
//				//主推权限列表
//				this.notifyFactionRolePosition(role);
//			}else{
//				this.sendMail(roleId, GameContext.getI18n().getText(TextId.FACTION_DEMISE_PRESIDENT_MAIL_TITLE), tips);
//			}
//			//主推原帮主的权限
//			this.notifyFactionRolePosition(leader);
//			return result.success();
//		} catch (RuntimeException e) {
//			this.logger.error("FactionApp.demisePresident error: ", e);
//			throw new ServiceException("FactionAppImpl.demisePresident exception", e);
//		}
//	}
//	
//	@Override
//	public Result inviteJoinFaction(RoleInstance leader, RoleInstance role){
//		Result result = new Result();
//		
//		Date roleLeaveTime = role.getLeaveFactionTime();
//		if(null != roleLeaveTime){
//			int timeDiff = (int)DateUtil.dateDiffMinute(new Date(), roleLeaveTime);
//			int cd = this.factionCreateConfig.getJoinCd();
//			if(timeDiff < cd){
//				return result.setInfo(GameContext.getI18n().messageFormat(TextId.Faction_Invite_Cd_Not_Enough, cd, cd - timeDiff));
//			}
//		}
//		
//		if(!this.getPowerTypeSet(leader).contains(FactionPowerType.Invite_To_Join)){
//			return result.setInfo(GameContext.getI18n().getText(TextId.Faction_Invite_Position_NOT_HAS));
//		}
//		Faction faction = this.getFaction(leader);
//		if(faction.isFull()){
//			return result.setInfo(Status.Faction_Member_Full.getTips());
//		}
//		if(role.getLevel() < factionCreateConfig.getJoinRoleLevel()){
//			return result.setInfo(Status.Faction_Target_Level_NotEnough.getTips());
//		}
//		if(role.hasFaction()){
//			if(role.getFactionId().equals(leader.getFactionId())){
//				return result.setInfo(Status.Faction_Role_Exist.getTips());
//			}
//			return result.setInfo(Status.Faction_Target_Own.getTips());
//		}
//		return result.success();
//	}
//	
//	@Override
//	public Result acceptInvitation(RoleInstance role, String factionId) throws ServiceException{
//		Result result = new Result();
//		if(role.getLevel() < factionCreateConfig.getJoinRoleLevel()){
//			return result.setInfo(Status.Faction_RoleLevel_NotEnough.getTips());
//		}
//		if(role.hasFaction()){
//			return result.setInfo(Status.Faction_ApplyRole_OwnFaction.getTips());
//		}
//		Faction faction = this.getFaction(factionId);
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Exist.getTips());
//		}
//		if(faction.isFull()){
//			return result.setInfo(Status.Faction_Member_Full.getTips());
//		}
//		FactionRole factionRole = new FactionRole();
//		factionRole.setRoleId(role.getIntRoleId());
//		factionRole.setFactionId(factionId);
//		factionRole.setRoleName(role.getRoleName());
//		factionRole.setRoleLevel(role.getLevel());
//		factionRole.setCareer(role.getCareer());
//		factionRole.setSex(role.getSex());
//		//以前在此门派的贡献度
//		factionRole.setContribution(role.getFactionContributeValue(factionId));
//		factionRole.setTotalContribution(role.getFactionTotalContributeValue(factionId));
//		factionRole.setUserId(role.getUserId());
//		
//		C0072_FactionRoleAddInternalMessage internalReqMsg = new C0072_FactionRoleAddInternalMessage();
//		internalReqMsg.setRoleId(role.getRoleId());
//		internalReqMsg.setFaction(faction);
//		internalReqMsg.setFactionRole(factionRole);
//		internalReqMsg.setOperaRoleId(role.getRoleId());
//		GameContext.getUserSocketChannelEventPublisher().publish(role.getUserId(), 
//				internalReqMsg, emptyChannelSession, true);
//		return result.success();
//	}
//	
//	/**
//	 * 初始化门派建筑配置（门派建筑NPC的创建、升级配置）
//	 */
//	private void initBuildConfigMap(){
//		String fileName = XlsSheetNameType.faction_build.getXlsName();
//		String sheetName = XlsSheetNameType.faction_build.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			for(FactionBuild config : XlsPojoUtil.sheetToList(sourceFile, sheetName, FactionBuild.class)){
//				if(null == config){
//					continue;
//				}
//				int buildId = config.getBuildId();
//				String npcId = config.getNpcId();
//				if(!this.buildConfigMap.containsKey(buildId)){
//					this.buildConfigMap.put(buildId, new LinkedHashMap<Integer,FactionBuild>());
//				}
//				if(!Util.isEmpty(npcId)) {
//					//加载建筑NPC出生信息时，须验证模板中是否存在
//					NpcTemplate npcTemplate = GameContext.getNpcApp().getBuildNpcTemplate(npcId, config.getLevel());
//					if(null == npcTemplate){
//						Log4jManager.checkFail();
//						Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",npcId=" + npcId + ",The npcTemplate does not exist!");
//						continue;
//					}
//					config.setImage((byte) npcTemplate.getResid());//建筑图片ID
//					if(!buildNpcMap.containsKey(npcId)) {
//						buildNpcMap.put(npcId, buildId);
//					}
//				}
//				config.init();
//				this.buildConfigMap.get(buildId).put(config.getLevel(), config);
//			}
//		}catch(Exception e){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ",e);
//		}
//	}
//	
//	/** 从库中查询门派已经拥有的建筑信息 */
//	private void initFactionBuild(Faction faction){
//		try{
//			Map<Integer, List<Integer>> deleteMap = new HashMap<Integer, List<Integer>>();
//			for(FactionBuild build : this.baseDAO.selectList(FactionBuild.class, "factionId", faction.getFactionId())){
//				if(null == build){
//					continue;
//				}
//				FactionBuild fb = buildConfigMap.get(build.getBuildId()).get(build.getLevel());
//				if(null == fb) {
//					continue;
//				}
//				int buildId = fb.getBuildId();
//				//技能依赖建筑，加载建筑是加载技能
//				if(fb.getType() == FactionBuildFuncType.Faction_Skill) {
//					Map<Integer, Integer> skillMap = fb.getSkillMap();
//					if(!Util.isEmpty(skillMap)){
//						faction.getFactionSkillMap().putAll(skillMap);
//					}
//				}
//				faction.getBuildingMap().put(buildId, fb);
//				
//				if(!deleteMap.containsKey(buildId)) {
//					deleteMap.put(buildId, new ArrayList<Integer>());
//				}
//				deleteMap.get(buildId).add(build.getLevel());
//			}
//			for(Integer key : deleteMap.keySet()) {
//				List<Integer> list = deleteMap.get(key);
//				if(Util.isEmpty(list)){
//					continue;
//				}
//				if(list.size() == 1){
//					continue;
//				}
//				int maxLevel = 0;
//				for(Integer level : list) {
//					if(null == level) {
//						continue;
//					}
//					if(level > maxLevel) {
//						maxLevel = level;
//					}
//				}
//				this.factionDAO.deleteFactionBuild(faction.getFactionId(), key, maxLevel);
//			}
//		}catch(Exception e){
//			logger.error("FactionApp select faction buildings error:" + e);
//		}
//	}
//	
//	@Override
//	public Map<String,NpcInstance> createBuildNpcInstance(String factionId) {
//		Faction faction = this.getFaction(factionId);
//		if(null == faction){
//			return null;
//		}
//		Map<String,NpcInstance> buildMap = new HashMap<String,NpcInstance>();
//		try{
//			for(FactionBuild build : faction.getBuildingMap().values()){
//				if(null == build){
//					continue;
//				}
//				if(Util.isEmpty(build.getNpcId())) {
//					continue;
//				}
//				NpcTemplate npcTemplate = GameContext.getNpcApp().getBuildNpcTemplate(build.getNpcId(), build.getLevel());
//				if(null == npcTemplate){
//					continue;
//				}
//				//根据配置信息设置建筑的坐标值
//				if(!this.buildConfigMap.containsKey(String.valueOf(build.getNpcId()))){
//					continue;
//				}
//				FactionBuild config = this.buildConfigMap.get(build.getNpcId()).get(build.getLevel());
//				if(null == config){
//					continue;
//				}
//				build.setX(config.getX());
//				build.setY(config.getY());
//				NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(npcTemplate, build);
//				buildMap.put(npcTemplate.getNpcid(), npcInstance);
//			}
//		}catch(Exception e){
//			logger.error("FactionApp initialize faction buildings error:" + e);
//		}
//		return buildMap;
//	}
//
//	@Override
//	public Result createOrUpgradeBuilding(RoleInstance role, int buildId){
//		Result result = new Result();
//		//TODO:判断权限
//		
//		if(!this.getPowerTypeSet(role).contains(FactionPowerType.UpgradeBuild)){
//			return result.setInfo(Status.Faction_Build_Upgrade_No_Position.getTips());
//		}
//		
//		Faction faction = this.getFaction(role);
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Exist.getTips());
//		}
//		int level = 0;
//		if(faction.getBuildingMap().containsKey(buildId)){
//			level = faction.getBuildingMap().get(buildId).getLevel();
//		}
//		
//		
//		if(level >= this.getBuildConfigMap().get(buildId).size()){
//			return result.setInfo(Status.Faction_Build_MaxLevel.getTips());
//		}
//		FactionBuild build = this.buildConfigMap.get(buildId).get(level+1);
//		return this.buildNpcBorn(role, build);
//	}
//	
//	@Override
//	public Result createBuilding(RoleInstance role, int buildId){
//		Result result = new Result();
//		
//		Faction faction = this.getFaction(role);
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Exist.getTips());
//		}
//		
//		int level = 0;
//		if(faction.getBuildingMap().containsKey(buildId)){
//			level = faction.getBuildingMap().get(buildId).getLevel();
//		}
//		
//		if(level >= this.getBuildConfigMap().get(buildId).size()){
//			return result.setInfo(Status.Faction_Build_MaxLevel.getTips());
//		}
//		FactionBuild build = this.buildConfigMap.get(buildId).get(level+1);
//		return this.buildNpcBorn(role, build);
//	}
//	
//	/** 创建新的建筑NPC实例 */
//	private Result buildNpcBorn(RoleInstance role, FactionBuild build){
//		Result result = new Result();
//		if(null == build){
//			return result.setInfo(Status.FAILURE.getTips());
//		}
//		//判断建筑创建条件
//		result = build.canBuild(role);
//		if(!result.isSuccess()){
//			return result;
//		}
//		Faction faction = this.getFaction(role);
//		
//		result = GameContext.getFactionFuncApp().changeFactionMoney(faction, OperatorType.Decrease, build.getFactionMoney(),OutputConsumeType.faction_money_build,"");
//		if(!result.isSuccess()){
//			return result;
//		}
//		
//		String npcId = build.getNpcId();
//		if(null != npcId && npcId.length() > 0) {
//			NpcTemplate npcTemplate = GameContext.getNpcApp().getBuildNpcTemplate(npcId, build.getLevel());
//			//判断门派地图副本是否创建，如果没有创建则直接修改门派建筑的数据库,如果存在则需更新地图建筑NPC信息并发消息通知
//			String mapId = factionCreateConfig.getFactionMapId();
//			String factionMapContaninerId = ID + faction.getFactionId();
//			Map<String,MapCopyContainer> contaninerMap = GameContext.getMapApp().getCopyContainerMap();
//			if(contaninerMap.containsKey(factionMapContaninerId)){
//				NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(npcTemplate, build);
//				MapContainer mapContainer = contaninerMap.get(factionMapContaninerId);
//				MapInstance mapInstance = mapContainer.createMapInstance(GameContext.getMapApp().getMap(mapId), role);
//				this.buildNpcEnterMap(mapInstance, npcInstance);//建筑NPC进入地图消息
//			}
//		}
//		build.setFactionId(role.getFactionId());
//		build.setCreateDate(new Date());
//		if(!faction.getBuildingMap().containsKey(build.getBuildId())){
//			this.baseDAO.insert(build);
//		}else{
//			this.baseDAO.update(build);
//			//公会建筑升级记录
//			FactionRecord factionRecord = new FactionRecord();
//			factionRecord.setType(FactionRecordType.Faction_Record_Build.getType());
//			factionRecord.setFactionId(faction.getFactionId());
//			factionRecord.setData1(build.getFactionMoney());
//			factionRecord.setData2(String.valueOf(build.getLevel()));
//			factionRecord.setData3(build.getBuildName());
//			GameContext.getFactionFuncApp().createFactionRecord(factionRecord);
//		}
//		faction.getBuildingMap().put(build.getBuildId(), build);//更新门派已有的建筑信息
////		this.sendUpdateBuildMessage(role, build);//更新建筑列表消息
//		//公会升级更新公会技能
//		if(build.getType() == FactionBuildFuncType.Faction_Skill) {
//			GameContext.getFactionFuncApp().upgradeFactionSkill(faction,build);
//		}
//		//公会升级仓库扩充
//		if(build.getType() == FactionBuildFuncType.Faction_Warehouse) {
//			GameContext.getFactionFuncApp().expansionWarehouse(faction,build);
//		}
//		return result.success();
//	}
//	
//	/** 创建或升级建筑的返回消息 */
////	private void sendUpdateBuildMessage(RoleInstance role, FactionBuild build){
////		FactionCreateBuildingRespMessage resp = new FactionCreateBuildingRespMessage();
////		resp.setBuildId(build.getBuildId());
////		resp.setBuildName(build.getBuildName());
////		int level = build.getLevel();
////		resp.setBuildLevel((byte) level);
////		resp.setImage(build.getImage());
////		resp.setDesc(build.getDesc());
////		//判断是否是最高级别
////		resp.setStatus(TWO);//2:表示达到最高等级
////		int buildId = build.getBuildId();
////		int maxLevel = this.buildConfigMap.get(buildId).size();
////		if(level < maxLevel){
////			FactionBuild nextBuild = this.buildConfigMap.get(buildId).get(level+1);
////			resp.setStatus(ZERO);//0:表示不可创建
////			if(nextBuild.canBuild(role).canSuccess()){
////				resp.setStatus(ONE);//1:表示可以创建
////			}
////			resp.setFactionLevel((byte) nextBuild.getFactionLevel());
////			resp.setFactionRoleNum((byte) nextBuild.getFactionRoleNum());
////			resp.setFactionMoney((byte) nextBuild.getFactionMoney());
////			resp.setNextLevelDesc(nextBuild.getDesc());
////		}
////		role.getBehavior().sendMessage(resp);
////	}
//	
//	/** 建筑NPC进入地图 */
//	private void buildNpcEnterMap(MapInstance mapInstance, NpcInstance npcInstance){
//		npcInstance.setMapInstance(mapInstance);
//		npcInstance.setMapId(mapInstance.getInstanceId());
//		NpcInstanceFactroy.createNpcBehavior(npcInstance);
//		mapInstance.addAbstractRole(npcInstance);//将建筑NPC添加到地图的npcList中
//		//通知地图内所有人有NPC进入
//		for(RoleInstance mapRole : mapInstance.getRoleList()){
//			C0204_MapUserEntryNoticeRespMessage resp = new C0204_MapUserEntryNoticeRespMessage();
//			resp.setItem(Converter.getNpcBodyItem(npcInstance, mapRole));
//			mapRole.getBehavior().sendMessage(resp);
//		}
//	}
//	
//	@Override
//	public Result buildNpcDeath(String factionId, NpcInstance npc){
//		Result result = new Result();
//		String npcId = npc.getNpcid();
//		
//		Integer id = buildNpcMap.get(npcId);
//		if(null == id) {
//			return result.setInfo(Status.Faction_Build_Not_Exist.getTips()); 
//		}
//		int buildId = id.intValue();
//		Faction faction = this.getFaction(factionId);
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Exist.getTips());
//		}
//		if(!faction.getBuildingMap().containsKey(buildId)){
//			return result.setInfo(Status.Faction_Build_Not_Exist.getTips());
//		}
//		FactionBuild build = faction.getBuildingMap().get(buildId);
//		if(null == build){
//			return result.setInfo(Status.Faction_Build_Not_Exist.getTips());
//		}
//		int level = build.getLevel();
//		if(level <= 1){
//			this.baseDAO.delete(FactionBuild.class, "factionId", factionId, "buildId", build.getBuildId());
//			faction.getBuildingMap().remove(buildId);
//			return result.success();
//		}
//		//建筑NPC死亡之后降级
//		NpcTemplate npcTemplate = GameContext.getNpcApp().getBuildNpcTemplate(npcId, level);
//		NpcInstance npcInstance = NpcInstanceFactroy.createNpcInstance(npcTemplate, build);
//		this.baseDAO.update(build);
//		faction.getBuildingMap().put(buildId, build);//更新门派已有的建筑信息
//		this.buildNpcEnterMap(npc.getMapInstance(), npcInstance);
//		return result.success();
//	}
//	
//	@Override
//	public Map<Integer, Map<Integer, FactionBuild>> getBuildConfigMap() {
//		return this.buildConfigMap;
//	}
//	
//	@Override
//	public Result modifyFactionDesc(RoleInstance role, String desc) {
//		Result result = new Result();
//		Faction faction = this.getFaction(role);
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Own.getTips());
//		}
//		if(!this.getPowerTypeSet(role).contains(FactionPowerType.Modify_Desc)){
//			return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_MODIFY_DESC_POSITION_NOT_HAS));
//		}
//		String factionDesc = GameContext.getIllegalWordsService().doFilter(desc.trim());
//		if(factionDesc.indexOf("*") != -1){
//			return result.setInfo(Status.Faction_Desc_Illegal.getTips());
//		}
//		if(factionDesc.length() > factionConfig.getFactionDescLength()){
//			return result.setInfo(Status.Faction_Desc_TooLonger.getTips());
//		}
//		faction.setFactionDesc(Util.replaceComma(factionDesc));
//		this.baseDAO.update(faction);
//		return result.success();
//	}
//
//	@Override
//	public Result modifySignature(RoleInstance role, String signature) {
//		Result result = new Result();
//		Faction faction = this.getFaction(role);
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Own.getTips());
//		}
//		String signatureFilter = GameContext.getIllegalWordsService().doFilter(signature.trim());
//		if(signatureFilter.indexOf("*") != -1){
//			return result.setInfo(Status.Faction_Desc_Illegal.getTips());
//		}
//		if(signatureFilter.length() > factionConfig.getFactionRoleSignatureLength()){
//			return result.setInfo(Status.Faction_Desc_TooLonger.getTips());
//		}
//		FactionRole factionRole = this.getFactionRole(role);
//		factionRole.setSignature(Util.replaceComma(signatureFilter));
//		return result.success();
//	}
//	
//	@Override
//	public boolean canModifyFactionName(String factionName) {
//		return CheckNameUtil.isMatchChangeName(factionName);
//	}
//	
//	@Override
//	public Result modifyFactionName(RoleInstance role, String newName){
//		Result result = new Result();
//		try {
//			Faction faction = this.getFaction(role);
//			if(null == faction){
//				return result.setInfo(Status.Faction_Not_Own.getTips());
//			}
//			String oldName = faction.getFactionName();
//			if(!canModifyFactionName(oldName)){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_MODIFY_NAME_CAN_NOT));
//			}
//			if(!this.getPowerTypeSet(role).contains(FactionPowerType.Modify_Name)){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_MODIFY_NAME_POSITION_NOT_HAS));
//			}
//			String factionName = newName.trim();
//			//过滤敏感词
//			String illegalChar = GameContext.getIllegalWordsService().findIllegalChar(factionName);
//			if(null != illegalChar){
//				return result.setInfo(Status.Faction_Name_Illegal.getTips());
//			}
//			//过滤禁用词
//			String forbidChar = GameContext.getIllegalWordsService().findForbiddenChar(factionName);
//			if(null != forbidChar){
//				return result.setInfo(Status.Faction_Name_Forbidden.getTips());
//			}
//			if(factionName.length() > this.factionConfig.getFactionNameLength()){
//				return result.setInfo(Status.Faction_Name_TooLonger.getTips());
//			}
//			if(null != this.searchFactionByName(factionName)){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_MODIFY_NAME_ALREADY_HAS));
//			}
//			//修改门派名称
//			faction.setFactionName(factionName);
//			this.baseDAO.update(faction);
//			/*//TODO:消息量太大，先不通知了
//			//通知帮众头顶显示变化
//			for(RoleInstance member : this.getAllOnlineFactionRole(faction)){
//				if(null == member){
//					continue;
//				}
//				this.notifyFactionRoleHeadShowChange(member);
//			}*/
//			return result.success();
//		} catch (RuntimeException e) {
//			this.logger.error("FactionApp.modifyFactionName error: ", e);
//			return result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
//		}
//	}
//
//	public BaseDAO getBaseDAO() {
//		return baseDAO;
//	}
//
//	public void setBaseDAO(BaseDAO baseDAO) {
//		this.baseDAO = baseDAO;
//	}
//
//	public FactionConfig getFactionConfig() {
//		return factionConfig;
//	}
//
//	public void setFactionConfig(FactionConfig factionConfig) {
//		this.factionConfig = factionConfig;
//	}
//
//	public OnlineCenter getOnlineCenter() {
//		return onlineCenter;
//	}
//
//	public void setOnlineCenter(OnlineCenter onlineCenter) {
//		this.onlineCenter = onlineCenter;
//	}
//
//	public ChatApp getChatApp() {
//		return chatApp;
//	}
//
//	public void setChatApp(ChatApp chatApp) {
//		this.chatApp = chatApp;
//	}
//
//	public IdFactory<String> getIdFactory() {
//		return idFactory;
//	}
//
//	public void setIdFactory(IdFactory<String> idFactory) {
//		this.idFactory = idFactory;
//	}
//
//	public Cache<String, Map<Integer, FactionRole>> getFactionRoleCache() {
//		return factionRoleCache;
//	}
//
//	public void setFactionRoleCache(Cache<String, Map<Integer, FactionRole>> factionRoleCache) {
//		this.factionRoleCache = factionRoleCache;
//	}
//
//	@Override
//	public Set<FactionPowerType> getPowerTypeSet(FactionPositionType positionType) {
//		return this.positionPowerSet.get(positionType);
//	}
//	
//	public boolean haveFactionPowerType(RoleInstance role,FactionPowerType powerType){
//		if(null ==  powerType){
//			return false ;
//		}
//		return this.getPowerTypeSet(role).contains(powerType) ;
//	}
//	
//	@Override
//	public Set<FactionPowerType> getPowerTypeSet(RoleInstance role) {
//		Set<FactionPowerType> powerSet = new HashSet<FactionPowerType>();
//		FactionRole factionRole = this.getFactionRole(role);
//		if(null == factionRole){
//			return powerSet;
//		}
//		FactionPositionType positionType = factionRole.getPositionType();
//		if(null == positionType){
//			return powerSet;
//		}
//		powerSet.addAll(this.positionPowerSet.get(positionType));
//		return powerSet;
//	}
//	
//	@Override
//	public FactionPositionType getPositionType(RoleInstance role) {
//		FactionRole factionRole = this.getFactionRole(role);
//		if(null == factionRole){
//			return null;
//		}
//		return factionRole.getPositionType();
//	}
//	
//	@Override
//	public Result changeContributeNum(RoleInstance role, OperatorType operatorType, int value) {
//		Result result = new Result();
//		if(0 == value){
//			return result.success();
//		}
//		Faction faction = this.getFaction(role);
//		FactionRole factionRole = this.getFactionRole(role);
//		if(null == faction || null == factionRole){
//			return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_NOT_HAVE_FACTION));
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
//			//同步门派属性（门派等级变化、门派职位变化）
//			if(chanageValue > 0) {
//				this.synchContribution(faction, chanageValue);
//			}
//		}
//		return result.success();
//	}
//	
//	/**
//	 * 同步门派贡献度
//	 * 门派等级变化
//	 * 门派职位变化
//	 * @param faction
//	 * @param chanageValue
//	 */
//	private void synchContribution(Faction faction, int chanageValue){
//		try {
//			//chanageValue为0，表示门派内职位变更时，重新排序职位
//			if(null == faction){
//				return;
//			}
//			//同步职位
//			C0076_FactionChangePostionInternalMessage internalReqMsg = new C0076_FactionChangePostionInternalMessage();
//			internalReqMsg.setFaction(faction);
//			GameContext.getUserSocketChannelEventPublisher().publish(faction.getFactionId(),
//					internalReqMsg, emptyChannelSession, true);
//			synchronized(faction.getSynchContributeLock()){
//				//门派总贡献变化
//				int contribution = faction.getContribution();
//				contribution += chanageValue;
//				if(contribution < 0){
//					contribution = 0;
//				}
//				faction.setContribution(contribution);
//				faction.setSaveDbStateType(SaveDbStateType.Update);
//				int contribute = faction.getContribution();
//				byte curLevel = faction.getFactionLevel();
//				byte nextLevel = (byte)(curLevel + 1);
//				FactionUpgrade curUpgrade = this.factionUpgradeMap.get(curLevel);
//				FactionUpgrade nextUpgrade = this.factionUpgradeMap.get(nextLevel);
//				if(contribute > curUpgrade.getContribution()){
//					if(null != nextUpgrade) {
//						contribute -= curUpgrade.getContribution();
//						faction.setContribution(contribute);
//						faction.setFactionLevel(nextLevel);
//						faction.setMaxMemberNum(nextUpgrade.getMaxMemberNum());
//						faction.setMaxIntegral(nextUpgrade.getMaxIntegral());
//						faction.setMaxDonateCount(nextUpgrade.getMaxDonateCount());
//					}else{
//						faction.setContribution(curUpgrade.getContribution());
//					}
//				}
//				byte newLevel = faction.getFactionLevel();//新等级
//				//门派等级变化，在门派频道内通知
//				if(newLevel > curLevel){//升级
//					String message = GameContext.getI18n().messageFormat(TextId.FACTION_UP_GRADE_MSG, newLevel);
//					this.chatApp.sendSysMessage(ChatSysName.System, ChannelType.Faction, message, null, faction);
//					//门派升级记录
//					FactionRecord factionRecord = new FactionRecord();
//					factionRecord.setType(FactionRecordType.Faction_Record_Level.getType());
//					factionRecord.setFactionId(faction.getFactionId());
//					factionRecord.setData1(newLevel);
//					GameContext.getFactionFuncApp().createFactionRecord(factionRecord);
//				}
//			}
//		} catch (Exception e) {
//			this.logger.error("FactionApp.synchContribution error:", e);
//		}
//	}
//	
//	@Override
//	public void changePosition(Faction faction){
//		try{
//			FactionUpgrade factionUpgrade = this.factionUpgradeMap.get(faction.getFactionLevel());
//			if(null == factionUpgrade) {
//				return;
//			}
//			//职位变化
//			int deputyNum = factionUpgrade.getDeputyNum();//副帮主数量
//			int eliteNum = factionUpgrade.getEliteNum();//精英数量
//			int positionNum = deputyNum + eliteNum + 1;
//			//新职位的所有成员ID
//			Set<Integer> roleIdSet = new HashSet<Integer>();
//			//帮众排序：按贡献度排序（第一个是帮主）
//			List<FactionRole> frList = this.getFactionRoleListByCon(faction.getFactionId());
//			int size = Math.min(positionNum, frList.size());
//			for(int i=0; i<size; i++){
//				FactionRole fr = frList.get(i);
//				if(null == fr){
//					continue;
//				}
//				if(FactionPositionType.Leader == fr.getPositionType()){
//					continue;
//				}
//				FactionPositionType positionType = FactionPositionType.Elite;
//				if(i <= deputyNum){
//					positionType = FactionPositionType.Deputy;
//				}
//				int roleId = fr.getRoleId();
//				roleIdSet.add(roleId);
//				//更新门派上的职位Map
//				faction.getPositionRoleMap().put(roleId, fr);
//				//职位没有变化
//				if(fr.getPositionType() == positionType){
//					continue;
//				}
//				//修改职位
//				this.changePosition(fr, positionType);
//			}
//			//从副帮主或精英降级到普通帮众
//			Iterator<FactionRole> iterator = faction.getPositionRoleMap().values().iterator();
//			while(iterator.hasNext()){
//				FactionRole fr = iterator.next();
//				if(null == fr || FactionPositionType.Leader == fr.getPositionType()){
//					continue;
//				}
//				int roleId = fr.getRoleId();
//				//属于新职位中的成员
//				if(roleIdSet.contains(roleId)){
//					continue;
//				}
//				//修改职位
//				FactionPositionType positionType = FactionPositionType.Member;
//				this.changePosition(fr, positionType);
//				//从职位Map中移除
//				iterator.remove();
//			}
//		}catch(Exception e){
//			logger.error("FactionApp.changePosition",e);
//		}
//	}
//	
//	/**
//	 * 修改帮众的职位（不包括帮主）
//	 * @param factionRole
//	 * @param positionType
//	 */
//	private void changePosition(FactionRole factionRole, FactionPositionType positionType){
//		try {
//			if(null == factionRole || null == positionType){
//				return;
//			}
//			//不能修改帮主的职位
//			if(FactionPositionType.Leader == factionRole.getPositionType()){
//				return;
//			}
//			int roleId = factionRole.getRoleId();
//			RoleInstance role = this.getRoleInstance(roleId);
//			if(null == role){
//				return;
//			}
//			//修改职位
//			factionRole.setPositionType(positionType);
//			//通知头顶门派职位变化
//			this.notifyFactionRoleHeadShowChange(roleId);
//			//不在线的才需要入库
//			boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(roleId));
//			if(!isOnline){
//				this.baseDAO.update(factionRole);
//			}
//		} catch (Exception e) {
//			this.logger.error("FactionApp.chanagePosition error:", e);
//		}
//	}
//	
//	@Override
//	public IntegralResult changeFactionIntegral(RoleInstance role, OperatorType operatorType, 
//			int value,IntegralChannel channel,boolean isForcedAdd){
//		IntegralResult result = new IntegralResult();
//		if(0 == value){
//			result.success();
//			return result ;
//		}
//		if(value < 0){
//			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
//			return result ;
//		}
//		if(OperatorType.Add != operatorType && OperatorType.Decrease != operatorType){
//			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
//			return result ;
//		}
//		Faction faction = this.getFaction(role);
//		if(null == faction){
//			result.setInfo(GameContext.getI18n().getText(TextId.FACTION_NOT_HAVE_FACTION));
//			return result ;
//		}
//		synchronized(faction.getIntegralLock()){
//			int integral = faction.getIntegral();
//			int maxIntegral = faction.getMaxIntegral();
//			if(OperatorType.Add == operatorType && integral  >= maxIntegral){
//				 result.setInfo(GameContext.getI18n().getText(TextId.FACTION_INTEGRAL_MAX));
//				 return result ;
//			}
//			if(OperatorType.Decrease == operatorType && integral < value){
//				 result.setInfo(GameContext.getI18n().getText(TextId.FACTION_INTEGRAL_NOT_ENOUGH));
//				 return result ;
//			}
//			int newIntegral = integral ;
//			FactionIntegralLogType integralLogType = FactionIntegralLogType.Consume;//积分日志类型
//			if(OperatorType.Add == operatorType){
//				newIntegral += value;
//				integralLogType = FactionIntegralLogType.Income;
//			}else if(OperatorType.Decrease == operatorType){
//				newIntegral -= value;
//			}
//			int effectValue = value;
//			result.setEffectValue(value);
//			if(newIntegral > maxIntegral){
//				effectValue = maxIntegral - integral ;
//				result.setEffectValue(effectValue);
//				//非强制添加
//				if(!isForcedAdd){
//					 result.setInfo(GameContext.getI18n().messageFormat(TextId.FACTION_INTEGRAL_ADD_MAX, effectValue));
//					 //标志为必须二次确认
//					 result.setMustConfirm();
//					 return result ;
//				}
//				newIntegral = maxIntegral;
//			}
//			faction.setIntegral(newIntegral);
//			faction.setSaveDbStateType(SaveDbStateType.Update);
//			//门派积分变化记录
//			FactionIntegralLog integralLog = new FactionIntegralLog();
//			integralLog.setFactionId(faction.getFactionId());
//			integralLog.setRoleId(role.getIntRoleId());
//			integralLog.setRoleName(role.getRoleName());
//			integralLog.setOperateType(integralLogType.getType());
//			integralLog.setIntegral(effectValue);
//			integralLog.setRemainIntegral(newIntegral);
//			integralLog.setOperateTime(new Date());
//			integralLog.setInfo(channel.getContent());
//			try {
//				this.baseDAO.insert(integralLog);
//			} catch (RuntimeException e) {
//				this.logger.error("FactionApp.changeFactionIntegral error: ", e);
//			}
//		}
//		result.success();
//		return result;
//	}
//	
//	@Override
//	public void login(RoleInstance role) {
//		try {
//			String roleId = role.getRoleId();
//			String factionId = getFactionId(roleId);
//			if(!Util.isEmpty(factionId)){
//				role.setFactionId(factionId);
//			}
//			
//			//查询角色曾经加入过门派的贡献值
//			List<FactionContribute> list = GameContext.getBaseDAO().selectList(FactionContribute.class, "roleId", roleId);
//			if(!Util.isEmpty(list)){
//				for(FactionContribute fcb : list){
//					if(null == fcb){
//						continue;
//					}
//					role.getFactionContributeMap().put(fcb.getFactionId(), fcb);
//				}
//			}
//			//加载门派捐献相关信息
//			GameContext.getFactionFuncApp().roleLoginInitDonate(role);
//		} catch (RuntimeException e) {
//			this.logger.error("FactionApp.login() error: ", e);
//		}
//	}
//	
//	@Override
//	public Map<FactionDescType, FactionDescribe> getDescribeMap() {
//		return this.describeMap;
//	}
//
//	
//	@Override
//	public Map<Byte,FactionUpgrade> getFactionUpgradeMap() {
//		return this.factionUpgradeMap;
//	}
//
//	public FactionDAOImpl getFactionDAO() {
//		return factionDAO;
//	}
//
//	public void setFactionDAO(FactionDAOImpl factionDAO) {
//		this.factionDAO = factionDAO;
//	}
//
//	@Override
//	public List<FactionIntegralLog> getFactionIntegralLogList(String factionId,
//			int startRow, int rows, FactionIntegralLogType integralLogType) {
//		if(Util.isEmpty(factionId) || 0 == rows || null == integralLogType){
//			return null;
//		}
//		if(FactionIntegralLogType.All == integralLogType){
//			return this.factionDAO.getIntegralLogList(factionId, startRow, rows);
//		}
//		return this.factionDAO.getIntegralLogList(factionId, startRow, rows, integralLogType.getType());
//	}
//
//	@Override
//	public void clearIntegralLog() {
//		try {
//			this.factionDAO.deleteIntegralLogBeforeOneWeek();
//		} catch (RuntimeException e) {
//			this.logger.error("FactionApp.clearIntegralLog", e);
//		}
//	}
//
//	@Override
//	public Map<Integer, FactionRole> getFactionRoleCache(String factionId) {
//		return this.factionRoleCache.get(factionId);
//	}
//
//	@Override
//	public Result impeach(RoleInstance role) {
//		Result result = new Result();
//		Faction faction = this.getFaction(role);
//		if(null == faction){
//			return result.setInfo(Status.Faction_Not_Own.getTips());
//		}
//		result = faction.canImpeach(role);
//		if(!result.isSuccess()) {
//			return result;
//		}
//		int goodsNum = factionCreateConfig.getImpeachGoodsNum();
//		synchronized(faction.getImpeachLock()){
//			int goodsId = factionCreateConfig.getImpeachGoodsId();
//			//----------------------------
//			//快速购买
//			result = GameContext.getQuickBuyApp().doQuickBuy(role, goodsId, goodsNum, OutputConsumeType.faction_impeach_consume, null);
//			if(!result.isSuccess()){
//				return result;
//			}
//			//----------------------------
//			impeachUpdate(faction.getFactionId(),role);
//		}
//		return result.success();
//	}
//	
//	private void impeachUpdate(String factionId, RoleInstance role) {
//		try {
//			Faction faction = this.getFaction(factionId);
//			if(null == faction){
//				return;
//			}
//			//超过弹劾保护时间，更换帮主，重排职位
//			synchronized(faction.getImpeachLock()){
//				int impeachRoleId = role.getIntRoleId();
//				faction.setSaveDbStateType(SaveDbStateType.Update);
//				Map<Integer,FactionRole> frMap = this.getFactionRoleMap(factionId);
//				if(Util.isEmpty(frMap)){
//					return;
//				}
//				int oldLeaderId = faction.getLeaderId();
//				FactionRole fr = frMap.get(impeachRoleId);
//				FactionRole oldLeader = frMap.get(oldLeaderId);
//				if(null == oldLeader || null == fr){
//					return;
//				}
//				//更换帮主
//				fr.setPosition(FactionPositionType.Leader.getType());
//				oldLeader.setPosition(FactionPositionType.Member.getType());
//				String impeachRoleName = fr.getRoleName();
//				faction.setLeaderId(impeachRoleId);
//				faction.setLeaderName(impeachRoleName);
//				this.baseDAO.update(faction);
//				//通知头顶显示
//				this.notifyFactionRoleHeadShowChange(oldLeaderId);
//				this.notifyFactionRoleHeadShowChange(impeachRoleId);
//				//重新排职位--同步门派总贡献
//				this.changePosition(faction);
//				//弹劾成功，门派频道发消息
//				String message = impeachRoleName + GameContext.getI18n().getText(TextId.FACTION_IMPEACH_SUCCESS);
//				this.chatApp.sendSysMessage(ChatSysName.Faction, ChannelType.Faction, message, null, faction);
//				//给新帮主发邮件，通知弹劾成功
//				String context1 = GameContext.getI18n().messageFormat(TextId.FACTION_IMPEACH_MAIL_CONTENT, oldLeader.getRoleName());
//				this.sendMail(impeachRoleId, GameContext.getI18n().getText(TextId.FACTION_IMPEACH_MAIL_TITLE), context1);
//				//给原帮主发邮件，通知被弹劾
//				String context2 =  GameContext.getI18n().messageFormat(TextId.FACTION_IMPEACH_OLD_LEADER_MAIL_CONTENT, impeachRoleName);
//				this.sendMail(oldLeaderId, GameContext.getI18n().getText(TextId.FACTION_IMPEACH_MAIL_TITLE), context2);
//			}
//		} catch (Exception e) {
//			this.logger.error("FactionApp.impeachUpdate error: ", e);
//		}
//	}
//
//	@Override
//	public List<Faction> getFactionListByName(String factionName) {
//		List<Faction> list = this.factionDAO.getFactionByName(factionName);
//		if(Util.isEmpty(list)){
//			return list;
//		}
//		//从内存中获取所查找的门派
//		List<Faction> factionList = new ArrayList<Faction>();
//		for(Faction f : list){
//			if(null == f){
//				continue;
//			}
//			Faction faction = this.factionMap.get(f.getFactionId());
//			if(null == faction){
//				continue;
//			}
//			factionList.add(faction);
//		}
//		return factionList;
//	}
//
//	@Override
//	public Map<String, Faction> getFactionMap() {
//		return this.factionMap;
//	}
//
//	@Override
//	public List<FactionRole> getFactionRoleListByOnline(List<FactionRole> frList) {
//		List<FactionRole> list = new ArrayList<FactionRole>();
//		try {
//			List<FactionRole> onlineList = new ArrayList<FactionRole>();
//			List<FactionRole> offlineList = new ArrayList<FactionRole>();
//			for(FactionRole fr : frList) {
//				boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(String.valueOf(fr.getRoleId()));
//				if(isOnline){
//					onlineList.add(fr);
//					continue;
//				}
//				offlineList.add(fr);
//			}
//			list.addAll(onlineList);
//			list.addAll(offlineList);
//		} catch (Exception e) {
//			logger.error("FactionApp.getFactionRoleListByOnline error: ", e);
//		}
//		return list;
//	}
//	
//	@Override
//	public String getBuildName(int buildId, int buildLevel) {
//		String buildName = "";
//		Map<Integer, FactionBuild> buildMap = GameContext.getFactionApp().getBuildConfigMap().get(buildId);
//		if(null == buildMap) {
//			return buildName;
//		}
//		FactionBuild fb = buildMap.get(buildLevel);
//		if(null == fb) {
//			return buildName;
//		}
//		buildName = fb.getBuildName();
//		return buildName;
//	}
//
//	@Override
//	public List<Faction> getFactionRankList(int size) {
//		List<Faction> list = new ArrayList<Faction>();
//		list.addAll(this.factionMap.values());
//		this.sortFaction(list);
//		int rankSize = Util.getSubListSize(list.size(), size);
//		List<Faction> sortList = list.subList(0, rankSize);
//		return sortList;
//	}
//	
//	/**
//	 * 给有权限的在线成员发送入会申请
//	 * @param factionId
//	 * @return
//	 */
//	private List<FactionRole> sendApplyMessage(String factionId, RoleInstance applyRole){
//		List<FactionRole> frList = new ArrayList<FactionRole>();
//		Map<Integer,FactionRole> frMap = this.getFactionRoleMap(factionId);
//		if(Util.isEmpty(frMap)){
//			return frList;
//		}
//		C1739_FactionApplyJoinNotifyMessage message = null;
//		for(FactionRole fr : frMap.values()) {
//			String roleId = String.valueOf(fr.getRoleId());
//			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
//			if(null == role){
//				continue;
//			}
//			
//			if(!this.getPowerTypeSet(role).contains(FactionPowerType.Dispose_Apply_Join)){
//				continue;
//			}
//			
//			if(null == message) {
//				message = new C1739_FactionApplyJoinNotifyMessage();
//				message.setRoleId(applyRole.getIntRoleId());
//				message.setRoleName(applyRole.getRoleName());
//			}
//			
//			role.getBehavior().sendMessage(message);
//		}
//		return frList;
//	}
//	
//	/**
//	 * 主推权限变化
//	 * @param role
//	 */
//	private void notifyFactionRolePosition(RoleInstance role){
//		try{
//			C1721_FactionSelfPowerListRespMessage message = new C1721_FactionSelfPowerListRespMessage();
//			int power = this.getFactionPosition(role);
//			message.setPower(power);
//			role.getBehavior().sendMessage(message);
//		}catch(Exception e){
//			this.logger.error("FactionApp.notifyFactionRoleHeadShowChange error: ", e);
//		}
//	}
//	
//	@Override
//	public int getFactionPosition(RoleInstance role) {
//		int power = 0;
//		Set<FactionPowerType> powerSet = this.getPowerTypeSet(role);
//		if(Util.isEmpty(powerSet)) {
//			return power;
//		}
//		int index = 1;
//		for(FactionPowerType factionPowerType : FactionPowerType.values()) {
//			if(powerSet.contains(factionPowerType)) {
//				power = power |(1<<index);
//			}
//			index++;
//		}
//		return power;
//	}
//	
//	@Override
//	public String getFactionMapId(){
//		return this.factionCreateConfig.getFactionMapId();
//	}
//	
//	@Override
//	public String getFactionCreateInfo(){
//		String str = MessageFormat.format(factionCreateConfig.getInfo(), factionCreateConfig.getRoleLevel(), factionCreateConfig.getConsumeGoods().getName());
//		return str;
//	}
//	
//	@Override
//	public int getImpeachDay(){
//		return this.factionCreateConfig.getImpeachDay();
//	}
//
//	@Override
//	public List<Faction> getFactionWarFactionList(int campFactinCount) {
//		List<Faction> factionWarList = new ArrayList<Faction>();
//		/*try{
//			List<Faction> list = new ArrayList<Faction>();
//			list.addAll(this.factionMap.values());
//			if(Util.isEmpty(list)){
//				return factionWarList;
//			}
//			this.sortFaction(list);
//			List<Faction> kunLunList = new ArrayList<Faction>();
//			List<Faction> yuXuList = new ArrayList<Faction>();
//			List<Faction> jiuLiList = new ArrayList<Faction>();
//			for(Faction faction : list) {
//				if(null == faction) {
//					continue;
//				}
//				if(faction.getFactionCamp() == CampType.immortal.getType() &&
//						kunLunList.size() < campFactinCount) {
//					kunLunList.add(faction);
//				}
//				if(faction.getFactionCamp() == CampType.human.getType() &&
//						yuXuList.size() < campFactinCount) {
//					yuXuList.add(faction);
//				}
//				if(faction.getFactionCamp() == CampType.goblin.getType() &&
//						jiuLiList.size() < campFactinCount) {
//					jiuLiList.add(faction);
//				}
//				if(kunLunList.size() >= campFactinCount && yuXuList.size() >= campFactinCount && jiuLiList.size() >= campFactinCount){
//					break;
//				}
//			}
//			factionWarList.addAll(kunLunList);
//			factionWarList.addAll(yuXuList);
//			factionWarList.addAll(jiuLiList);
//			if(factionWarList.size() < 6){
//				return null;
//			}
//		}catch(Exception e){
//			logger.error("FactionApp.getFactionWarFactionList error",e);
//		}*/
//		return factionWarList;
//	}
//	
//	@Override
//	public int getFactionRolePosition(String roleId, String factionId){
//		int position = -1;
//		Faction faction = this.getFaction(factionId);
//		if(null == faction){
//			return position;
//		}
//		this.initFactionRoleCache(faction);
//		FactionRole fr = this.factionRoleCache.get(factionId).get(Integer.parseInt(roleId));
//		if(null == fr){
//			return position;
//		}
//		return fr.getPosition();
//	}
//	
//	@Override
//	public Faction getFirstFaction(){
//		try{
//			List<Faction> list = new ArrayList<Faction>();
//			list.addAll(this.factionMap.values());
//			if(Util.isEmpty(list)){
//				return null;
//			}
//			this.sortFaction(list);
//			return list.get(0);
//		}catch(Exception e){
//			logger.debug("getFirstFaction error",e);
//		}
//		return null;
//	}
//	
//	@Override
//	public Faction getCampFirstFaction(byte campId){
//		try{
//			List<Faction> list = new ArrayList<Faction>();
//			list.addAll(this.factionMap.values());
//			if(Util.isEmpty(list)){
//				return null;
//			}
//			this.sortFaction(list);
//			for(Faction faction : list){
//				if(null == faction){
//					continue;
//				}
//				if(faction.getFactionCamp() == campId){
//					return faction;
//				}
//			}
//		}catch(Exception e){
//			logger.debug("getFirstFaction error",e);
//		}
//		return null;
//	}
//	
//	/**
//	 * 玩家离开门派时，如果在门派地图直接踢出
//	 * @param role
//	 */
//	private void factionMapKickRole(RoleInstance role){
//		try{
//			String factionMapId = this.getFactionMapId();
//			MapInstance mapInstance = role.getMapInstance();
//			if(null == mapInstance){
//				return;
//			}
//			String mapId = mapInstance.getMap().getMapId();
//			if(!factionMapId.equals(mapId)){
//				return;
//			}
//			GameContext.getUserMapApp().changeMap(role, role.getCopyBeforePoint());
//		}catch(Exception e){
//			logger.error("FactionApp.factionMapKickRole",e);
//		}
//	}
//	
//	@Override
//	public String getFactionId(String roleId){
//		return this.factionDAO.getFactionIdByRoleId(roleId);
//	}
//}