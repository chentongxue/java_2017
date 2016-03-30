package com.game.draco.app.copy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.team.PlayerTeam;
import sacred.alliance.magic.app.team.Team;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.GoodsUseType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.MapConstant;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapMultiCopyContainer;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.copy.domain.CopyCount;
import com.game.draco.app.copy.team.ApplyInfo;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.item.CopyPanelItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.request.C0209_CopyEnterReqMessage;
import com.game.draco.message.request.C0210_CopyEnterConfirmReqMessage;
import com.game.draco.message.response.C0256_CopyPanelRespMessage;

public class CopyLogicAppImpl implements CopyLogicApp{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private BaseDAO baseDAO;
	
	//副本配置<副本ID,CopyConfig>
	private Map<Short, CopyConfig> copyConfigMap = new LinkedHashMap<Short, CopyConfig>();
	//按副本类型区分<copyType, List>
	private Map<CopyType, List<CopyConfig>> copyConfigTypeMap = new HashMap<CopyType, List<CopyConfig>>();
	/** 副本地图配置：KEY=地图ID,VALUE=地图配置信息 */
	private Map<String, CopyMapConfig> mapConfigMap = new HashMap<String, CopyMapConfig>();
	/** 副本的有序地图列表：KEY=副本ID,VALUE=地图配置序列*/
	private Map<Short,List<CopyMapConfig>> copyMapRelation = new HashMap<Short,List<CopyMapConfig>>();
	//副本掉落<副本ID，List>
	private Map<Short, List<Integer>> fallMap = new HashMap<Short, List<Integer>>();
	
	//出副本不可传回原点时，传送到此点
	private Point failurePoint = new Point();
	
	/** 地图刷怪匹配规则：KEY=地图ID,VALUE=匹配关系对象 */
	private Map<String,List<CopyMapRoleRule>> copyMapRoleRuleMap = new HashMap<String,List<CopyMapRoleRule>>();
	
	private MapApp getMapApp(){
		return GameContext.getMapApp();
	}
	
	@Override
	public CopyMapConfig getMapConfig(String mapId){
		return this.mapConfigMap.get(mapId);
	}
	
	/** 会长是否开启公会副本 **/
	@Override
	public boolean hadCreateUnionInstance(Union union){
		if(union == null){
			return false;
		}
//		for(String containerId : union.getCopyProgMap().values()){
//			MapMultiCopyContainer container = (MapMultiCopyContainer)this.getMapApp().getCopyContainerMap().get(containerId);
//			if(container != null && !container.canDestroy()){
//				return true;
//			}
//		}
		return false;
	}
	
	/** 获取进入失败传送点 **/
	@Override
	public Point getFailurePoint() {
		return this.failurePoint;
	}
	
	@Override
	public void enterCopy(RoleInstance role, short copyId){
		//必须删除副本掉线的标记信息
		role.setCopyLostReLoginInfo(null);
		//判断副本类型（组队副本、公会副本）
		CopyConfig copyConfig = this.getCopyConfig(copyId);
		if(copyConfig == null){
			return ;
		}
		CopyType copyType = copyConfig.getCopyType();
		if(copyType == null){
			return ;
		}
		Result result = copyConfig.enterCondition(role);
		if(!result.isSuccess()){
			C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
			msg.setMsgContext(result.getInfo());
			role.getBehavior().sendMessage(msg);
			return ;
		}
		//如果要求门派等级，则为门派副本。没有门派或门派等级不足不能进入。
		int fcLvl = copyConfig.getNeedFactionLvl();
		if(fcLvl > 0){
			if(!role.hasUnion()){
				this.sendTipNotifyMessage(role, Status.Copy_Enter_No_Faction.getTips());
				return;
			}
			if(role.getUnionLevel() < fcLvl){
				this.sendTipNotifyMessage(role, Status.Copy_Enter_No_FactionLvl.getTips().replace(Wildcard.Number, String.valueOf(fcLvl)));
				return;
			}
		}
		if(CopyType.personal == copyType){
			this.enterPersonalCopy(role, copyConfig);
		}else if(copyType == CopyType.team){
			this.enterTeamCopy(role, copyId);
		}
		/*else if(copyType == CopyType.faction){
			this.enterFactionCopy(role, copyId);
		}*/
	}
	
	/**
	 * 进入个人副本
	 * @param role
	 * @param copyConfig
	 */
	private void enterPersonalCopy(RoleInstance role, CopyConfig copyConfig){
		//判断自己的次数
		if(!this.isCountEnough(role, copyConfig)){
			this.sendTipNotifyMessage(role, Status.Copy_Today_Count_Finished.getTips());
			return;
		}
		//判断体力值
		if(!this.isPowerEnough(role, copyConfig)){
			this.sendTipNotifyMessage(role, Status.Copy_Role_Power_Not_Enough.getTips());
			return;
		}
		//创建副本容器
		MapMultiCopyContainer container = new MapMultiCopyContainer();
		container.initByCreate(role, copyConfig);
		//进入副本地图
		this.enterCopyMap(role, container);
	}
	
