package sacred.alliance.magic.app.treasure;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0515_GoodsTreasureTransReqMessage;

import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTreasure;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class TreasureAppImpl extends TreasureApp {
	private static final short TRANSFERCMD_TREASUREMAP = new C0515_GoodsTreasureTransReqMessage().getCommandId();
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		//this.loadTreasureItem();在GoodsLoader中已经做过处理
		this.loadTreasureMaps();
		this.loadTreasureMonsters();
		this.loadTreasureGoods();
		this.init();
	}

	@Override
	public void stop() {
		
	}
	
	private void init(){
		Map<String, GoodsBase> goodsMap = GameContext.getGoodsLoader().getDataMap();
		for(GoodsBase gb : goodsMap.values()){
			if(gb.getGoodsType() != GoodsType.GoodsTreasure.getType()){
				continue ;
			}
			GoodsTreasure treasure = (GoodsTreasure)gb;
			
			int i = 0;
			//初始化map
			String[] probs = Util.splitString(treasure.getMapProbs());
			String[] ids = Util.splitString(treasure.getMaps());
			if(probs.length != ids.length){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure mapIdsLen not equal mapProbsLen, treaId: " + treasure.getId());
			}
			List<Integer> mapProbsList = new ArrayList<Integer>();
			List<String> mapIdsList = new ArrayList<String>();
			int curProbs = 0;
			for(i=0; i<probs.length; i++){
				curProbs += Integer.parseInt(probs[i]);
				mapProbsList.add(curProbs);
				TreasureMap tm = allMapsMap.get(Integer.valueOf(ids[i]));
				if(null == tm){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("treasure TreasureMap not exist , treaId=" + treasure.getId() + " treasureMapId=" + ids[i]);
					continue ;
				}
				//检测逻辑
				tm.check();
				mapIdsList.add(tm.getMapId());
			}
			treasure.setMapProbsTotal(curProbs);
			treasure.setMapProbsList(mapProbsList);
			treasure.setMapsList(mapIdsList);
			
			//检测鉴定物品是否存在
			if(treasure.getIdentifyGoodsId() >0 && 
					null == GameContext.getGoodsApp().getGoodsBase(treasure.getIdentifyGoodsId())){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure idendtify goods not exsit, treaId: " + treasure.getId());
			}
			//初始化怪物
			probs = Util.splitString(treasure.getMonsterProbs());
			ids = Util.splitString(treasure.getMonsterIds());
			if(probs.length != ids.length){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure monsterIdsLen not equal monsterProbsLen, treaId: " + treasure.getId());
			}
			curProbs = 0;
			List<Integer> monsterProbsList = new ArrayList<Integer>();
			List<TreasureMonster> monstersList = new ArrayList<TreasureMonster>();
			for(i=0; i<probs.length; i++){
				curProbs += Integer.parseInt(probs[i]);
				monsterProbsList.add(curProbs);
				TreasureMonster monster = this.allMonstersMap.get(Integer.valueOf(ids[i]));
				if(null == monster){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("reading treasure monster id not in treasureMonsters, treaId: " + treasure.getId() + " treasureMonsterId=" + ids[i] );
					continue ;
				}
				//检测怪物是否存在
				monster.check();
				monstersList.add(monster);
			}
			treasure.setMonsterProbsTotal(curProbs);
			treasure.setMonsterProbsList(monsterProbsList);
			treasure.setMonsterList(monstersList);
			//goods
			probs = Util.splitString(treasure.getGoodsProbs());
			ids = Util.splitString(treasure.getGoodsIds());
			if(probs.length != ids.length){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure goodIdsLen not equal probsLen, treaId: " + treasure.getId());
			}
			curProbs = 0;
			List<Integer> goodsProbsList = new ArrayList<Integer>();
			List<TreasureGood> goodsList = new ArrayList<TreasureGood>();
			for(i=0; i<probs.length; i++){
				curProbs += Integer.parseInt(probs[i]);
				goodsProbsList.add(curProbs);
				TreasureGood good = this.allGoodsMap.get(Integer.valueOf(ids[i]));
				if(null == good){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("reading treasure good id not in treasureGood, treaId: " + treasure.getId() + " treasureGoodId=" + ids[i]);
					continue ;
				}
				//检测物品是否存在
				good.check();
				goodsList.add(good);
			}
			treasure.setGoodsProbsTotal(curProbs);
			treasure.setGoodsProbsList(goodsProbsList);
			treasure.setGoodsList(goodsList);
			//触发事件
			int emptyProb = treasure.getEmptyProb();
			int monsterProb = treasure.getMonsterProb();
			int goodsProb = treasure.getGoodsProb();
			if(emptyProb==0 && monsterProb==0 && goodsProb==0 ){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("use treasure good occur things prob = 0,treaId=" + treasure.getId());
			}
			List<Integer> thingsProbsList = new ArrayList<Integer>();
			thingsProbsList.add(0, emptyProb);
			thingsProbsList.add(1, emptyProb + monsterProb);
			thingsProbsList.add(2, emptyProb + monsterProb + goodsProb);
			treasure.setThingsProbsList(thingsProbsList);
			treasure.setThingsProbsTotal(treasure.getEmptyProb() + treasure.getMonsterProb() + treasure.getGoodsProb());
		}
	}
	
	private void loadTreasureMaps(){
		String fileName = XlsSheetNameType.treasure_maps.getXlsName();
		String sheetName = XlsSheetNameType.treasure_maps.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allMapsMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TreasureMap.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		/*if(Util.isEmpty(this.allMapsMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}*/
	}
	
	private void loadTreasureMonsters(){
		String fileName = XlsSheetNameType.treasure_monsters.getXlsName();
		String sheetName = XlsSheetNameType.treasure_monsters.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allMonstersMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TreasureMonster.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		/*if(Util.isEmpty(this.allMonstersMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return;
		}*/
		for(TreasureMonster treasureMonster : this.allMonstersMap.values() ){
			treasureMonster.init();
		}
	}
	
	private void loadTreasureGoods(){
		String fileName = XlsSheetNameType.treasure_goods.getXlsName();
		String sheetName = XlsSheetNameType.treasure_goods.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			allGoodsMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TreasureGood.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		/*if(Util.isEmpty(this.allGoodsMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return;
		}*/
		for(TreasureGood treasureGood : allGoodsMap.values()){
			treasureGood.init();
		}
		
	}
	
	
	@Override
	public void doIdentify(RoleInstance role,RoleGoods roleGoods){
		//标识为已经鉴定
		roleGoods.setOtherParm(roleGoods.getOtherParm() + GoodsTreasure.IDENTIFY_FLAG);
		GameContext.getUserGoodsApp().syncSomeGoodsGridMessage(role, roleGoods);
	}
	
	@Override
	public TreasurePosResult handleWrongPoint(RoleInstance role, RoleGoods roleGoods){
		TreasurePosResult posResult = new TreasurePosResult();
		GoodsTreasure treasure = (GoodsTreasure)GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		String otherParam = roleGoods.getOtherParm();
		boolean hasIdentify = false;
		Point point = null;
		String mapId = null ;
		short x = 0 ;
		short y = 0 ;
		if(!Util.isEmpty(otherParam)){
			String[] params = GoodsTreasure.parseOtherParams(otherParam);
			mapId = params[0];
			x = Short.valueOf(params[1]);
			y = Short.valueOf(params[2]);
			//是否鉴定
			hasIdentify = treasure.hasIdentify(params);
		}
		if(!treasure.isRightMapId(mapId)){
			point = treasure.createRandomPoint(null);
		}else if(!GoodsTreasure.existRoadPoint(mapId, x, y) 
				|| GoodsTreasure.nearJumpPoint(mapId, x, y)){
			point = treasure.createRandomPoint(mapId);
		}else{
			//藏宝点合法
			posResult.setResult(Result.SUCCESS);
			posResult.setPosType(TreasurePosResult.POS_LEGAL);
			return posResult;
		}
		if(null == point){
			return posResult;
		}
		//重新生成藏宝点成功
		String newOtherParam = treasure.createOtherParams(point);
		roleGoods.setOtherParm(newOtherParam);
		if(hasIdentify){
			this.doIdentify(role, roleGoods);
		}
		posResult.setResult(Result.SUCCESS);
		posResult.setPosType(TreasurePosResult.POS_CARETE_SUCESS);
		return posResult;
	}
	

	@Override
	public Message triggerCostMessage(RoleInstance role,
			String goodsInstanceId, int needGold, int needBinding) {
		String goldParam = AttributeType.goldMoney.getType() + Cat.comma
				+ goodsInstanceId;
		String bindParam = AttributeType.bindingGoldMoney.getType() + Cat.comma
				+ goodsInstanceId;
		return QuickCostHelper.getMessage(role, TRANSFERCMD_TREASUREMAP,
				goldParam, TRANSFERCMD_TREASUREMAP, bindParam, GameContext.getI18n().getText(TextId.WORLD_MAP_FEE),
				needGold, needBinding);
	}
	
	@Override
	public Result transferTargetPoint(RoleInstance role, AttributeType type, int needGold, int needBinding, Point tarPoint) {
		int roleGold = role.getGoldMoney();
		int roleBinding = role.getBindingGoldMoney();
		int cost = 0;
		Result result = new Result();
		result.setResult(Result.FAIL);
		//绑金
		if(AttributeType.bindingGoldMoney == type){
			if(needBinding < 0){
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
				return result;
			}
			else if(needBinding > roleBinding){
				result.setInfo(GameContext.getI18n().messageFormat(TextId.NOT_ENOUGH_ATTRIBUTE,type.getName()));
				return result;
			}
			cost = needBinding;
		}
		//金条
		else if(AttributeType.goldMoney== type){
			if(needGold < 0){
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
				return result;
			}
			else if(needGold > roleGold){
				result.setInfo(GameContext.getI18n().messageFormat(TextId.NOT_ENOUGH_ATTRIBUTE,type.getName()));
				return result;
			}
			cost = needGold;
		}
		//传送
		ChangeMapResult transResult = null ;
		try {
			transResult = GameContext.getUserMapApp().changeMap(role, tarPoint);
		} catch (ServiceException e) {
		}
		if(!transResult.isSuccess()) {
			result.setInfo(transResult.getDesc());
			return result;
		}
		result.setResult(Result.SUCCESS);
		//扣除消耗
		if(null != type){
			GameContext.getUserAttributeApp().changeRoleMoney(role, type
					,OperatorType.Decrease, cost, OutputConsumeType.treasure_map_transmit);
			role.getBehavior().notifyAttribute();
		}
		//发送提示信息
		C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage();
		notifyMsg.setMsgContext(GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_SUCCESS));
		role.getBehavior().sendMessage(notifyMsg);
		return result;
	}
	
}
