package sacred.alliance.magic.app.goods.behavior;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsTreasureBehavior;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.treasure.TreasureGood;
import sacred.alliance.magic.app.treasure.TreasureMonster;
import sacred.alliance.magic.app.treasure.TreasureMonsterParam;
import sacred.alliance.magic.app.treasure.TreasurePosResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTreasure;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.type.NpcSummonType;
import com.game.draco.message.push.C0006_TipNoSelectWindowNotifyMessage;

public class UseGoodsTreasure extends GoodsTreasureBehavior {
	protected final static Logger logger = LoggerFactory.getLogger(UseGoodsTreasure.class);
	private final static int[] DISTANCE = {600,300};
	//提示信息时,角度偏移10度
	private final static int DEGREES_OFFSET = 10 ;
	public UseGoodsTreasure(){
		this.behaviorType = GoodsBehaviorType.Use;
	}
	/**
	 * 使用无尽深渊（藏宝图）物品
	 */
	@Override
	public Result operate(AbstractParam param) {
		UseGoodsParam useParam = (UseGoodsParam)param ;
		RoleGoods roleGoods = useParam.getRoleGoods();
		int goodsId = roleGoods.getGoodsId();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
		RoleInstance role = param.getRole();
		if(roleGoods == null || role == null || goodsBase == null){
			return new Result().setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
		}
		String otherParam = roleGoods.getOtherParm();
		GoodsTreasure treasure = (GoodsTreasure)goodsBase;
		Result posUseResult = canPositionUse(role, otherParam, treasure);
		if(!posUseResult.isSuccess()){
			TreasurePosResult posResult = handleWrongPoint(role, roleGoods);
			if(!posResult.isSuccess()){
				return new Result().setInfo(GameContext.getI18n().getText(TextId.TREASURE_USE_FAILE));
			}
			if(posResult.getPosType() == TreasurePosResult.POS_CARETE_SUCESS){
				//重新生成藏宝点成功则要重新判断一遍点
				posUseResult = canPositionUse(role, otherParam, treasure);
				if(!posUseResult.isSuccess()){
					return posUseResult;
				}
			}
			if(posResult.getPosType() == TreasurePosResult.POS_LEGAL){
				return posUseResult;
			}
		}
		//成功使用“虚空漩涡”（藏宝图）
		GoodsResult useResult = this.useTreasure(role, treasure, roleGoods);
		if(useResult.isSuccess()){
			GameContext.getUserGoodsApp().deleteForBagByRoleGoods(role, roleGoods, 1, OutputConsumeType.treasure_map_use);
			return useResult;
		}
		return useResult;
	}
	
	private Result canPositionUse(RoleInstance role, String otherParam, GoodsTreasure treasure){
		Result result = new Result();
		String[] otherParams = GoodsTreasure.parseOtherParams(otherParam);
		if(null == otherParams || otherParams.length < 3){
			return result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		}
		String mapId = otherParams[0];
		MapInstance curMap = role.getMapInstance();
		//判断能否使用
		if(null == curMap){
			return result.setInfo(GameContext.getI18n().getText(TextId.MAP_INSTANCE_NULL));
		}
		
		if(!mapId.equals(curMap.getMap().getMapId())){
			return result.setInfo(GameContext.getI18n().getText(TextId.TREASURE_CANOT_USE_MAP));
		}
		//如果半径radius配置为-1，则没有位置限制
		int radius = treasure.getRadius();
		if(radius <= 0){
			result.setResult(Result.SUCCESS);
			return result;
		}
		short x = Short.valueOf(otherParams[1]);
		short y = Short.valueOf(otherParams[2]);
		//距离
		int distance = Util.distance(role.getMapX(), role.getMapY(), x, y);
		if(distance <= radius){
			//可以成功使用
			result.setResult(Result.SUCCESS);
			return result;
		}
		result.setInfo(GameContext.getI18n().getText(TextId.TREASURE_DISTANCE_TIPS)
				.replace("{0}",this.getDegreesInfo(role.getMapX(),role.getMapY(), x, y))
				.replace("{1}",this.getDistanceInfo(distance)));
		return result ;
	}
		
