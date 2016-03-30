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

	private final static String KEY = "abc@world*9" ;
	private final static short MAP_TRANSFER_CONFIRM_CMD = new C0253_MapTransferConfirmReqMessage().getCommandId();
	private List<WorldMapInfo> worldMapInfo = null ;
	private java.util.Map<String,WorldMapInfo> worldMapInfoMap = new HashMap<String,WorldMapInfo>();
	//传送参数长度
	private static final int TRANGE_PARAMS_LENGTH = 6 ;
	private ParasConfig parasConfig ;
	
	@Override
	public List<WorldMapInfo> getAllWorldMapInfo(){
		return this.worldMapInfo ;
	}
	
	@Override
	public Result transfer(RoleInstance role, Point point,int cost) {
		String condInfo = this.cond(role, point);
		Result result = new Result();
		if (!Util.isEmpty(condInfo)) {
			result.setInfo(condInfo);
			return result;
		}
		int goodsId = this.parasConfig.getWorldMapGoodsId() ;
		boolean useGoods = (goodsId > 0 ) ;
		if(useGoods && role.getRoleBackpack().existGoods(goodsId)){
			return this.toolsChangeMap(role, point, goodsId);
		}
		if(cost <=0 ){
			//不支持钻石传输，道具不足
			return result.setInfo(GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_HAVENOT_GOODS)) ;
		}
		//判断钻石是否足够
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role,
				AttributeType.goldMoney, cost);
		if (ar.isIgnore()) {
			return ar;
		}
		if (!ar.isSuccess()) {
			result.setInfo(GameContext.getI18n().messageFormat(
					TextId.NOT_ENOUGH_ATTRIBUTE,
					AttributeType.goldMoney.getName()));
			return result;
		}
		//扣钱二次确认
		role.getBehavior().sendMessage(this.triggerCostMessage(role, point, cost));
		result.success();
		return result;
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId) ;
	}
	
	@Override
	public Result transferConfirm(RoleInstance role,String confirmInfo) {
		Result result = new Result();
		if(Util.isEmpty(confirmInfo)){
			return result.setInfo(this.getText(TextId.ERROR_INPUT));
		}
		String[] params = parseParam(confirmInfo);
		if(null == params || params.length != TRANGE_PARAMS_LENGTH){
			return result.setInfo(this.getText(TextId.ERROR_INPUT));
		}
		String mapId = "";
		int mapX = 0;
		int mapY = 0;
		byte pointType = 0 ;
		int cost = 0 ;
		try{
			mapId = params[0];
			mapX = Integer.parseInt(params[1]);
			mapY = Integer.parseInt(params[2]);
			pointType = Byte.parseByte(params[3]);
			cost = Integer.parseInt(params[4]);
			String md5Info = params[5] ;
			String pointInfo = mapId + Cat.comma + mapX + Cat.comma + mapY + Cat.comma  + pointType + Cat.comma + cost ;
			String md5 = GameContext.md5.getMD5(pointInfo + KEY);
			if(!md5Info.equals(md5)){
				return result.setInfo(this.getText(TextId.ERROR_INPUT));
			}
		}catch(Exception e){
			return result.setInfo(this.getText(TextId.ERROR_INPUT));
		}
		Map map = GameContext.getMapApp().getMap(mapId);
		if(null == map){
			return result.setInfo(this.getText(TextId.ERROR_INPUT));
		}
		Point point = new Point(mapId, mapX, mapY,pointType);
		result = this.checkMoney(role, cost);
		if(result.isIgnore() || !result.isSuccess()){
			return result;
		}
		String condInfo = this.cond(role, point);
		if (!Util.isEmpty(condInfo)) {
			return result.setInfo(condInfo);
		}
		//可以传送
		return this.moneyChangMap(role,point,cost);
	}
	
	
	private Message triggerCostMessage(RoleInstance role,Point point,int cost) {
		String pointInfo = point.getMapid() + Cat.comma + point.getX() 
					+ Cat.comma + point.getY() + Cat.comma  + point.getEventType()
					+ Cat.comma + cost ;
		String md5 = GameContext.md5.getMD5(pointInfo + KEY);
		pointInfo = pointInfo + Cat.comma + md5 ;
		return QuickCostHelper.getMessage(role, MAP_TRANSFER_CONFIRM_CMD, pointInfo, MAP_TRANSFER_CONFIRM_CMD, "", 
				GameContext.getI18n().getText(TextId.WORLD_MAP_FEE), cost, 0);
	}
	
	
	private Result toolsChangeMap(RoleInstance role,Point point,int goodsId){
		Result result = new Result();
		if(!this.changeMap(role, point)){
			return result.setInfo(GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_FAIL)) ;
		}
		GameContext.getUserGoodsApp().deleteForBag(role, goodsId, 1, OutputConsumeType.world_transmit);
		String text = GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_BYTOOLS_SUCCESS) ;
		this.sendNotifyMessage(role, text);
		return result.success();
	}
	
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
		if(role.getMapId().equals(point.getMapid())){
			return null ;
		}
		//判断目标地图是否可以传送
		WorldMapInfo mapInfo = this.worldMapInfoMap.get(point.getMapid()) ;
		if(null == mapInfo){
			return GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_NOT_OPEN) ;
		}
		return null ;
	}
	
	private Result checkMoney(RoleInstance role, int cost) {
		Result result = new Result().failure();
		// 金条
		if (cost <= 0) {
			return result.setInfo(Status.Sys_Error.getTips());
		}
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role,
				AttributeType.goldMoney, cost);
		if (ar.isIgnore()) {
			return ar;
		}
		if (!ar.isSuccess()) {
			result.setInfo(GameContext.getI18n().messageFormat(
					TextId.WORLD_MAP_FEE_NOT_ENOUGH, AttributeType.goldMoney.getName()));
			return result;
		}
		return result.success();
	}
	
	
	private Result moneyChangMap(RoleInstance role, Point point,int cost) {
		Result result = new Result();
		if(!this.changeMap(role, point)){
			return result.setInfo(GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_FAIL)) ;
		}
		//扣除消耗
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney
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
	
	private String[] parseParam(String param){
		if(StringUtil.nullOrEmpty(param)){
			return null;
		}
		String[] result = param.split(Cat.comma);
		return result;
	}
}
