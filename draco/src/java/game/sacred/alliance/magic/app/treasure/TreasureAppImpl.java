package sacred.alliance.magic.app.treasure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.map.MapUtil;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTreasure;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.dailyplay.DailyPlayType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.team.Team;
import com.game.draco.message.request.C0515_GoodsTreasureTransReqMessage;

public class TreasureAppImpl implements TreasureApp {
	private static final short TRANSFERCMD_TREASUREMAP = new C0515_GoodsTreasureTransReqMessage().getCommandId();
	
	protected Map<Integer, TreasureMap> allMapsMap = null;
	protected Map<Integer, TreasureMonster> allMonstersMap = null;
	protected Map<Integer, TreasureGood> allGoodsMap = null;
	
	@Override
	public Map<Integer, TreasureMap> getAllMapsMap() {
		return allMapsMap;
	}
	
	@Override
	public void setAllMapsMap(Map<Integer, TreasureMap> allMapsMap) {
		this.allMapsMap = allMapsMap;
	}
	
	@Override
	public Map<Integer, TreasureMonster> getAllMonstersMap() {
		return allMonstersMap;
	}
	
	public void setAllMonstersMap(Map<Integer, TreasureMonster> allMonstersMap) {
		this.allMonstersMap = allMonstersMap;
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadTreasureMaps();
		this.loadTreasureMonsters();
		this.loadTreasureGoods();
		this.init();
	}

	@Override
	public void stop() {
		
	}
	
