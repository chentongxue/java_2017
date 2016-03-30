package sacred.alliance.magic.app.map.worldmap;

import java.util.HashMap;
import java.util.List;

import sacred.alliance.magic.app.config.ParasConfig;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0253_MapTransferConfirmReqMessage;

public class WorldMapAppImpl implements WorldMapApp {

	private List<WorldMapInfo> worldMapInfo = null ;
	private java.util.Map<String,WorldMapInfo> worldMapInfoMap = new HashMap<String,WorldMapInfo>();
	private static final short MAP_TRANSFER_CONFIRM_CMD = new C0253_MapTransferConfirmReqMessage().getCommandId();
	//传送参数长度
	private static final int TRANGE_PARAMS_LENGTH = 5 ;
	private ParasConfig parasConfig ;
	
	@Override
	public List<WorldMapInfo> getAllWorldMapInfo(){
		return this.worldMapInfo ;
	}
	
	@Override
	public Result transfer(RoleInstance role, Point point) {
		String condInfo = this.cond(role, point);
		if (!Util.isEmpty(condInfo)) {
			Result result = new Result();
			result.setInfo(condInfo);
			return result;
		}
		/*// VIP次数是否已用完
		int vipTotalCount = GameContext.getRoleVipApp().getPrivCount(role,
				PrivType.FreeTransport);
		if (vipTotalCount < 0) {
			// vip设置无限制
			return this.unlimitVipChangeMap(role, point);
		}
		int useCount = GameContext.getRoleVipApp().getUsedCount(
				role, PrivType.FreeTransport) ;
		if (vipTotalCount > 0 && vipTotalCount > useCount) {
			//VIP传送
			return this.vipChangeMap(role, point,vipTotalCount,useCount);
		}*/
		//物品传送
		/*int goodsId = this.parasConfig.getWorldMapGoodsId() ;
		if(goodsId > 0 && role.getRoleBackpack().existGoods(goodsId)){
			//道具传送
			return this.toolsChangeMap(role, point,goodsId);
		}
		//提示扣钱二次确认
		role.getBehavior().sendMessage(this.triggerCostMessage(role, point));*/
		//返回成功
		return new Result().success();
	}
	