	private String getDegreesInfo(int roleX,int roleY,int x,int y){
		return GameContext.getI18n().getText(TextId.TREASURE_DEGREES[this.getDegreesIndex(roleX, roleY, x, y)]);
	}
	
	private int getDegreesIndex(int roleX,int roleY,int x,int y){
		int d = Util.getDegrees(roleX, roleY, x, y);
		if(d < 0){
			d += 360 ;
		}
		if(this.inRange(d, 0, DEGREES_OFFSET) 
				|| this.inRange(d, 360-DEGREES_OFFSET, 360)){
			//右
			return 0 ;
		}
		if(this.inRange(d, DEGREES_OFFSET, 90-DEGREES_OFFSET)){
			return 1 ;
		}
		if(this.inRange(d, 90-DEGREES_OFFSET, 90 + DEGREES_OFFSET)){
			return 2 ;
		}
		if(this.inRange(d, 90+DEGREES_OFFSET, 180-DEGREES_OFFSET)){
			return 3 ;
		}
		if(this.inRange(d, 180-DEGREES_OFFSET, 180+DEGREES_OFFSET)){
			return 4 ;
		}
		if(this.inRange(d, 180+DEGREES_OFFSET, 270-DEGREES_OFFSET)){
			return 5 ;
		}
		if(this.inRange(d, 270-DEGREES_OFFSET, 270+DEGREES_OFFSET)){
			return 6 ;
		}
		return 7 ;
	}
	
	private boolean inRange(int value,int min,int max){
		return value >= min && value <= max ;
	}
	
	
	
	private String getDistanceInfo(int distance) {
		for (int index = 0; index < Math.min(DISTANCE.length, TextId.TREASURE_DISTANCE.length); index++) {
			if (distance > DISTANCE[index]) {
				return GameContext.getI18n().getText(TextId.TREASURE_DISTANCE[index]);
			}
		}
		return GameContext.getI18n().getText(TextId.TREASURE_DISTANCE[TextId.TREASURE_DISTANCE.length - 1]);
	}
	
	private void pushIncomeMessage(RoleInstance role,List<GoodsOperateBean> goodsList,
			int bindMoney,int gameMoney){
		Converter.pushIncomeMessage(role, goodsList, bindMoney, gameMoney, 0);
	}
	