	/**
	 * 副本次数是否足够
	 * @param role 角色
	 * @param copyConfig 副本配置
	 * @return
	 */
	private boolean isCountEnough(RoleInstance role, CopyConfig copyConfig){
		int count = copyConfig.getCount();
		//-1表示不计次数
		if(count < 0){
			return true;
		}
		int currCount = this.getCopyCurrCount(role, copyConfig.getCopyId());
		return currCount < count;
	}
	
	/**
	 * 体力值是否足够
	 * @param role
	 * @param copyConfig
	 * @return
	 */
	private boolean isPowerEnough(RoleInstance role, CopyConfig copyConfig){
		int power = copyConfig.getPower();
		//不消耗体力值
		if(power <= 0){
			return true;
		}
		int currPower = role.get(AttributeType.curPower);
		return currPower >= power;
	}
	
	/**
	 * 进入副本地图
	 * @param role
	 * @param container
	 */
	private void enterCopyMap(RoleInstance role, MapMultiCopyContainer container){
		try{
			CopyConfig copyConfig = container.getCopyConfig();
			short copyId = container.getCopyId();
			if(!container.haveEnterCopy(role.getRoleId())){
				//再次判断自己的次数（组队副本的队员进入）
				if(!this.isCountEnough(role, copyConfig)){
					this.sendTipNotifyMessage(role, Status.Copy_Today_Count_Finished.getTips());
					return;
				}
				//再次判断体力值
				if(!this.isPowerEnough(role, copyConfig)){
					this.sendTipNotifyMessage(role, Status.Copy_Role_Power_Not_Enough.getTips());
					return;
				}
				//第一次进入副本，需要扣除相应的道具
				int goodsId = copyConfig.getNeedGoodsId();
				if(goodsId > 0){
					GoodsUseType goodsUseType = copyConfig.getGoodsUseType();
					int goodsNum = copyConfig.getNeedGoodsNum();
					if(GoodsUseType.Consume == goodsUseType && goodsNum > 0){
						GameContext.getUserGoodsApp().deleteForBag(role, goodsId, goodsNum, OutputConsumeType.npc_transmit);
					}
				}
				//扣除体力值
				this.reduceCopyPower(role, copyConfig);
				//扣除副本次数
				this.reduceCopyCount(role, copyId);
				//扣除角色的副本次数时，必须调用，让容器知道哪些角色是合法的
				container.deductRoleCopyCount(role);
				//将角色添加到容器中
				container.addRoleToEnterSet(role.getRoleId());
				//队长首次进入，需要给其他队员发送弹板提示
				Team team = role.getTeam();
				if(null != team && team.isLeader(role) && team.getPlayerNum() > 1){
					this.sendTeamRoleEnterMsg(role, copyConfig);
				}
			}
			//进入消息
			this.sendEnterMessage(role, copyConfig);
			//切换地图 
			Point targetPoint = new Point(copyConfig.getEnterMapId(), copyConfig.getMapX(), copyConfig.getMapY());
			GameContext.getUserMapApp().changeMap(role, targetPoint);
			//统计进入副本次数
			//GameContext.getCountApp().incrEnterCopyCount((RoleInstance)role, copyConfig.getSignType());
			//重置副本内的兑换
			GameContext.getExchangeApp().resetExchangeByCopyId((RoleInstance)role, copyId);
			//重置副本内的召唤
			GameContext.getSummonApp().resetSummonByCopyId((RoleInstance)role, copyId);
		}catch(Exception e){
			logger.error("copyLogicApp.enterCopyMap error: ",e);
		}
	}
	
	/**
	 * 扣除副本次数
	 * @param role
	 * @param copyId
	 */
	private void reduceCopyCount(RoleInstance role, short copyId){
		//第一次进入副本容器，需要修改副本记录，副本进入次数增加
		CopyCount copyCount = role.getCopyCountMap().get(copyId);
		Date now = new Date();
		if(null == copyCount){
			copyCount = new CopyCount();
			copyCount.setRoleId(role.getIntRoleId());
			copyCount.setCopyId(copyId);
			copyCount.setEnterNum((byte) 1);
			copyCount.setUpdateTime(now);
			this.baseDAO.insert(copyCount);
			role.getCopyCountMap().put(copyId, copyCount);
		}else{
			int enterNum = copyCount.getEnterNum() + 1;
			copyCount.setEnterNum((byte) enterNum);
			copyCount.setUpdateTime(now);
			this.baseDAO.update(copyCount);
		}
	}
	
	/**
	 * 扣除体力值
	 * @param role
	 * @param copyConfig
	 */
	private void reduceCopyPower(RoleInstance role, CopyConfig copyConfig){
		int power = copyConfig.getPower();
		if(power <= 0){
			return;
		}
		//扣除体力值
		role.getBehavior().changeAttribute(AttributeType.curPower, OperatorType.Decrease, power);
		role.getBehavior().notifyAttribute();
	}
	
