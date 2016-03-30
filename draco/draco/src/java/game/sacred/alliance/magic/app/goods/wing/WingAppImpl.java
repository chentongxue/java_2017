package sacred.alliance.magic.app.goods.wing;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsDeriveSupport;
import sacred.alliance.magic.app.goods.behavior.result.EquipUpgradeResult;
import sacred.alliance.magic.app.goods.behavior.result.WingGrowResult;
import sacred.alliance.magic.app.goods.derive.EquipUpgradeConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.EquipslotType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.WingAttriItem;
import com.game.draco.message.item.WingGridItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C0571_GoodsWingRespMessage;
import com.game.draco.message.response.C0572_GoodsWingUpgradeRespMessage;
import com.game.draco.message.response.C0573_GoodsWingGridRespMessage;
import com.game.draco.message.response.C0582_GoodsWingNoHasRespMessage;

public class WingAppImpl implements WingApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, WingGridConfig> wingGridConfigMap = new LinkedHashMap<String, WingGridConfig>();
	private final static int DEFAULT_MAX_WING_GRID = 9;//默认的最大命格数
	private final static int DEFAULT_GROW_WING_GRID_COUNT = 1;//默认培养次数
	@Override
	public void start() {
		this.loadWingConfig();
	}

	/**
	 * 加载翅膀命格配置
	 */
	private void loadWingConfig(){
		String fileName = "";
		String sheetName = "";
		try{
			fileName = XlsSheetNameType.equip_wing_grid.getXlsName();
			sheetName = XlsSheetNameType.equip_wing_grid.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<WingGridConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, WingGridConfig.class);
			if(Util.isEmpty(list)) {
				Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName);
				Log4jManager.checkFail();
			}
			for(WingGridConfig wingConfig : list) {
				wingConfig.init();
				String key = getKey(wingConfig.getId(), wingConfig.getLevel());
				wingGridConfigMap.put(key, wingConfig);
			}
		}catch(Exception ex){ 
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}
	
	@Override
	public C0572_GoodsWingUpgradeRespMessage wingUpgrade(RoleInstance role) {
		//判断条件
		C0572_GoodsWingUpgradeRespMessage resp = new C0572_GoodsWingUpgradeRespMessage();
		EquipUpgradeResult equipUpgradeResult = upgradeCondition(role);
		if(!equipUpgradeResult.isSuccess()){
			resp.setInfo(equipUpgradeResult.getInfo());
			return resp;
		}
		try{
			//翅膀升级
			return doUpgrade(role, equipUpgradeResult);
		}catch(Exception e){
			logger.error("wingUpgrade error:",e);
			resp.setType(RespTypeStatus.FAILURE);
			resp.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_ERROR));
		}
		return resp;
	}
	
	@Override
	public Result growWingGrid(RoleInstance role, byte wingGridId) {
		//判断是否满足培养条件
		WingGrowResult wingGrowResult = growCondition(role, wingGridId);
		if(!wingGrowResult.isSuccess()){
			return wingGrowResult;
		}
		Result result = new Result();
		try{
			//培养
			result = doGrowWingGrid(role, wingGrowResult, DEFAULT_GROW_WING_GRID_COUNT);
			if(!result.isSuccess()) {
				return result;
			}
		}catch(Exception e){
			logger.error("wingUpgrade error:",e);
			result.failure();
			result.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_ERROR));
		}
		return result;
	}
	
	@Override
	public Result allGrowWingGrid(RoleInstance role, byte wingGridId) {
		WingGrowResult wingGrowResult = growCondition(role, wingGridId);
		if(!wingGrowResult.isSuccess()){
			return wingGrowResult;
		}
		Result result = new Result();
		try{
			//获得次数
			int count = getGrowCount(role, wingGrowResult);
			//培养
			result = doGrowWingGrid(role, wingGrowResult, count);
			if(!result.isSuccess()) {
				return result;
			}
		}catch(Exception e){
			logger.error("wingUpgrade error:",e);
			result.failure();
			result.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_ERROR));
		}
		return result;
	}
	
	public int getGrowCount(RoleInstance role, WingGrowResult wingGrowResult){
		int count = 0;
		try{
			WingGridConfig wingGridConfig = wingGrowResult.getWingGridConfig();
			int roleGoodsCount = role.getRoleBackpack().countByGoodsId(wingGridConfig.getGoodsId());
			int roleMoney = role.getSilverMoney();
			//玩家身上的物品够培养多少次
			int growCount = roleGoodsCount/wingGridConfig.getGoodsNum();
			int onceNeedMoney = wingGridConfig.getGameMoney();
			int needMoeny = growCount * onceNeedMoney;
			if(needMoeny <= roleMoney) {
				//如果钱够，取物品够几次用的
				count = growCount;
			}else{
				//如果钱不够，去玩家的钱够几次的
				count = roleMoney/onceNeedMoney;
			}
			
			WingGrid wingGrid = wingGrowResult.getWingGrid();
			int maxExp = wingGridConfig.getMaxExp();
			int wingGridExp = wingGrid.getCurExp();
			int onceExp = wingGridConfig.getAddExp();
			int expCount = (maxExp - wingGridExp)/onceExp;
			if(count > expCount){
				count = expCount;
			}
		}catch(Exception e){
			logger.error("WingApp getGrowCount error",e);
		}
		return count;
	}
	
	private EquipUpgradeResult upgradeCondition(RoleInstance role){
		EquipUpgradeResult result = new EquipUpgradeResult();
		try{
			RoleGoods roleGoods = role.getRoleWingGoods();
			if(null == roleGoods) {
				result.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_NULL));
				return result;
			}
			GoodsEquipment ge = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, roleGoods.getGoodsId());
			if(null == ge){
				result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NOT_EQUIPMENT));
				return result;
			}
			GoodsEquipment target = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, ge.getUpgradeId());
			if(null == target){
				result.setInfo(GameContext.getI18n().getText(TextId.GOODS_CANOT_UPGRADE));
				return result;
			}
			EquipUpgradeConfig config = GameContext.getGoodsApp().getEquipUpgradeConfig(ge.getLevel(), ge.getQualityType(), ge.getEquipslotType());
			if(null == config){
				result.setInfo(GameContext.getI18n().getText(TextId.GOODS_CANOT_UPGRADE));
				return result;
			}
			if(StorageType.equip.getType() == roleGoods.getStorageType()){
				//如果是装备着的物品需要判断目标物品是否可使用
				if(role.getLevel() < target.getLvLimit() ){
					result.setInfo(GameContext.getI18n().messageFormat(TextId.GOODS_ON_CANOT_UPGRADE_BY_TARGET, 
							String.valueOf(target.getLvLimit())));
					return result;
				}
			}
			if(role.getSilverMoney() < config.getGameMoney()){
				//提示用户游戏币不足
				result.setInfo(GameContext.getI18n().getText(TextId.ROLE_NO_MONEY));
				return result ;
			}
			//判断材料是否足够
			Map<Integer,Integer> materialMap = config.getMaterialMap();
			for(int goodsId : materialMap.keySet()){
				int num = materialMap.get(goodsId);
				int roleNum = role.getRoleBackpack().countByGoodsId(goodsId);
				if(roleNum < num){
					//材料数目不够
					result.setInfo(Status.GOODS_NO_ENOUGH.getTips());
					return result ;
				}
			}
			result.setEquipGoods(roleGoods);
			result.setEquipTemplate(ge);
			result.setEquipUpgradeConfig(config);
			result.setTargetEquipment(target);
			result.success();
		}catch(Exception e){
			logger.error("WingApp.upgradeCondition", e);
		}
		return result;
	}
	
	/**
	 * 翅膀升级
	 * @param role
	 * @param result
	 * @return
	 */
	private C0572_GoodsWingUpgradeRespMessage doUpgrade(RoleInstance role,EquipUpgradeResult result){
		C0572_GoodsWingUpgradeRespMessage resp = new C0572_GoodsWingUpgradeRespMessage();
		try{
			result.failure();
			RoleGoods equipGoods = result.getEquipGoods();
			GoodsEquipment targetTemplate = result.getTargetEquipment() ;
			//需要删除的材料列表
			EquipUpgradeConfig config = result.getEquipUpgradeConfig();
			//删除物品操作
			Result delRs = GameContext.getUserGoodsApp().deleteForBagByMap(role, config.getMaterialMap(), OutputConsumeType.gem_upgrade);
			if(!delRs.isSuccess()){
				resp.setInfo(delRs.getInfo());
				return resp;
			}
			//扣除钱
			if(config.getGameMoney() > 0) {
				GameContext.getUserAttributeApp().changeRoleMoney(role,
						AttributeType.silverMoney, OperatorType.Decrease, config.getGameMoney(), OutputConsumeType.gem_upgrade);
				role.getBehavior().notifyAttribute();
			}
			//如果装备穿着的需要重新计算属性
			boolean on = (StorageType.equip.getType() == equipGoods.getStorageType());
			AttriBuffer oldBuffer = null ;
			if(on){
				oldBuffer = RoleGoodsHelper.getAttriBuffer(equipGoods);
			}
			//修改绑定状态
			equipGoods.setBind(BindingType.already_binding.getType());
			
			boolean openNewGrid = false;
			//升级有可能宝石孔会发生变化
			if(targetTemplate.getOpenHoleNum() 
					> result.getEquipTemplate().getOpenHoleNum()){
				this.incrWingGrid(equipGoods, targetTemplate.getOpenHoleNum());
				//开启新的命格
				WingGridConfig wingGridConfig = this.getWingGrid((byte)targetTemplate.getOpenHoleNum(), 1);
				String wingGridName = wingGridConfig.getName();
				resp.setOpenWingGridId(wingGridConfig.getId());
				resp.setInfo(GameContext.getI18n().messageFormat(TextId.WING_GOODS_GRID_UPGRADE_SUCCESS_AND_OPEN, wingGridName));
				openNewGrid = true;
			}
			//修改模板ID
			equipGoods.setGoodsId(targetTemplate.getId());
			
			try {
				if (on) {
					// 重新计算属性并且通知客户端
					AttriBuffer newBuffer = RoleGoodsHelper.getAttriBuffer(equipGoods);
					newBuffer.append(oldBuffer.reverse());
					GameContext.getUserAttributeApp().changeAttribute(role, newBuffer);
					role.getBehavior().notifyAttribute();
				}
				// 将最新的装备信息push给客户端
				GoodsDeriveSupport.notifyGoodsInfo(role, equipGoods, equipGoods.getStorageType());
			}catch(Exception ex){
				logger.error("WingApp.doUpgrade error:",ex);
			}
			role.setRoleWingGoods(equipGoods);
			//物品入库
			equipGoods.offlineSaveDb();
			result.success();
			resp.setType(result.getResult());
			if(!openNewGrid && result.isSuccess()) {
				resp.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_GRID_UPGRADE_SUCCESS));
			}
			//世界广播
			this.broadcast(role, result.getEquipTemplate(), targetTemplate, result.getEquipUpgradeConfig().getBroadcast());
		}catch(Exception e){
			logger.error("WingApp.doUpgrade error:", e);
			resp.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_ERROR));
		}
		return resp;
	}
	
	private void incrWingGrid(RoleGoods roleGoods,int totalGridNum){
		WingGrid[] nowGrids = new WingGrid[totalGridNum];
		WingGrid[] grids = roleGoods.getWingGrids();
		for(int i=0;i<grids.length;i++){
			nowGrids[i] = grids[i] ;
		}
		for(int i = grids.length; i < totalGridNum; i++) {
			String key = getKey((byte)(i + 1), 1);
			WingGridConfig config = this.wingGridConfigMap.get(key);
			WingGrid wingGrid = new WingGrid(config.getId(), 0, 1);
			nowGrids[i] = wingGrid;
		}
		roleGoods.setWingGrids(nowGrids);
	}
	
	
	@Override
	public RoleGoods getWingGoods(RoleInstance role) {
		RoleGoods roleGoods = role.getRoleWingGoods();
		if(null != roleGoods) {
			return roleGoods;
		}
		roleGoods = role.getEquipBackpack().getEquipGoods(EquipslotType.wing.getType());
		if(null != roleGoods) {
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
			if(null != gb && gb.getDeadline() <= 0) {
				role.setRoleWingGoods(roleGoods);
				return roleGoods;
			}
		}
		roleGoods = getBackpackWing(role);
		if(null != roleGoods) {
			role.setRoleWingGoods(roleGoods);
			return roleGoods;
		}
		return null;
	}
	
	@Override
	public void loadRoleWing(RoleInstance role) {
		RoleGoods roleGoods = role.getRoleWingGoods();
		if(null != roleGoods) {
			return;
		}
		roleGoods = getWingGoods(role);
		if(null == roleGoods) {
			return;
		}
		
		//是否需要初始化属性
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if(null == gb){
			return;
		}
		int templateHoles = gb.getOpenHoleNum();
		int hasWingGrid = 0;
		if(!Util.isEmpty(roleGoods.getMosaic())){
			String[] arr = roleGoods.getMosaic().split(Cat.comma);
			hasWingGrid = arr.length;
		}
		
		if(templateHoles <= hasWingGrid) {
			return;
		}
		
		WingGrid[] wingGrids = roleGoods.getWingGrids();
		for(int i = hasWingGrid; i < templateHoles; i++) {
			int windGridId = i+1;
			WingGridConfig config = this.getWingGrid((byte)windGridId, 1);
			if(null == config) {
				continue;
			}
			wingGrids[i] = new WingGrid(windGridId, 0, 1);
		}
		role.setRoleWingGoods(roleGoods);
	}
	
	/**
	 * 获取背包中的翅膀
	 * @param role
	 * @return
	 */
	private RoleGoods getBackpackWing(RoleInstance role) {
		RoleGoods wingGoods = null;
		try{
			List<RoleGoods> list = role.getRoleBackpack().getAllGoods();
			if(Util.isEmpty(list)) {
				return null;
			}
			GoodsEquipment equipment = null;
			for(RoleGoods roleGoods : list) {
				if(null == roleGoods) {
					continue;
				}
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
				if(null == gb) {
					continue;
				}
				if(gb.getGoodsType() != GoodsType.GoodsEquHuman.getType() && gb.getGoodsType() != GoodsType.GoodsEquGoddess.getType()) {
					continue;
				}
				GoodsEquipment goodsEquipment = (GoodsEquipment)gb;
				if(goodsEquipment.getEquipslotType() != EquipslotType.wing.getType()) {
					continue;
				}
				if(goodsEquipment.getDeadline() != 0) {
					continue;
				}
				if(null == equipment) {
					equipment = goodsEquipment;
					wingGoods = roleGoods;
					continue;
				}
				if(equipment.getLevel() < goodsEquipment.getLevel()) {
					equipment = goodsEquipment;
					wingGoods = roleGoods;
				}
			}
		}catch(Exception e){
			logger.error("WingApp.getBackpackWing error:",e);
		}
		return wingGoods;
	}
	
	private WingGrowResult growCondition(RoleInstance role, byte wingGridId) {
		WingGrowResult result = new WingGrowResult();
		try{
			RoleGoods roleGoods = role.getRoleWingGoods();
			if(null == roleGoods) {
				result.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_NULL));
				return result;
			}
			if(wingGridId <=0) {
				result.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_GRID_ERROR));
				return result;
			}
			
			GoodsEquipment ge = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, roleGoods.getGoodsId());
			if(null == ge){
				result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NOT_EQUIPMENT));
				return result;
			}
			
			Result openResult = gridIsOpen(roleGoods, wingGridId);
			if(!openResult.isSuccess()) {
				result.setInfo(openResult.getInfo());
				return result;
			}
			
			WingGrid wingGrid = roleGoods.getWingGrids()[wingGridId-1];
			if(null == wingGrid){
				result.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_GRID_NOT_OPNE));
				return result;
			}
			
			String key = getKey(wingGridId, wingGrid.getLevel() + 1);
			WingGridConfig nextWingGridConfig = wingGridConfigMap.get(key);
			if(null == nextWingGridConfig) {
				result.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_GRID_MAX));
				return result;
			}
			
			key = getKey(wingGridId, wingGrid.getLevel());
			WingGridConfig wingGridConfig = wingGridConfigMap.get(key);
			if(null == wingGridConfig) {
				result.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_ERROR));
				return result;
			}
			
			if(role.getSilverMoney() < wingGridConfig.getGameMoney()){
				//提示用户游戏币不足
				result.setInfo(GameContext.getI18n().getText(TextId.ROLE_NO_MONEY));
				return result ;
			}
			int num = wingGridConfig.getGoodsNum();
			int roleNum = role.getRoleBackpack().countByGoodsId(wingGridConfig.getGoodsId());
			if(roleNum < num){
				//材料数目不够
				result.setInfo(wingGridConfig.getInfo());
				return result;
			}
			result.setRoleGoods(roleGoods);
			result.setWingGridConfig(wingGridConfig);
			result.setWingGrid(wingGrid);
			result.success();
			return result;
		}catch (Exception e) {
			logger.error("WingApp.growCondition error:",e);
		}
		return result;
	}
	
	/**
	 * 培养命格
	 * @param role
	 * @param result
	 * @return
	 */
	private Result doGrowWingGrid(RoleInstance role, WingGrowResult result, int count){
		try{
			result.failure();
			RoleGoods roleGoods = result.getRoleGoods();
			WingGridConfig wingGridConfig = result.getWingGridConfig();
			WingGrid wingGrid = result.getWingGrid();
			
			int goodsNum = wingGridConfig.getGoodsNum() * count;
			Result delRs = GameContext.getUserGoodsApp().deleteForBag(role, wingGridConfig.getGoodsId(), goodsNum, OutputConsumeType.wing_grow_goods);
			if(!delRs.isSuccess()){
				result.setInfo(delRs.getInfo());
				return result;
			}
			
			boolean needNotify = false;
			//扣除钱
			int money = wingGridConfig.getGameMoney() * count;
			if(wingGridConfig.getGameMoney() > 0) {
				GameContext.getUserAttributeApp().changeRoleMoney(role,
						AttributeType.silverMoney, OperatorType.Decrease, money, OutputConsumeType.wing_grow_silver);
				needNotify = true;
			}
			
			//如果装备穿着的需要重新计算属性
			boolean on = (StorageType.equip.getType() == roleGoods.getStorageType());
			AttriBuffer oldBuffer = null ;
			
			String key = getKey(wingGridConfig.getId(), wingGridConfig.getLevel() + 1);
			WingGridConfig nextWingGridConfig = wingGridConfigMap.get(key);
			int exp = (wingGridConfig.getAddExp() * count) + wingGrid.getCurExp();
			boolean isUp = false;
			if(wingGridConfig.isUpLevel(exp)) {
				//升级了 如果装备穿着的 先计算一下升级前的属性
				if(on){
					oldBuffer = RoleGoodsHelper.getAttriBuffer(roleGoods);
				}
				if(null != nextWingGridConfig) {
					exp -= wingGridConfig.getMaxExp();
					wingGrid.setLevel(nextWingGridConfig.getLevel());
					isUp = true;
				}else{
					exp = wingGridConfig.getMaxExp();
				}
			}
			wingGrid.setCurExp(exp);
			roleGoods.getWingGrids()[wingGridConfig.getId() - 1] = wingGrid;
			result.setInfo(GameContext.getI18n().messageFormat(TextId.WING_GOODS_GRID_GROW_SUCCESS, exp));
			
			if(!isUp) {
				if(needNotify) {
					role.getBehavior().notifyAttribute();
				}
				return result.success();
			}
			
			if (on) {
				// 重新计算属性并且通知客户端
				AttriBuffer newBuffer = RoleGoodsHelper.getAttriBuffer(roleGoods);
				newBuffer.append(oldBuffer.reverse());
				GameContext.getUserAttributeApp().changeAttribute(role, newBuffer);
				needNotify = true;
			}
			// 将最新的装备信息push给客户端
			GoodsDeriveSupport.notifyGoodsInfo(role, roleGoods, roleGoods.getStorageType());
			//通知客户端属性变化
			if(needNotify) {
				role.getBehavior().notifyAttribute();
			}
			//世界广播
			this.broadcastGrid(role, nextWingGridConfig);
			//升级后直接入库
			roleGoods.offlineSaveDb();
			result.success();
		}catch(Exception e){
			logger.error("WingApp.doGrowWingGrid error:",e);
		}
		return result;
	}
	
	private Map<Byte, Integer> getWingGridTotalAttr(RoleInstance role) {
		Map<Byte, Integer> map = new HashMap<Byte, Integer>();
		RoleGoods roleGoods = role.getRoleWingGoods();
		if(null == roleGoods) {
			return null;
		}
		WingGrid[] wingGrids = roleGoods.getWingGrids();
		for(WingGrid wingGrid : wingGrids) {
			if(null == wingGrid) {
				continue;
			}
			String key = getKey((byte)wingGrid.getWingGridId(), wingGrid.getLevel());
			WingGridConfig config = this.wingGridConfigMap.get(key);
			Map<Byte, Integer> configArrtMap = config.getAttrMap();
			if(Util.isEmpty(configArrtMap)) {
				continue;
			}
			for(byte attr : configArrtMap.keySet()) {
				int value = configArrtMap.get(attr);
				Integer orgValue = map.get(attr);
				if(null != orgValue) {
					value += orgValue;
				}
				map.put(attr, value);
			}
		}
		return map;
	}
	
	@Override
	public List<AttriItem> getWingGridAttri(byte wingGirdId, int level) {
		String key = getKey(wingGirdId, level);
		WingGridConfig config = this.wingGridConfigMap.get(key);
		if(null == config) {
			return null;
		}
		return config.getAttrList();
	}
	
	@Override
	public WingGridConfig getWingGrid(byte wingGirdId, int level) {
		String key = getKey(wingGirdId, level);
		WingGridConfig config = this.wingGridConfigMap.get(key);
		if(null == config) {
			return null;
		}
		return config;
	}
	
	private String getKey(byte id, int level){
		return id + Cat.underline + level;
	}
	
	@Override
	public void stop() {
	}
	
	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public C0571_GoodsWingRespMessage getGoodsWingInfo(RoleInstance role) {
		C0571_GoodsWingRespMessage resp = new C0571_GoodsWingRespMessage();
		RoleGoods roleGoods = role.getRoleWingGoods();
		if(null == roleGoods){//无翅膀时提示
			roleGoods = getWingGoods(role);
			if(null == roleGoods) {
				C0582_GoodsWingNoHasRespMessage message = new C0582_GoodsWingNoHasRespMessage();
				message.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_NULL));
				role.getBehavior().sendMessage(message);
				return null;
			}
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if(null == gb) {
			return resp;
		}
		GoodsEquipment ge = (GoodsEquipment)gb;
		resp.setName(gb.getName());
		resp.setLevel((byte)ge.getLevel());
		
		GoodsEquipment target = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, ge.getUpgradeId());
		if(null != target && target.getOpenHoleNum() > ge.getOpenHoleNum()){
			resp.setCanOpen((byte)1);
		} 
		//装备属性
		List<AttriItem> equipAttrList = ge.getAttriItemList();
		List<WingAttriItem> wingAttriList = new ArrayList<WingAttriItem>();
		if(!Util.isEmpty(equipAttrList)) {
			for(AttriItem item : equipAttrList) {
				WingAttriItem wingItem = new WingAttriItem();
				byte attriType = item.getAttriTypeValue();
				wingItem.setType(attriType);
				wingItem.setValue(AttributeType.formatValue(attriType, item.getValue()));
				wingAttriList.add(wingItem);
			}
		}
		resp.setCurAttrItems(wingAttriList);
		//翅膀总属性
		Map<Byte, Integer> totalAttrMap = getWingGridTotalAttr(role);
		List<WingAttriItem> totalAttrList = new ArrayList<WingAttriItem>();
		if(!Util.isEmpty(totalAttrMap)) {
			for(Byte type : totalAttrMap.keySet()) {
				int value = totalAttrMap.get(type);
				WingAttriItem wingItem = new WingAttriItem();
				wingItem.setType(type);
				wingItem.setValue(AttributeType.formatValue(type, value));
				totalAttrList.add(wingItem);
			}
		}
		resp.setWingGridAttrItems(totalAttrList);
		//翅膀命格信息
		List<WingGridItem> wingGirdItems = new ArrayList<WingGridItem>();
		WingGrid[] wingGrids = roleGoods.getWingGrids();
		for(int i=0;i<DEFAULT_MAX_WING_GRID;i++) {
			WingGridItem wingGridItem = new WingGridItem();
			if(i<wingGrids.length) {
				WingGrid wingGrid = wingGrids[i];
				if(null == wingGrid) {
					continue;
				}
				WingGridConfig config = this.getWingGrid((byte)(i + 1), wingGrid.getLevel());
				if(null == config){
					continue;
				}
				wingGridItem.setId((byte)wingGrid.getWingGridId());
				wingGridItem.setLevel((byte)wingGrid.getLevel());
				wingGridItem.setName(config.getName());
				wingGridItem.setQualityType(config.getQualityType());
				wingGirdItems.add(wingGridItem);
				continue;
			}
			
			byte wingGridId = (byte)(i + 1);
			WingGridConfig config = this.getWingGrid(wingGridId, 1);
			if(null == config){
				continue;
			}
			wingGridItem.setId(wingGridId);
			wingGridItem.setLevel((byte)0);
			wingGridItem.setName(config.getName());
			wingGridItem.setQualityType(config.getQualityType());
			wingGirdItems.add(wingGridItem);
		}
		resp.setWingGirdItems(wingGirdItems);
		
		//升级配置
		EquipUpgradeConfig config = GameContext.getGoodsApp().getEquipUpgradeConfig(ge.getLevel(),
				ge.getQualityType(), ge.getEquipslotType());
		if(null == config) {
			return resp;
		}
		//升级需要的钱
		resp.setMoney(config.getGameMoney());
		//升级需要的物品
		Map<Integer, Integer> materialMap = config.getMaterialMap();
		if(Util.isEmpty(materialMap)) {
			return resp;
		}
		List<GoodsLiteNamedItem> goodsItemList = new ArrayList<GoodsLiteNamedItem>();
		for(int goodsId : materialMap.keySet()) {
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == goodsBase) {
				continue;
			}
			int num = materialMap.get(goodsId);
			if(num <= 0) {
				continue;
			}
			GoodsLiteNamedItem goodsItem = new GoodsLiteNamedItem();
			goodsItem.setGoodsId(goodsId);
			goodsItem.setNum((short) num);
			goodsItem.setBindType(goodsBase.getBindType());
			goodsItem.setGoodsImageId(goodsBase.getImageId());
			goodsItem.setQualityType(goodsBase.getQualityType());
			goodsItem.setGoodsName(goodsBase.getName());
			goodsItemList.add(goodsItem);
		}
		resp.setGoodsItemList(goodsItemList);
		return resp;
	}
	
	@Override
	public C0573_GoodsWingGridRespMessage getGoodsWingGridInfo(RoleInstance role, byte wingGridId) {
		RoleGoods roleGoods = role.getRoleWingGoods();
		if(null == roleGoods){//无翅膀时提示
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.WING_GOODS_NULL));
			role.getBehavior().sendMessage(message);
			return null;
		}
		Result result = gridIsOpen(roleGoods, wingGridId);
		if(!result.isSuccess()) {
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage(result.getInfo());
			role.getBehavior().sendMessage(message);
			return null;
		}
		WingGrid wingGrid = roleGoods.getWingGrids()[wingGridId - 1];
		if(null == wingGrid) {
			return null;
		}
		C0573_GoodsWingGridRespMessage resp = new C0573_GoodsWingGridRespMessage();
		resp.setCurExp(wingGrid.getCurExp());
		resp.setCurLevel((byte)wingGrid.getLevel());
		//当前等级属性
		WingGridConfig curConfig = this.getWingGrid(wingGridId, wingGrid.getLevel());
		if(null == curConfig) {
			return null;
		}
		resp.setCurWingGridId(curConfig.getId());
		resp.setMoney(curConfig.getGameMoney());
		resp.setMaxExp(curConfig.getMaxExp());
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(curConfig.getGoodsId());
		if(null == goodsBase) {
			return null;
		}
		GoodsLiteNamedItem goodsItem = new GoodsLiteNamedItem();
		goodsItem.setGoodsId(curConfig.getGoodsId());
		goodsItem.setNum((short)curConfig.getGoodsNum());
		goodsItem.setBindType(goodsBase.getBindType());
		goodsItem.setGoodsImageId(goodsBase.getImageId());
		goodsItem.setQualityType(goodsBase.getQualityType());
		goodsItem.setGoodsName(goodsBase.getName());
		resp.setGoodsItem(goodsItem);
		List<AttriItem> curAttrList = curConfig.getAttrList();
		if(Util.isEmpty(curAttrList)) {
			return null;
		}
		List<WingAttriItem> curAttrItems = new ArrayList<WingAttriItem>();
		for(AttriItem item : curAttrList) {
			WingAttriItem wingAttriItem = new WingAttriItem();
			byte attriType = item.getAttriTypeValue();
			wingAttriItem.setType(attriType);
			wingAttriItem.setValue(AttributeType.formatValue(attriType, item.getValue()));
			curAttrItems.add(wingAttriItem);
		}
		resp.setCurAttrItems(curAttrItems);
		
		//下一级属性
		WingGridConfig nextConfig = this.getWingGrid((byte)wingGridId, wingGrid.getLevel() + 1);
		if(null == nextConfig) {
			return resp;
		}
		List<AttriItem> nextAttrList = nextConfig.getAttrList();
		if(Util.isEmpty(nextAttrList)) {
			return resp;
		}
		List<WingAttriItem> nextAttrItems = new ArrayList<WingAttriItem>();
		for(AttriItem item : nextAttrList) {
			WingAttriItem wingAttriItem = new WingAttriItem();
			byte attriType = item.getAttriTypeValue();
			wingAttriItem.setType(attriType);
			wingAttriItem.setValue(AttributeType.formatValue(attriType, item.getValue()));
			nextAttrItems.add(wingAttriItem);
		}
		resp.setNextAttrItems(nextAttrItems);
		return resp;
	}
	
	@Override
	public void initRoleGoodsWingGird(RoleGoods roleGoods, int templateHoles) {
		try{
			StringBuffer wingBuffer = new StringBuffer();
			String cat = "";
			int index = -1 ;
			for (int i = 0; i < templateHoles; i++) {
				int windGridId = i+1;
				WingGridConfig config = this.getWingGrid((byte)windGridId, 1);
				if(null == config) {
					continue;
				}
				index++;
				wingBuffer.append(cat);
				wingBuffer.append(index);
				wingBuffer.append(Cat.colon);
				wingBuffer.append(config.getId());
				wingBuffer.append(Cat.colon);
				wingBuffer.append(0);
				wingBuffer.append(Cat.colon);
				wingBuffer.append(1);
				cat = Cat.comma;
			}
			roleGoods.setMosaic(wingBuffer.toString());
		}catch(Exception e){
			logger.error("WingApp.initRoleGoodsWingGird error:",e);
		}
	}
	
	/**
	 * 命格是否开启
	 * @param roleGoods
	 * @return
	 */
	private Result gridIsOpen(RoleGoods roleGoods, int wingGridId){
		Result result = new Result();
		GoodsEquipment ge = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, roleGoods.getGoodsId());
		if(null == ge){
			return result.setInfo(GameContext.getI18n().getText(TextId.WING_GOODS_ERROR));
		}
		if(wingGridId > ge.getOpenHoleNum()) {
			String key = getKey((byte)wingGridId, 1);
			WingGridConfig wingGridConfig = wingGridConfigMap.get(key);
			return result.setInfo(wingGridConfig.getOpenInfo());
		}
		return result.success();
	}
	
	private void broadcast(RoleInstance role, GoodsEquipment equipTemplate, GoodsEquipment targetEquipment, String broadcastMsg){
		try{
			if(Util.isEmpty(broadcastMsg)) {
				return;
			}
			if(null == equipTemplate || null == targetEquipment) {
				return;
			}
			String equipGoodsContent = Wildcard.getChatGoodsContent(equipTemplate.getId(), ChannelType.Publicize_Personal);
			String targetGoodsContent = Wildcard.getChatGoodsContent(targetEquipment.getId(), ChannelType.Publicize_Personal);
			String message = MessageFormat.format(broadcastMsg,role.getRoleName(),equipGoodsContent,targetGoodsContent);							
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		}catch(Exception e){
			logger.error("EquipUpgrade.broadcast",e);
		}
	}
	
	private void broadcastGrid(RoleInstance role, WingGridConfig wingGridConfig){
		try{
			if(null == wingGridConfig) {
				return;
			}
			String broadcastMsg = wingGridConfig.getBroadcast();
			if(Util.isEmpty(broadcastMsg)) {
				return;
			}
			String wingGridName = Wildcard.getWingGridName(wingGridConfig.getName(), QualityType.get(wingGridConfig.getQualityType()), ChannelType.Publicize_Personal);
			String message = MessageFormat.format(broadcastMsg,role.getRoleName(),wingGridName,wingGridConfig.getLevel());							
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		}catch(Exception e){
			logger.error("WingApp.broadcast",e);
		}
	}
}
