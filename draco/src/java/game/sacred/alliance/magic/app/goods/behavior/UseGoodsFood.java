package sacred.alliance.magic.app.goods.behavior;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.IntervalTimeItem;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.UseGoodsParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.map.MapProperty;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsFood;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffDetail;
import com.game.draco.app.buff.GoodsBuff;
import com.game.draco.message.push.C0520_GoodsUseCoolingTimeNotifyMessage;


/**
 * 使用药水、吃馒头
 * 这取决于GoogsType配置
 * @author Wang.K
 *
 */
public class UseGoodsFood extends AbstractGoodsBehavior{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public UseGoodsFood() {
		this.behaviorType = GoodsBehaviorType.Use;
	}

	
	/*
	 * 1.是否为goodsFood
	 * 2.使用条件判断，等级、冷却时间
	 * 3.加buff
	 * 4.判断是否消耗物品，即消耗掉
	 * 5.重置冷却时间
	 */
	@Override
	public GoodsResult operate(AbstractParam param) {
		GoodsResult result = new GoodsResult();
		try{
			UseGoodsParam useGoodsParam = (UseGoodsParam)param;
			RoleGoods roleGoods = useGoodsParam.getRoleGoods();
			RoleInstance role = useGoodsParam.getRole();
			int goodsId = roleGoods.getGoodsId();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(roleGoods == null || role == null || goodsBase == null){
				return result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			}
			GoodsFood food = (GoodsFood)goodsBase;
			String canResult = this.canUse(role, food);
			if(canResult != null){
				return result.setInfo(canResult);
			}
			int count = useGoodsParam.getUseCount();
			if(count <1){
				count = 1 ;
			}
			if(count > roleGoods.getCurrOverlapCount()){
				count = roleGoods.getCurrOverlapCount();
			}
			if(food.getIntervalTime() > 0){
				count = 1 ;
			}
			Result useResult = null ;
			int useCount = 0 ;
			for(;useCount<count;useCount++){
				useResult = this.useEffered(role, food);
				if(!useResult.isSuccess()){
					break ;
				}
			}
			if(useCount > 0){
				this.setIntervalTime(role, food);
				this.consume(role, roleGoods, food,useCount);
				this.coolingTimeNotify(role, food); 
				return result.setResult(GoodsResult.SUCCESS);
			}
			return result.setInfo(useResult.getInfo());
		}catch(Exception e){
			log.error("",e);
		}
		return result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
	}
	
	
	private void coolingTimeNotify(RoleInstance role, GoodsFood food){
		try{
			int applyIntervalTime = food.getIntervalTime();
			if(applyIntervalTime <= 0){
				return ;
			}
			byte coolingTimeType = (byte)food.getIntervalId();
			C0520_GoodsUseCoolingTimeNotifyMessage resp = new C0520_GoodsUseCoolingTimeNotifyMessage();
			resp.setCoolingTimeType(coolingTimeType);
			resp.setApplyIntervalTime(applyIntervalTime);
			role.getBehavior().sendMessage(resp);
		}catch(Exception e){
			log.error("",e);
		}
	}
	
	
	
	
	private String canUse(RoleInstance role, GoodsFood food) {
		if(!GameContext.getMapApp().canMapProperty(role, MapProperty.canUseFood.getType())){
			return GameContext.getI18n().getText(TextId.MAP_CANOT_DO_THIS_THING);
		}
		//判断当前地图是否允许使用
		int lvlimit = food.getLvLimit();
		if (lvlimit > role.getLevel()) {
			return GameContext.getI18n().getText(TextId.ROLE_LEVEl_SHORTAGE);
		}
		if (food.getIntervalTime() <= 0) {
			return null;
		}
		int intervalId = food.getIntervalId();
		IntervalTimeItem item = role.getTimeMap().get(intervalId);
		if (null == item) {
			return null;
		}
		long diffTime = System.currentTimeMillis() - item.getExecTime();
		if (diffTime >= item.getIntervalTime()) {
			return null;
		} else {
			return GameContext.getI18n().messageFormat(
					TextId.USE_GOODS_FOODS_CD,
					(int) ((item.getIntervalTime() - diffTime) / 1000));
		}

	}
	
	
	private Result useEffered(RoleInstance role, GoodsFood food){
		boolean empty = true ;
		Result result = new Result();
		short triggerBuffId = food.getTriggerBuffId();
		int triggerBuffLv = food.getTriggerBuffLv();
		if(triggerBuffId > 0 && triggerBuffLv >0){
			empty = false ;
			//添加buff
			GoodsBuff goodsBuff = (GoodsBuff) GameContext.getBuffApp().getBuff(triggerBuffId);
			result = GameContext.getUserBuffApp().addBuffStat(role, 
					role,triggerBuffId,goodsBuff.getPersistTime(), triggerBuffLv);
			if(!result.isSuccess()){
				return result ;
			}
		}
		Set<Short> cleanBuffSet = food.getCleanBuffSet();
		if(!Util.isEmpty(cleanBuffSet)){
			empty = false ;
			//清除buff
			GameContext.getUserBuffApp().cleanBuffById(role, cleanBuffSet, 0);
			result.success();
		}
		if(empty){
			//没有任何效果使用成功
			result.success();
		}
		return result ;
	}
	
	
	private void setIntervalTime(RoleInstance role, GoodsFood food){
		int intervalId = food.getIntervalId();
		int intervalTime = food.getIntervalTime();
		if(intervalTime <=0){
			return ;
		}
		IntervalTimeItem item = role.getTimeMap().get(intervalId);
		if (null == item) {
			IntervalTimeItem it = new IntervalTimeItem();
			it.setExecTime(System.currentTimeMillis());
			it.setIntervalTime(intervalTime);
			role.getTimeMap().put(intervalId, it);
		}else{
			item.setExecTime(System.currentTimeMillis());
			item.setIntervalTime(intervalTime);
		}
	}
	
	
	private void consume(RoleInstance role, RoleGoods roleGoods, GoodsFood food,int useCount){
		boolean consume = food.hasApplyDisappear();
		if(consume){
			GameContext.getUserGoodsApp().deleteForBagByRoleGoods(role, 
					roleGoods, useCount, OutputConsumeType.goods_use);
		}
	}


}