	/** 是否通关,能进入下一层 **/
	@Override
	public String isCopyPass(RoleInstance role){
		CopyMapConfig config = this.getMapConfig(role.getMapId());
		if(config == null){
			return null;
		}
		if(config.isNeedKillAll() && this.hasEnemy(role)){
			return config.getNeedKillAllTips();
		}
		if(this.hasBoss(role, config.getNeedKillNpcId())){
			return config.getNeedKillAllTips();
		}
		return null;
	}
	
	/** 是否通关,能进入下一层 **/
	@Override
	public boolean isCopyPass(MapInstance instance){
		if(instance == null){
			return true;
		}
		CopyMapConfig config = this.getMapConfig(instance.getMap().getMapId());
		if(config == null){
			return true;
		}
		//没有通关配置
		if(!config.hasPassCondition()){
			return true;
		}
		if(config.isNeedKillAll() && this.hasEnemy(instance.getNpcList())){
			return false;
		}
		if(this.hasBoss(instance.getNpcList(), config.getNeedKillNpcId())){
			return false;
		}
		return true;
	}
	
	/** 角色登录逻辑 **/
	@Override
	public void login(RoleInstance role){
		if(role == null){
			return ;
		}
		try{
			//容错处理，角色上线所在地图为副本类型，则放到容错点
			this.mapFaultTolerant(role);
			List<CopyCount> copyCountList = this.baseDAO.selectList(CopyCount.class, CopyCount.ROLEID, role.getRoleId());
			if(!Util.isEmpty(copyCountList)){
				for(CopyCount count : copyCountList){
					if(null == count){
						continue;
					}
					role.getCopyCountMap().put(count.getCopyId(), count);
				}
			}
		}catch(Exception e){
			this.logger.error("CopyLogicApp.login error: ", e);
		}
	}
	
	/**
	 * 地图容错
	 * 角色上线所在地图为副本类型，则放到容错点
	 * 在副本掉线保护之内的除外
	 * @param role
	 */
	private void mapFaultTolerant(RoleInstance role){
		//是否在副本掉线保护时间之内
		if(this.isCopySafety(role)){
			return;
		}
		//清掉副本掉线标记信息（别的地方已经清了，这里再次清，以防万一）
		role.setCopyLostReLoginInfo(null);
		sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(role.getMapId());
		if(map.getMapConfig().getLogictype() == MapLogicType.unionInstanceLogic.getType()
				|| map.getMapConfig().getLogictype() == MapLogicType.copyLogic.getType()){
			role.setMapId(this.failurePoint.getMapid());
			role.setMapX(this.failurePoint.getX());
			role.setMapY(this.failurePoint.getY());
		}
	}
	
	/** 根据副本ID获取副本配置 **/
	@Override
	public CopyConfig getCopyConfig(short copyId){
		return this.copyConfigMap.get(copyId);
	}
	
	/** 获取副本掉落信息 **/
	@Override
	public List<Integer> getCopyFalls(short copyId){
		return fallMap.get(copyId);
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadCopyConfig();
		this.loadMapConfig();//副本地图配置必须在副本配置和刷怪规则之后加载
		this.loadMapRoleRule();//地图与刷怪匹配关系须在二者加载之后
		this.loadFalls();
		this.loadFailurePoint();
		//清除副本掉线标识
		this.clearAllCopyLostReLoginInfo();
	}
	
	/**
	 * 清除所有角色的副本掉线标识（服务器启动的时候调用）
	 */
	private void clearAllCopyLostReLoginInfo(){
		GameContext.getRoleDAO().clearAllCopyLostReLoginInfo();
	}
	
	/** 加载副本进入配置 */
	private void loadCopyConfig(){
		String fileName = XlsSheetNameType.copy_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_config.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<CopyConfig> configList = XlsPojoUtil.sheetToList(sourceFile, sheetName, CopyConfig.class);
			//按等级需求排序（配置表中可能不是有序的）
			this.sortCopyConfigList(configList);
			for(CopyConfig config : configList){
				//验证副本配置信息
				config.checkAndInit("load fileName=" + fileName + " sheetName=" + sheetName + ".");
				this.copyConfigMap.put(config.getCopyId(), config);
				CopyType copyType = config.getCopyType();
				if(!this.copyConfigTypeMap.containsKey(copyType)){
					this.copyConfigTypeMap.put(copyType, new ArrayList<CopyConfig>());
				}
				this.copyConfigTypeMap.get(copyType).add(config);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error("load fileName="+fileName+" sheetName="+sheetName+" is error.");
			Log4jManager.checkFail();
		}
	}
	