	private GoodsResult useTreasure(RoleInstance role, GoodsTreasure treasure,
			RoleGoods roleGoods){
		GoodsResult result = new GoodsResult();
		result.failure();
		int lvlimit = treasure.getLvLimit();
		if(lvlimit > role.getLevel()){
			result.setInfo(GameContext.getI18n().getText(TextId.ROLE_LEVEl_SHORTAGE));
			return result ;
		}
		try{
			int index = Util.getProbsIndex(treasure.getThingsProbsList(), treasure.getThingsProbsTotal());
			C0006_TipNoSelectWindowNotifyMessage tips = null;
			switch(index){
			case 0://什么都没有获得
				result.success();
				tips = new C0006_TipNoSelectWindowNotifyMessage();
				tips.setMsgContent(GameContext.getI18n().getText(TextId.TREASURE_GET_NOTHING));
				role.getBehavior().sendMessage(tips);
				break;
			case 1: //触发怪物
				int monsterIdIndex = Util.getProbsIndex(treasure.getMonsterProbsList(), treasure.getMonsterProbsTotal());
				TreasureMonster monster = treasure.getMonsterList().get(monsterIdIndex);//
				for(TreasureMonsterParam param : monster.getMonstersParamMap().values()){
					createTreasureMonster(role, param);
				}
				result.success();
				//广播
				try{
					if(!Util.isEmpty(monster.getBroadcastInfo())){
						String msg = monster.getBroadcastTips(role, Wildcard.getChatGoodsName(treasure.getId(), ChannelType.Publicize_Personal));
						GameContext.getChatApp().sendSysMessage(ChatSysName.Treasure, ChannelType.Publicize_Personal, msg, null, null);
					}
				}catch (Exception ex){
					logger.error("" ,ex);
				}
				break;
			case 2: //获得物品
				int freeSize = role.getRoleBackpack().freeGridCount();
				if(freeSize <=0){
					result.setInfo(Status.Bag_Is_Full.getTips());
					return result ;
				}
				int goodsIndex = Util.getProbsIndex(treasure.getGoodsProbsList(), treasure.getGoodsProbsTotal());
				TreasureGood good = treasure.getGoodsList().get(goodsIndex);
				
				AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, good.getGoodsList(), OutputConsumeType.treasure_map);
				//避免用户刷物品
				//1.背包至少要有1个空格
				//2.背包满的时候发邮件
				//背包满了发邮件
				List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
				try{
					if(!Util.isEmpty(putFailureList)){
						GameContext.getMailApp().sendMail(role.getRoleId(), 
								MailSendRoleType.TreasureMap.getName(), "", 
								MailSendRoleType.TreasureMap.getName(), 
								OutputConsumeType.treasure_map_mail.getType(),
								putFailureList);
					}
				}catch(Exception e){
					logger.error("",e);
				}
				
				int bindMoney = good.getBindMoney();
				int gameMoney = good.getGameMoneyMin()
						+ (int) (Math.random() * (good.getGameMoneyMax() - good
								.getGameMoneyMin()));
				boolean needNotify = false;
				if (gameMoney > 0) {
					GameContext.getUserAttributeApp().changeRoleMoney(role,
							AttributeType.gameMoney, OperatorType.Add,
							gameMoney, OutputConsumeType.treasure_map);
					needNotify = true;
				}
				if (needNotify) {
					role.getBehavior().notifyAttribute();
				}
				result.success();
				try {
					// 给客户端提示获得物品信息
					this.pushIncomeMessage(role, good.getGoodsList(),
							bindMoney, gameMoney);
					// 广播(只喊第一个物品)
					this.broadcastGoods(role.getRoleName(), good.getGoods1(),
							treasure.getId(), good.getBroadcastInfo());
				}catch(Exception ex){
					logger.error("",ex);
				}
				
				break;
			}
			return result;
		}catch(Exception e){
			logger.error("", e);
		}
		return result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
	}
	
	/**
	 * 系统广播（有物品的只喊第一个物品）
	 * @param roleName
	 * @param goodsId
	 * @param treasureId
	 * @param goodsBCInfo
	 */
	private void broadcastGoods(String roleName, int goodsId, int treasureId, String goodsBCInfo){
		try{
			if(Util.isEmpty(goodsBCInfo)){
				return;
			}
			String treasureStr = Wildcard.getChatGoodsName(treasureId, ChannelType.Publicize_Personal);
			String goodsStr = Wildcard.getChatGoodsName(goodsId, ChannelType.Publicize_Personal); 
			String msg = MessageFormat.format(goodsBCInfo, roleName, treasureStr , goodsStr);
			GameContext.getChatApp().sendSysMessage(ChatSysName.Treasure, ChannelType.Publicize_Personal, msg, null, null);
		}catch (Exception ex){
			logger.error("UseGoodsTreasure.broadcastGoods error:" ,ex);
		}
	}
	
	private void createTreasureMonster(RoleInstance role, TreasureMonsterParam param) {
		try{
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				return;
			}
			int roleMapX = role.getMapX();
			int roleMapY = role.getMapY();
			for(int i=0; i< param.getNum(); i++){
				NpcInstance npc = mapInstance.summonCreateNpc(param.getNpcId(), roleMapX, roleMapY, role.getRoleId());
				npc.setSummonType(NpcSummonType.TREASURE.getType());
				//玩家提示
				C0006_TipNoSelectWindowNotifyMessage tips = new C0006_TipNoSelectWindowNotifyMessage();
				String context = MessageFormat.format(
						   GameContext.getI18n().getText(TextId.TREASURE_GET_MONSTER),
						   npc.getNpcname());
				tips.setMsgContent(context);
				role.getBehavior().sendMessage(tips);
			}
		}catch(Exception e) {
			logger.error("use treasure to create monster error!",e);
		}
		
	}

}