	@Override
	public Result transferConfirm(RoleInstance role,String confirmInfo) {
		if(Util.isEmpty(confirmInfo)){
			return new Result().setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		Point point = this.analysisParamPoint(confirmInfo);
		if(null == point){
			return new Result().setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		AttributeType moneyType = this.analysisParamType(confirmInfo);
		if(null == moneyType){
			return new Result().setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		String moneyInfo = this.checkMoney(role, moneyType);
		if(!Util.isEmpty(moneyInfo)){
			return new Result().setInfo(moneyInfo);
		}
		String condInfo = this.cond(role, point);
		if (!Util.isEmpty(condInfo)) {
			return new Result().setInfo(condInfo);
		}
		//可以传送
		return this.moneyChangMap(role, moneyType, point);
	}
	
	
	private Message triggerCostMessage(RoleInstance role,Point point) {
		String pointInfo = point.getMapid() + Cat.comma + point.getX() + Cat.comma + point.getY() + Cat.comma  + point.getEventType();
		String goldParam = AttributeType.goldMoney.getType() + Cat.comma + pointInfo;
		String bindParam = AttributeType.bindingGoldMoney.getType() + Cat.comma + pointInfo;
		return QuickCostHelper.getMessage(role, MAP_TRANSFER_CONFIRM_CMD, goldParam, MAP_TRANSFER_CONFIRM_CMD, bindParam, 
				GameContext.getI18n().getText(TextId.WORLD_MAP_FEE), parasConfig.getWorldMapGoldCost(), parasConfig.getWorldMapBindingCost());
	}
	
	/*
	private Result toolsChangeMap(RoleInstance role,Point point,int goodsId){
		Result result = new Result();
		if(!this.changeMap(role, point)){
			return result.setInfo(GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_FAIL)) ;
		}
		GameContext.getUserGoodsApp().deleteForBag(role, goodsId, 1, OutputConsumeType.world_transmit);
		GoodsBase tools = GameContext.getGoodsApp().getGoodsBase(goodsId);
		String text = GameContext.getI18n().getText(TextId.VIP_MAP_TRANSFER_DEFAULT_TOOLS) ;
		if(null != tools){
			 text = GameContext.getI18n().messageFormat(TextId.VIP_MAP_TRANSFER_TOOLS,tools.getName());
		}
		this.sendNotifyMessage(role, text);
		return result.success();
	}*/
	
	private void sendNotifyMessage(RoleInstance role,String text){
		if(Util.isEmpty(text)){
			return ;
		}
		//发送提示信息
		C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage();
		notifyMsg.setMsgContext(text);
		role.getBehavior().sendMessage(notifyMsg);
	}
	
	/*private Result vipChangeMap(RoleInstance role,Point point,int vipTotalCount,int useCount){
		Result result = new Result();
		if(!this.changeMap(role, point)){
			return result.setInfo(GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_FAIL)) ;
		}
		role.getRoleCount().incrDayFreeTransport();
		this.sendNotifyMessage(role, MessageFormat.format(
				GameContext.getI18n().getText(TextId.VIP_MAP_TRANSFER_LIMITED), 
				this.getVipName(role.getVipLevel()),
				vipTotalCount - useCount -1 
				));
		return result.success() ;
	}
*/
	/*private Result unlimitVipChangeMap(RoleInstance role,Point point){
		Result result = new Result();
		if(!this.changeMap(role, point)){
			return result.setInfo(GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_FAIL)) ;
		}
		role.getRoleCount().incrDayFreeTransport();
		this.sendNotifyMessage(role, MessageFormat.format(
				GameContext.getI18n().getText(TextId.VIP_MAP_TRANSFER_UNLIMITED), this.getVipName(role.getVipLevel())));
		return result.success() ;
	}*/
	
	
	
	private boolean changeMap(RoleInstance role,Point point){
		//传送
		ChangeMapResult result = null ;
		try {
			result = GameContext.getUserMapApp().changeMap(role, point);
		} catch (ServiceException e) {
		}
		return (null != result && result.isSuccess());
	}
	
	
	private String cond(RoleInstance role,Point point){
		if(null == point || null == point.getMapid()){
			return GameContext.getI18n().getText(TextId.ERROR_INPUT) ;
		}
		//判断级别
		MapConfig mapConfig = GameContext.getMapApp().getMapConfig(point.getMapid());
		if(null == mapConfig){
			return GameContext.getI18n().getText(TextId.WORLD_MAP_NOT_OPEN) ;
		}
		int minLevel = mapConfig.getMinTransLevel();
		int maxLevel = mapConfig.getMaxTransLevel();
		if(minLevel != 0 || maxLevel != 0 ){
			if(minLevel > maxLevel){
				//不支持
				return GameContext.getI18n().getText(TextId.WORLD_MAP_NOT_OPEN) ;
			}
			if(role.getLevel()< minLevel){
				return GameContext.getI18n().getText(TextId.WORLD_MAP_ROLE_LEVEL_TOO_LESS) ;
			}
			if(role.getLevel()> maxLevel){
				return GameContext.getI18n().getText(TextId.WORLD_MAP_ROLE_LEVEL_TOO_HIGHT) ;
			}
		}
		
		//判断当前阵营是否可传送
		WorldMapInfo info = this.worldMapInfoMap.get(point.getMapid());
		if(null != info && 1 != info.getCampValue(role.getCampId())){
			return GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_NOT_OPEN) ;
		}
		return null ;
	}
	
	private String checkMoney(RoleInstance role, AttributeType moneyType) {
		int roleGold = role.getGoldMoney();
		int roleBinding = role.getBindingGoldMoney();
		int needGold = parasConfig.getWorldMapGoldCost();
		int needBinding = parasConfig.getWorldMapBindingCost();
		// 绑金
		if (AttributeType.bindingGoldMoney == moneyType) {
			if (needBinding < 0) {
				return Status.Sys_Error.getTips();
			}
			if (needBinding > roleBinding) {
				return GameContext.getI18n().messageFormat(
						TextId.WORLD_MAP_FEE_NOT_ENOUGH, moneyType
								.getName());
			}
		}
		if (AttributeType.goldMoney == moneyType) {
			// 金条
			if (needGold < 0) {
				return Status.Sys_Error.getTips();
			}
			if (needGold > roleGold) {
				return GameContext.getI18n().messageFormat(
						TextId.WORLD_MAP_FEE_NOT_ENOUGH, moneyType
								.getName());
			}
		}
		return null ;
	}
	
	
	private Result moneyChangMap(RoleInstance role,
			AttributeType moneyType, Point point) {
		int needGold = parasConfig.getWorldMapGoldCost();
		int needBinding = parasConfig.getWorldMapBindingCost();
		int cost = 0;
		//绑金
		if(AttributeType.bindingGoldMoney == moneyType){
			cost = needBinding;
		}else {
			cost = needGold;
		}
		Result result = new Result();
		if(!this.changeMap(role, point)){
			return result.setInfo(GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_FAIL)) ;
		}
		//扣除消耗
		GameContext.getUserAttributeApp().changeRoleMoney(role, moneyType
					,OperatorType.Decrease, cost, OutputConsumeType.world_transmit);
			role.getBehavior().notifyAttribute();
		//发送提示信息
		this.sendNotifyMessage(role, GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_SUCCESS));
		return result.success();
	}
		