	/**
	 * 检查藏宝图配置
	 */
	private void init(){
		Map<String, GoodsBase> goodsMap = GameContext.getGoodsLoader().getDataMap();
		for(GoodsBase gb : goodsMap.values()){
			if(gb.getGoodsType() != GoodsType.GoodsTreasure.getType()){
				continue ;
			}
			GoodsTreasure gt = (GoodsTreasure)gb;
			int i = 0;
			//初始化map
			//地图概率
			String[] probs = Util.splitString(gt.getMapProbs());
			//地图列表
			String[] ids = Util.splitString(gt.getMaps());
			if(probs.length != ids.length){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure mapIdsLen not equal mapProbsLen, treaId: " + gt.getId());
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
					Log4jManager.CHECK.error("treasure TreasureMap not exist , treaId=" + gt.getId() + " treasureMapId=" + ids[i]);
					continue ;
				}
				//检测逻辑
				tm.check();
				mapIdsList.add(tm.getMapId());
			}
			gt.setMapProbsTotal(curProbs);
			gt.setMapProbsList(mapProbsList);
			gt.setMapsList(mapIdsList);
			
			//检测鉴定物品是否存在
			if(gt.getIdentifyGoodsId() >0 && 
					null == GameContext.getGoodsApp().getGoodsBase(gt.getIdentifyGoodsId())){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure idendtify goods not exsit, treaId: " + gt.getId());
			}
			//初始化怪物
			probs = Util.splitString(gt.getMonsterProbs());
			ids = Util.splitString(gt.getMonsterIds());
			if(probs.length != ids.length){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure monsterIdsLen not equal monsterProbsLen, treaId: " + gt.getId());
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
					Log4jManager.CHECK.error("reading treasure monster id not in treasureMonsters, treaId: " + gt.getId() + " treasureMonsterId=" + ids[i] );
					continue ;
				}
				//检测怪物是否存在
				monster.check();
				monstersList.add(monster);
			}
			gt.setMonsterProbsTotal(curProbs);
			gt.setMonsterProbsList(monsterProbsList);
			gt.setMonsterList(monstersList);
			//goods
			//是否出现道具的权重
			probs = Util.splitString(gt.getGoodsProbs());
			//道具ID列表
			ids = Util.splitString(gt.getGoodsIds());
			if(probs.length != ids.length){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("treasure goodIdsLen not equal probsLen, treaId: " + gt.getId());
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
					Log4jManager.CHECK.error("reading treasure good id not in treasureGood, treaId: " + gt.getId() + " treasureGoodId=" + ids[i]);
					continue ;
				}
				//检测物品是否存在
				good.check();
				goodsList.add(good);
			}
			gt.setGoodsProbsTotal(curProbs);
			gt.setGoodsProbsList(goodsProbsList);
			gt.setGoodsList(goodsList);
			//触发事件
			int emptyProb = gt.getEmptyProb();
			int monsterProb = gt.getMonsterProb();
			int goodsProb = gt.getGoodsProb();
			if(emptyProb==0 && monsterProb==0 && goodsProb==0 ){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("use treasure good occur things prob = 0,treaId=" + gt.getId());
			}
			List<Integer> thingsProbsList = new ArrayList<Integer>();
			thingsProbsList.add(0, emptyProb);
			thingsProbsList.add(1, emptyProb + monsterProb);
			thingsProbsList.add(2, emptyProb + monsterProb + goodsProb);
			gt.setThingsProbsList(thingsProbsList);
			gt.setThingsProbsTotal(gt.getEmptyProb() + gt.getMonsterProb() + gt.getGoodsProb());
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
		if(Util.isEmpty(this.allMapsMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
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
		if(Util.isEmpty(this.allMonstersMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			return;
		}
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
		if(Util.isEmpty(this.allGoodsMap)){
			/*Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);*/
			return;
		}
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
	
	/**
	 * 固定地点改为任意地点
	 */
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
		}else if(!MapUtil.existRoadPoint(mapId, x, y) 
				|| MapUtil.nearJumpPoint(mapId, x, y)){
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
	

	/*@Override
	public Message triggerCostMessage(RoleInstance role,
			String goodsInstanceId, int needGold) {
		if(needGold <= 0){
			//提升缺少传输卷轴
			String msg = GameContext.getI18n().getText(TextId.WORLD_MAP_TRANSFER_HAVENOT_GOODS) ;
			return new C0003_TipNotifyMessage(msg); 
		}
		String goldParam = AttributeType.goldMoney.getType() + Cat.comma
				+ goodsInstanceId;
		return QuickCostHelper.getMessage(role, TRANSFERCMD_TREASUREMAP,
				goldParam, TRANSFERCMD_TREASUREMAP, "", GameContext.getI18n().getText(TextId.WORLD_MAP_FEE),
				needGold, 0);
	}*/
	
	/**
	 * 虚空漩涡（藏宝图）传送
	 */
	/*@Override
	public Result transferTargetPoint(RoleInstance role, int needGold,
			Point tarPoint) {
		Result result = new Result().failure();
		// 钻石
		if (needGold < 0) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result;
		}
		// 【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role,
				AttributeType.goldMoney, needGold);
		if (ar.isIgnore()) {
			return ar;
		}
		if (!ar.isSuccess()) {
			result.setInfo(GameContext.getI18n().messageFormat(
					TextId.NOT_ENOUGH_ATTRIBUTE,
					AttributeType.goldMoney.getName()));
			return result;
		}
		// 传送
		ChangeMapResult transResult = null;
		try {
			// 设置为藏宝图传送便于客户端传送成功后进行使用藏宝图读条
			tarPoint.setEventType(ChangeMapEvent.treasure.getEventType());
			transResult = GameContext.getUserMapApp().changeMap(role, tarPoint);
		} catch (ServiceException e) {

		}
		if (!transResult.isSuccess()) {
			result.setInfo(transResult.getDesc());
			return result;
		}
		result.setResult(Result.SUCCESS);
		// 扣除消耗
		GameContext.getUserAttributeApp().changeRoleMoney(role,
				AttributeType.goldMoney, OperatorType.Decrease, needGold,
				OutputConsumeType.treasure_map_transmit);
		role.getBehavior().notifyAttribute();
		
		// 发送提示信息
		C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage();
		notifyMsg.setMsgContext(GameContext.getI18n().getText(
				TextId.WORLD_MAP_TRANSFER_SUCCESS));
		role.getBehavior().sendMessage(notifyMsg);
		return result;
	}*/
	
	/**
	 * 无尽深渊（藏宝箱）
	 */
	@Override
	public boolean summonDeath(NpcInstance npc) {
		String summonerId = npc.getSummonRoleId();
		if(null == summonerId) {
			return false;
		}
		//召唤者必须在当前地图
		MapInstance map = npc.getMapInstance() ;
		if(null == map){
			return false ;
		}
		RoleInstance summoner = GameContext.getOnlineCenter().getRoleInstanceByRoleId(summonerId) ;
		if(null == summoner){
			return false ;
		}
		//要求在同一地图
		if(map.isSameMapInstance(summoner.getMapInstance())){
			GameContext.getFallApp().fallBox(npc, summoner, OutputConsumeType.treasure_map);
		}
		//激活日常
		this.dailyEffect(summoner, npc);
		return true;
	}
	
	private void dailyEffect(RoleInstance summoner, NpcInstance npc) {
		if (1 != npc.getNpc().getTreasureBoss()) {
			return;
		}
		// 召唤者必须在当前地图
		MapInstance map = npc.getMapInstance();
		if (null == map) {
			return;
		}
		Team team = summoner.getTeam();
		if(null == team){
			this.dailyEffect(map, summoner);
			return ;
		}
		for(AbstractRole m : team.getMembers()){
			this.dailyEffect(map, (RoleInstance)m);
		}
	}

	private void dailyEffect(MapInstance map,RoleInstance role){
		if(!map.isSameMapInstance(role.getMapInstance())){
			return ;
		}
		GameContext.getDailyPlayApp().incrCompleteTimes(role, 1,
				DailyPlayType.kill_treasure_boss, "");
	}
}