	/**
	 * 将副本配置表排序
	 * @param list
	 */
	private void sortCopyConfigList(List<CopyConfig> list){
		Collections.sort(list, new Comparator<CopyConfig>(){
			@Override
			public int compare(CopyConfig conf1, CopyConfig conf2) {
				if(conf1.getMinLevel() < conf2.getMinLevel()){
					return -1;
				}
				if(conf1.getMinLevel() > conf2.getMinLevel()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	/** 加载地图配置 */
	private void loadMapConfig(){
		String fileName = XlsSheetNameType.copy_map_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_map_config.getSheetName();
		String tip = "load fileName=" + fileName + ",sheetName="+ sheetName + ".";
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<CopyMapConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CopyMapConfig.class);
			for(CopyMapConfig config : list){
				if(null == config){
					continue;
				}
				String mapId = config.getMapId();
				short copyId = config.getCopyId();
				String info = tip + "copyId=" + copyId + ",";
				if(null == this.getCopyConfig(copyId)){
					Log4jManager.CHECK.error(info + "this copy is not exist");
					Log4jManager.checkFail();
					continue;
				}
				info += "mapId=" + mapId + ",";
				sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
				if(map == null){
					this.checkFail(info + "the map is not exist");
					continue ;
				}
				MapConfig mapConfig = map.getMapConfig();
				if(MapLogicType.copyLogic != mapConfig.getMapLogicType()){
					this.checkFail(info + "it's MapLogicType != MapLogicType.copyLogic.");
					continue;
				}
				if(mapConfig.getCopyId() > 0 ){
					this.checkFail(info + "this map is in copyId="+ mapConfig.getCopyId());
					continue ;
				}
				String ruleId = config.getRuleId();
				CopyNcpRuleType npcRuleType = CopyNcpRuleType.getCopyType(config.getRuleType());
				if(null == npcRuleType){
					this.checkFail(info + "npcRuleType=" + config.getRuleType() + ", the npcRuleType is not exist!");
				}
				//刷怪规则是固定配置的必须要配规则ID
				if(CopyNcpRuleType.Default == npcRuleType && Util.isEmpty(ruleId)){
					this.checkFail(info + "ruleType=" + config.getRuleType() + ",but ruleId is empty.");
					continue ;
				}
				//判断规则是否存在
				if(!Util.isEmpty(ruleId)){
					boolean ruleIsExist = GameContext.getRefreshRuleApp().ruleIsExist(Integer.parseInt(ruleId));
					if(!ruleIsExist) {
						this.checkFail(info + "ruleId=" + ruleId + ",this rule is not exist.");
						continue ;
					}
				}
				//验证刷新的跳转点是否在卡死复位点附近
				if(Util.inCircle(mapConfig.getMaporiginx(), mapConfig.getMaporiginy(), 
						config.getJumpX(), config.getJumpY(), MapConstant.JUMP_POINT_EFFECT_RADIOS)){
					this.checkFail(info + "jumpX and jumpY config error,it's nearby maporigin point.");
				}
				mapConfig.setCopyId(copyId);
				mapConfigMap.put(mapId, config);
				if(!this.copyMapRelation.containsKey(copyId)){
					this.copyMapRelation.put(copyId, new ArrayList<CopyMapConfig>());
				}
				this.copyMapRelation.get(copyId).add(config);
			}
			//副本地图排序
			for(List<CopyMapConfig> mapList : this.copyMapRelation.values()){
				if(Util.isEmpty(mapList)){
					continue;
				}
				this.sortCopyMapRelation(mapList);
				CopyMapConfig firstMap = mapList.get(0);
				firstMap.setFirstMap(true);
				CopyMapConfig lastMap = mapList.get(mapList.size()-1);
				lastMap.setLastMap(true);
			}
		}catch(Exception e){
			this.checkFail(tip + "is error.");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
	/**
	 * 地图配置列表排序
	 * @param mapList
	 */
	private void sortCopyMapRelation(List<CopyMapConfig> mapList){
		Collections.sort(mapList, new Comparator<CopyMapConfig>(){
			@Override
			public int compare(CopyMapConfig map1, CopyMapConfig map2) {
				if(map1.getMapIndex() < map2.getMapIndex()){
					return -1;
				}
				if(map1.getMapIndex() > map2.getMapIndex()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	/**
	 * 加载地图刷怪匹配关系配置
	 */
	private void loadMapRoleRule(){
		String fileName = XlsSheetNameType.copy_map_role_rule.getXlsName();
		String sheetName = XlsSheetNameType.copy_map_role_rule.getSheetName();
		String info = "load fileName=" + fileName + ",sheetName="+ sheetName + ".";
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<CopyMapRoleRule> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CopyMapRoleRule.class);
			for(CopyMapRoleRule config : list){
				if(null == config){
					continue;
				}
				String mapId = config.getMapId();
				if(!this.mapConfigMap.containsKey(mapId)){
					Log4jManager.CHECK.error(info + "mapId=" + mapId + ",this mapId is not exist.");
					Log4jManager.checkFail();
					continue;
				}
				String ruleId = config.getRuleId();
				boolean ruleIsExist = GameContext.getRefreshRuleApp().ruleIsExist(Integer.parseInt(ruleId));
				if(!ruleIsExist){
					Log4jManager.CHECK.error(info + "ruleId=" + ruleId + ",this ruleId is not exist.");
					Log4jManager.checkFail();
					continue;
				}
				if(!this.copyMapRoleRuleMap.containsKey(mapId)){
					this.copyMapRoleRuleMap.put(mapId, new ArrayList<CopyMapRoleRule>());
				}
				this.copyMapRoleRuleMap.get(mapId).add(config);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error(info, e);
			Log4jManager.checkFail();
		}
	}
	
	/* 加载掉落列表 */
	private void loadFalls(){
		String fileName = XlsSheetNameType.copy_fall_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_fall_config.getSheetName();
		
		List<FallConfig> list = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName, FallConfig.class);
		}catch(Exception e){
			Log4jManager.CHECK.error("load fileName="+fileName+" sheetName="+sheetName+" is error.");
			Log4jManager.checkFail();
		}
		
		if(Util.isEmpty(list)){
			return ;
		}
		
		for(FallConfig config : list){
			short copyId = config.getCopyId();
			int fallGoodsId = config.getFallGoodsId();
			if(fallGoodsId <= 0){
				continue ;
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(fallGoodsId);
			if(goodsBase == null){
				Log4jManager.CHECK.error("load fileName="+fileName+" sheetName="+sheetName+" goodsId="+fallGoodsId+" goods is no exist.");
				Log4jManager.checkFail();
				continue ;
			}
			
			if(fallMap.containsKey(copyId)){
				List<Integer> goods = fallMap.get(copyId);
				goods.add(fallGoodsId);
				continue ;
			}
			List<Integer> goodsList = new ArrayList<Integer>();
			goodsList.add(fallGoodsId);
			fallMap.put(copyId, goodsList);
		}
	}
	
	/* 加载传出副本时，原进入点非法，传送此点 */
	private void loadFailurePoint(){
		String fileName = XlsSheetNameType.copy_point_config.getXlsName();
		String sheetName = XlsSheetNameType.copy_point_config.getSheetName();
		String info = "load fileName=" + fileName + ",sheetName="+ sheetName + ".";
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<String> ratioList = XlsPojoUtil.sheetToStringList(sourceFile, sheetName);
			String mapId = ratioList.get(0);
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
			if(null == map){
				this.checkFail(info + "the map is not exist");
			}
			MapConfig mapConfig = map.getMapConfig();
			//容错点所在地图不能是副本
			if(mapConfig.getCopyId() > 0){
				this.checkFail(info + "the map is copy.");
			}
			//容错点赋值，该地图的卡死复位点
			this.failurePoint.setMapid(mapId);
			this.failurePoint.setX(mapConfig.getMaporiginx());
			this.failurePoint.setY(mapConfig.getMaporiginy());
		}catch(Exception e){
			this.checkFail(info);
		}
	}
	
	//组队副本进入逻辑
	private void enterTeamCopy(RoleInstance role, short copyId){
		Team team = role.getTeam();
		if(team == null){
			team = new PlayerTeam(role);
		}
		MapMultiCopyContainer container = null;
		String containerId = team.getCopyContainerId(copyId);
		if(!Util.isEmpty(containerId)){
			container = (MapMultiCopyContainer) this.getMapApp().getCopyContainer(containerId);
		}
		CopyConfig copyConfig = this.getCopyConfig(copyId);
		boolean canEnter = false;//副本是否可进入
		//副本已经存在，判断可否直接进入
		if(null != container){
			//人数限制判断
			if(!this.hasExceedRoleCount(role, container, copyId)){
				return ;
			}
			//拿到副本容器，需要将状态切为占有状态。
			//如果切换失败，说明已经是销毁状态了，需要重新创建副本。
			//如果切换成功，说明不需要创建副本，可直接进入地图。
			if(container.change_containerState_to_own()){
				canEnter = true;
			}
		}
		if(canEnter){
			//副本存在并且可进入，直接进入副本
			this.enterCopyMap(role, container);
		}else{
			//副本不存在，需要创建副本
			this.createTeamCopyContainer(role, copyConfig, false);
		}
	}
	
	//人数限制判断
	private boolean hasExceedRoleCount(RoleInstance role, MapMultiCopyContainer container, short copyId){
		CopyConfig copyConfig = this.getCopyConfig(copyId);
		if(!copyConfig.roleCountIsFull(container.getRoleCount())){
			return true;
		}
		C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
		String context = Status.Copy_Curr_Role_Num.getTips()
							.replace(Wildcard.Number, String.valueOf(container.getRoleCount()))
							+ Cat.comma + Status.Copy_Bigger_Than_Pre.getTips() + copyConfig.enterRoleCount();
		tipMsg.setMsgContext(context);
		role.getBehavior().sendMessage(tipMsg);
		return false;
	}
	
	/**
	 * 创建组队副本容器
	 * @param role
	 * @param copyConfig
	 * @param isConfirm 当队员次数不足时，确认进入副本
	 */
	private void createTeamCopyContainer(RoleInstance role, CopyConfig copyConfig, boolean isConfirm){
		Team team = role.getTeam();
		if(null == team){
			return;
		}
		if(!team.isLeader(role)){
			//提示队长未进入，队员不可进入
			this.sendTipNotifyMessage(role, Status.Copy_Leader_Not_Enter.getTips());
			return;
		}
		//判断自己的次数
		if(!this.isCountEnough(role, copyConfig)){
			this.sendTipNotifyMessage(role, Status.Copy_Today_Count_Finished.getTips());
			return;
		}
		//判断体力值
		if(!this.isPowerEnough(role, copyConfig)){
			this.sendTipNotifyMessage(role, Status.Copy_Role_Power_Not_Enough.getTips());
			return;
		}
		//如果要求门派等级，则为门派副本。不是一个门派的人不能进入。
		if(copyConfig.getNeedFactionLvl() > 0 && !team.isFactionTeam(role.getUnionId())){
			this.sendTipNotifyMessage(role, Status.Copy_Team_Member_Not_Same_Faction.getTips());
			return;
		}
		if(!copyConfig.roleCountLimit(team.getPlayerNum())){
			this.sendTipNotifyMessage(role, Status.Copy_Not_Suit_Pre.getTips() + copyConfig.enterRoleCount());
			return ;
		}
		short copyId = copyConfig.getCopyId();
		//非确认进入副本时，判断队员的次数，询问队长是否进入
		if(!isConfirm && team.getPlayerNum() > 1){
			StringBuffer buffer = new StringBuffer();
			String cat = "";
			for(AbstractRole member : team.getMembers()){
				if(!this.isCountEnough((RoleInstance) member, copyConfig)){
					buffer.append(cat).append(member.getRoleName());
					cat = Cat.comma;
				}
			}
			//有队员次数不足时，询问队长是否进入
			if(buffer.length() > 0){
				buffer.append(Status.Copy_Team_Member_Today_Count_Finished.getTips());
				short affirmCmdId = new C0210_CopyEnterConfirmReqMessage().getCommandId();
				this.sendConfirmationNotifyMessage(role, affirmCmdId, String.valueOf(copyId), buffer.toString());
				return;
			}
		}
		//创建副本容器
		MapMultiCopyContainer container = new MapMultiCopyContainer();
		container.initByCreate(role, copyConfig);
		//进入副本地图
		this.enterCopyMap(role, container);
	}
	
	/**
	 * 发送提示信息
	 * @param role
	 * @param msgContext
	 */
	private void sendTipNotifyMessage(RoleInstance role, String msgContext){
		C0003_TipNotifyMessage tipMsg = new C0003_TipNotifyMessage();
		tipMsg.setMsgContext(msgContext);
		role.getBehavior().sendMessage(tipMsg);
	}
	
	/**
	 * 发送二次确认消息
	 * @param role
	 * @param affirmCmdId
	 * @param affirmParam
	 * @param info
	 */
	private void sendConfirmationNotifyMessage(AbstractRole role, short affirmCmdId, String affirmParam, String info){
		C0007_ConfirmationNotifyMessage message = new C0007_ConfirmationNotifyMessage();
		message.setAffirmCmdId(affirmCmdId);
		message.setAffirmParam(affirmParam);
		message.setInfo(info);
		role.getBehavior().sendMessage(message);
	}
	
	@Override
	public void teamCopyCreateConfirm(RoleInstance role, String param) {
		try {
			short copyId = Short.valueOf(param);
			CopyConfig copyConfig = this.getCopyConfig(copyId);
			if(null == copyConfig){
				return;
			}
			this.createTeamCopyContainer(role, copyConfig, true);
		} catch (Exception e) {
			this.logger.error("copyLogicApp.teamCopyCreateConfirm error: ", e);
		}
	}
	
	/**
	 * 进入消息
	 * @param role
	 * @param copyConfig
	 */
	private void sendEnterMessage(RoleInstance role, CopyConfig copyConfig){
		CopyType copyType = copyConfig.getCopyType();
		String msg = Status.Copy_Role_Enter_Copy.getTips()
						.replace(Wildcard.Role_Name, role.getRoleName())
						.replace(Wildcard.CopyName, copyConfig.getCopyName());
		switch(copyType){
		case team:
			if(role.hasTeam()){
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Team, msg, null, role.getTeam());
			}
			break;
		case union:
			if(role.hasUnion()){
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Union, msg, null, role.getUnion());
			}
		/*case faction:
			if(role.hasFaction()){
				GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Faction, msg, null, role.getFaction());
			}
			break;*/
		}
	}
	
	private void sendTeamRoleEnterMsg(RoleInstance role, CopyConfig config){
		if(config.getCopyType() != CopyType.team){
			return ;
		}
		Team team = role.getTeam();
		if(!team.isLeader(role)){
			return ;
		}
		short cmdId = new C0209_CopyEnterReqMessage().getCommandId();
		String param = String.valueOf(config.getCopyId());
		String info = Status.Copy_Leader_Create_Copy_Need_Enter.getTips().replace(Wildcard.CopyName, config.getCopyName());
		for(AbstractRole member : team.getMembers()){
			if(team.isLeader(member)){
				continue;
			}
			this.sendConfirmationNotifyMessage(member, cmdId, param, info);
		}
	}
	
	private boolean hasEnemy(Collection<NpcInstance> npcList){
		if(Util.isEmpty(npcList)){
			return false;
		}
		for(NpcInstance npc : npcList){
			if(npc.getNpc().getNpctype() == NpcType.monster.getType()){
				return true;
			}
		}
		return false;
	}
	
	private boolean hasBoss(Collection<NpcInstance> npcList, String npcId){
		if(Util.isEmpty(npcList)){
			return false;
		}
		for(NpcInstance npc : npcList){
			if(npc.getNpcid().equals(npcId)){
				return true;
			}
		}
		return false;
	}
	
	//是否还有对立npc
	private boolean hasEnemy(RoleInstance role){
		MapInstance instance = role.getMapInstance();
		if(instance == null){
			return false;
		}
		return this.hasEnemy(instance.getNpcList());
	}
	
	private boolean hasBoss(RoleInstance role, String npcId){
		MapInstance instance = role.getMapInstance();
		if(instance == null){
			return false;
		}
		return this.hasBoss(instance.getNpcList(), npcId);
	}
	
	//返回角色所处的副本id
	private int getInnerCopyId(RoleInstance role){
		CopyMapConfig config = this.getMapConfig(role.getMapId());
		if(config == null){
			return -1;
		}
		return config.getCopyId();
	}
	
	@Override
	public void stop() {
		
	}

	@Override
	public C0256_CopyPanelRespMessage getCopyPanelRespMessage(RoleInstance role, short selectCopyId){
		C0256_CopyPanelRespMessage resp = new C0256_CopyPanelRespMessage();
		ApplyInfo applyInfo = this.getApplyInfo(role);
		List<CopyPanelItem> items = this.getCopyPanelItems(role, selectCopyId, applyInfo);
		this.sortCopyPanelItems(items);
		resp.setItems(items);
		if(null != applyInfo){
			resp.setApplyState((byte)1);
			resp.setApplyCopyId(applyInfo.getCopyId());
			resp.setWaitTime((int)((System.currentTimeMillis()-applyInfo.getApplyTime())/1000));
		}
		resp.setInnerCopyId((short)this.getInnerCopyId(role));
		return resp;
	}
	
	/**
	 * 副本面板中的排序
	 * 可进入 > 已完成 > 未开启
	 * 等级由小到大
	 * @param items
	 */
	private void sortCopyPanelItems(List<CopyPanelItem> items){
		Collections.sort(items, new Comparator<CopyPanelItem>(){
			@Override
			public int compare(CopyPanelItem item1, CopyPanelItem item2) {
				if(item1.getStatus() > item2.getStatus()){
					return -1;
				}
				if(item1.getStatus() < item2.getStatus()){
					return 1;
				}
				if(item1.getMinLevel() < item2.getMinLevel()){
					return -1;
				}
				if(item1.getMinLevel() > item2.getMinLevel()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	private ApplyInfo getApplyInfo(RoleInstance role/*, int copyType*/){
		/*if(copyType != CopyType.team.getType()){
			return null;
		}*/
		return GameContext.getCopyTeamApp().getApplyInfo(role);
	}
	
	private List<CopyPanelItem> getCopyPanelItems(RoleInstance role, int selectCopyId, ApplyInfo applyInfo){
		List<CopyPanelItem> items = new ArrayList<CopyPanelItem>();
		boolean isSelected = false;
		int roleInCopyId = this.getInnerCopyId(role);
		for(CopyConfig copy : this.copyConfigMap.values()){
			if(null == copy){
				continue;
			}
			//不能在每日副本中显示
			if(!copy.canShow(role)){
				continue;
			}
			short copyId = copy.getCopyId();
			CopyPanelItem item = new CopyPanelItem();
			item.setCopyId(copyId);
			item.setCopyType(copy.getType());
			item.setCopyName(copy.getCopyName());
			item.setMinLevel((byte) copy.getMinLevel());
			item.setMaxLevel((byte) copy.getMaxLevel());
			int currCount = this.getCopyCurrCount(role, copyId);//当前次数
			int maxCount = this.getCopyMaxCount(copyId);//最大次数
			CopyPanelStatus status = CopyPanelStatus.Not_Open;
			if(maxCount > 0 && currCount >= maxCount){
				status = CopyPanelStatus.Finished;
			} else if(copy.showCondition(role).isSuccess()){
				status = CopyPanelStatus.Can_Enter;
			}
			item.setStatus(status.getType());
			items.add(item);
			if(isSelected){
				continue ;
			}
			if(copyId == selectCopyId){
				item.setSelected((byte)1);
				isSelected = true;
				continue ;
			}
			//指定选中某个副本，不需要自动选择其他的
			if(selectCopyId > 0){
				continue;
			}
			//优先选中当前人物所在副本中的副本
			if(roleInCopyId > 0){
				if(copyId == roleInCopyId){
					item.setSelected((byte)1);
					isSelected = true;
				}
				continue ;
			}
			//选中自动组队的副本
			if(applyInfo != null){
				if(applyInfo.getCopyId() == copyId){
					item.setSelected((byte)1);
					isSelected = true;
				}
				continue ;
			}
			Result result = copy.enterCondition(role);
			if(!result.isSuccess()){
				continue;
			}
			//选中可进的副本
			if(CopyPanelStatus.Can_Enter == status){
				item.setSelected((byte)1);
				isSelected = true;
				continue;
			}
		}
		//没有选中的，默认选中第一个副本
		if(!isSelected){
			CopyPanelItem item = items.get(0);
			item.setSelected((byte)1);
		}
		return items;
	}

	@Override
	public CopyMapRoleRule getCopyMapRoleRule(RoleInstance role, String mapId) {
		if(null == role || Util.isEmpty(mapId)){
			return null;
		}
		List<CopyMapRoleRule> list = this.copyMapRoleRuleMap.get(mapId);
		if(!Util.isEmpty(list)){
			for(CopyMapRoleRule item : list){
				if(null == item){
					continue;
				}
				if(item.isSuitLevel(role)){
					return item;
				}
			}
		}
		return null;
	}

	public BaseDAO getBaseDAO() {
		return baseDAO;
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}

	@Override
	public int getCopyCurrCount(RoleInstance role, short copyId) {
		CopyCount count = role.getCopyCountMap().get(copyId);
		if(null == count){
			return 0;
		}
		int value = count.getCurrCount();
		//容错：如果当前完成次数大于最大次数，则返回最大次数
		int maxCount = this.getCopyMaxCount(copyId);
		if(value > maxCount){
			value = maxCount;
		}
		return value;
	}

	@Override
	public int getCopyMaxCount(short copyId) {
		CopyConfig copy = this.copyConfigMap.get(copyId);
		if(null == copy){
			return 0;
		}
		return copy.getCount();
	}

	@Override
	public void disposeCopyLostReLogin(RoleInstance role) {
		try {
			//掉线的副本标记信息
			String lostReLoginInfo = role.getCopyLostReLoginInfo();
			if(Util.isEmpty(lostReLoginInfo)){
				return;
			}
			String[] infos = lostReLoginInfo.split(Cat.comma);
			if(2 != infos.length){
				return;
			}
			//判断是否在副本掉线保护时间之内
			if(!this.isCopySafety(role)){
				return;
			}
			String containerId = infos[0];//地图容器ID
			String mapInstanceId = infos[1];//地图实例ID
			MapMultiCopyContainer container = (MapMultiCopyContainer)GameContext.getMapApp().getCopyContainer(containerId);
			MapInstance mapInstance = GameContext.getMapApp().getMapInstance(mapInstanceId);
			//容器必须存在，并且切换占有状态成功，才可以进入
			if(null == mapInstance || null == container || !container.change_containerState_to_own()){
				return;
			}
			//组队副本判断所有者是否是自己的队伍
			if(CopyType.team == container.getCopyType()){
				Team team = role.getTeam();
				if(null == team){
					return ;
				}
				//!!!! 下面条件得加上否则会出现队伍内全部玩家下线，再上线可以无限刷组队副本的bug
				if(container.getRoleCount() <=0){
					//全部成员都已经下线
					return ;
				}
				//当前队伍已经没有进度
				String existContainerId = team.getCopyContainerId(mapInstance.getMap().getMapConfig().getCopyId());
				if(Util.isEmpty(existContainerId) || !existContainerId.equals(containerId)){
					return ;
				}
				if(!team.getTeamId().equals(container.getOwnerId())){
					return;
				}
			}else if(CopyType.personal == container.getCopyType()){
				role.setCopyContainerId(containerId);
			}
			//设置进副本之前的点为下线的地点
			Point point = new Point(role.getMapId(), role.getMapX(), role.getMapY());
			if(!point.isDefaultMap()){
				point = this.failurePoint;
			}
			role.setCopyBeforePoint(point.getMapid(), point.getX(), point.getY());
			//设置角色的当前位置为该副本地图的卡死复位点
			role.setMapId(mapInstance.getMap().getMapId());
			role.setMapX(mapInstance.getMap().getMapConfig().getMaporiginx());
			role.setMapY(mapInstance.getMap().getMapConfig().getMaporiginy());
		} catch (Exception e) {
			this.logger.error("CopyLogicApp.disposeCopyLost error: ", e);
		} finally{
			//必须删除副本掉线的标记信息
			role.setCopyLostReLoginInfo(null);
		}
	}
	
	/**
	 * 是否在副本保护时间内
	 * @param role
	 * @return
	 */
	private boolean isCopySafety(RoleInstance role){
		Date lastOffTime = role.getLastOffTime();
		if(null == lastOffTime){
			return false;
		}
		long safetyTime = GameContext.getParasConfig().getCopyLostReLogin();
		return System.currentTimeMillis() - lastOffTime.getTime() <= safetyTime;
	}

}