	@Override
	public void start() {
		loadWorldMapData();
	}
	
	private void loadWorldMapData(){
		String sourceName = XlsSheetNameType.map_world.getXlsName();
		String sheetName = XlsSheetNameType.map_world.getSheetName();
		try{
			String path = GameContext.getPathConfig().getXlsPath();
			this.worldMapInfo = XlsPojoUtil.sheetToList(path+sourceName, sheetName, WorldMapInfo.class);
			if(Util.isEmpty(worldMapInfo)){
				Log4jManager.CHECK.error("not config the world map,sourceName= " + sourceName + " sheetName=" + sheetName);
				Log4jManager.checkFail();
				return ;
			}
			for(WorldMapInfo info : this.worldMapInfo){
				worldMapInfoMap.put(info.getMapId(), info);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error(this.getClass().getName()+" loadWorldMapData error");
			Log4jManager.checkFail();
		}
	}

	@Override
	public void setArgs(Object obj) {
	}
	
	@Override
	public void stop() {
	}

	public void setParasConfig(ParasConfig parasConfig) {
		this.parasConfig = parasConfig;
	}

	private Point analysisParamPoint(String param) {
		String[] params = parseParam(param);
		if(null == params || params.length != TRANGE_PARAMS_LENGTH){
			return null;
		}
		String mapId = "";
		int mapX = 0;
		int mapY = 0;
		byte pointType = 0 ;
		try{
			mapId = params[1];
			mapX = Integer.parseInt(params[2]);
			mapY = Integer.parseInt(params[3]);
			pointType = Byte.parseByte(params[4]);
		}catch(Exception e){
			e.printStackTrace();
		}
		Map map = GameContext.getMapApp().getMap(mapId);
		if(null == map){
			return null;
		}
		return new Point(mapId, mapX, mapY,pointType);
	}

	
	private AttributeType analysisParamType(String param) {
		String[] params = parseParam(param);
		if(null == params || params.length != TRANGE_PARAMS_LENGTH){
			return null;
		}
		String type = params[0];
		if(!Util.isNumeric(type)){
			return null;
		}
		byte attrType = Byte.parseByte(type);
		if(attrType == AttributeType.bindingGoldMoney.getType()){
			return AttributeType.bindingGoldMoney;
		}else if(attrType == AttributeType.goldMoney.getType()){
			return AttributeType.goldMoney;
		}
		return null;
	}
	
	private String[] parseParam(String param){
		if(StringUtil.nullOrEmpty(param)){
			return null;
		}
		String[] result = param.split(Cat.comma);
		return result;
	}
}
