package sacred.alliance.magic.app.goods.behavior.derive;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.StrengthenParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.goods.behavior.result.StrengthenResult;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.GoodsStrengthenType;
import sacred.alliance.magic.base.OneKeyOpType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.EquStrengthenstar;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.medal.MedalType;

public class GoodsStrengthen extends AbstractGoodsBehavior{
	
	public GoodsStrengthen(){
		this.behaviorType = GoodsBehaviorType.Strengthen;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		StrengthenParam stParam = (StrengthenParam)param;
		RoleInstance role = stParam.getRole();
		RoleGoods equipGoods = stParam.getEquipGoods();
		byte strengthenType = (byte)stParam.getStrengthentype();
		
		byte operate = stParam.getOperateType();
		if(operate == StrengthenParam.STRENGTHEN_INFO){
			return this.strengthenCondition(role, equipGoods);
		}
		else if(operate == StrengthenParam.STRENGTHEN_EXEC){
			Result result = this.strengthenExec(role, equipGoods, strengthenType);
			if (result instanceof StrengthenResult) {
				StrengthenResult stResult = (StrengthenResult) result;
				//强化日志
				GameContext.getStatLogApp().equipStrengLog(role, (StrengthenParam)param, stResult);
			}
			return result;
		}
		else if(operate == StrengthenParam.STRENGTHEN_ONEKEY){
			Result result = this.strengthenOneKeyExec(role, equipGoods, strengthenType);
			if (result instanceof StrengthenResult) {
				StrengthenResult stResult = (StrengthenResult) result;
				//强化日志
				GameContext.getStatLogApp().equipStrengLog(role, (StrengthenParam)param, stResult);
			}
			return result;
		}
			
		StrengthenResult result = new StrengthenResult();
		result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		return result;
	}

	
	/**
	 * 强化相关条件
	 * @param role
	 * @param equRG
	 * @return
	 */
	private StrengthenResult strengthenCondition(RoleInstance role, RoleGoods equRG) {
			StrengthenResult result = StrengthenResult.newFail();
			if(null == role){
				//用户不在线
				result.setInfo(GameContext.getI18n().getText(TextId.ROLE_NOT_ONLINE));
				return result ;
			}
			//获得装备模板属性
			GoodsEquipment equipGoods = GoodsDeriveSupport.getGoodsEquipment(equRG.getGoodsId());
			if(null == equipGoods){
				result.setResult(RespTypeStatus.FAILURE);
				result.setInfo(GameContext.getI18n().getText(TextId.DERIVE_NO_EQUIPMENT));
				return result;
			}
			//此物品不可强化
			if(equipGoods.getMaxStarLevel()<=0){
				result.setInfo(GameContext.getI18n().getText(TextId.GOODS_CANOT_STRENGTHEN));
				return result;
			}
			result.setResult(StrengthenResult.SUCCESS);
			result.setRoleGoods(equRG);
			result.setGoodsTemplate(equipGoods);
			return result ;
	}
	
	/**
	 * 强化操作执行逻辑
	 * @param role
	 * @param equipGoods
	 * @param strengthenType
	 * @return
	 */
	private Result strengthenExec(RoleInstance role, RoleGoods equipGoods, byte strengthenType) {
		StrengthenResult result = this.baseCond(role, equipGoods, strengthenType);
		if(!result.isSuccess()){
			return result;
		}
		//目标强化等级
		//获得当前装备强化等级、品质
		RoleGoods equRG = result.getRoleGoods();
		int strengthenNum  = equRG.getStarNum();
		int targetStrengthenNum  = strengthenNum + 1;
		EquStrengthenstar strengthenObj = GameContext.getGoodsApp().getStrengthenstar(targetStrengthenNum);
		int money = strengthenObj.getSilverMoney();
		if(role.getSilverMoney() < money){
			result.setInfo(GameContext.getI18n().getText(TextId.NOT_ENOUGH_GAME_MONEY));
			result.failure();
			return result ;
		}
		Map<Integer,Integer> delGoodsMap = new HashMap<Integer,Integer>();
		//强化材料
		int materialId = strengthenObj.getMaterialId();
		int mustMaterialNum = strengthenObj.getMaterialNum();
		if(materialId > 0 && mustMaterialNum > 0){
			delGoodsMap.put(materialId, mustMaterialNum);
		}
		//是否需要保底符
		int stoneId = strengthenObj.getStoneId();
		int mustStoneNum = strengthenObj.getStoneNum();
		GoodsStrengthenType st = result.getStrengthenType();
		if(st == GoodsStrengthenType.no_downgrade && stoneId > 0 && mustStoneNum>0){
			delGoodsMap.put(stoneId, mustStoneNum);//保底强化，扣保底符
		}
		//快捷购买逻辑
		Result res = GameContext.getQuickBuyApp().doQuickBuy(role, delGoodsMap, OutputConsumeType.goods_streng, null);
		if(!res.isSuccess()){
			return res;
		}
		//扣除钱币
		GoodsDeriveSupport.payMoney(role,money,OutputConsumeType.goods_streng);
		//获得强化结果
		int lvChanged = strengthenObj.getStrengthenResult(st);
		this.strengthenUpdate(role, result, strengthenObj, lvChanged);
		return result;
	}
	
	private void strengthenUpdate(RoleInstance role, StrengthenResult result,EquStrengthenstar strengthenObj, int lvChanged){
		RoleGoods equRG = result.getRoleGoods();
		boolean	changeAttribute = false;
		//强化之后，装备变为绑定
		equRG.setBind(BindingType.already_binding.getType());
		//是否穿着
		byte bagType = (byte)equRG.getStorageType();
		boolean on = (StorageType.equip.getType() == bagType);
		
		AttriBuffer oldBuffer = null ;
		if( 0 != lvChanged){
			//强化等级发生了变化
			changeAttribute = true ;
			if(on){
				oldBuffer = RoleGoodsHelper.getAttriBuffer(equRG);
			}
			int newStarNum = equRG.getStarNum() + lvChanged ;
			//修改装备强化等级
			equRG.setStarNum((byte)Math.max(0, newStarNum));
		}
		result.setStarNumChanged(lvChanged);
		result.setResult(Result.SUCCESS);
		try {
			
			if (changeAttribute && on) {
				// 穿着的装备才需要重新计算属性
				// 需要重新计算属性
				boolean onEquipBag = StorageType.equip.getType() == bagType;
				AttriBuffer newBuffer = RoleGoodsHelper.getAttriBuffer(equRG);
				newBuffer.append(oldBuffer.reverse());
				GameContext.getUserAttributeApp().changeAttribute(role, newBuffer);
				role.getBehavior().notifyAttribute() ;
				if(onEquipBag){
					//更新装备特效(装备背包)
					GameContext.getMedalApp().updateMedal(role, MedalType.QiangHua,equRG);
				}
			}
		} catch (Exception ex) {
			logger.error("",ex);
		}
		try {
			// 强化成功发送系统广播
			if (lvChanged > 0) {
				String broadcastInfo = strengthenObj.getBroadcastTips(role,
						result.getGoodsTemplate().getId());
				if (!Util.isEmpty(broadcastInfo)) {
					GameContext.getChatApp().sendSysMessage(ChatSysName.Goods_Strengthen,
							ChannelType.Publicize_Personal, broadcastInfo, null, null);
				}
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	private Result strengthenOneKeyExec(RoleInstance role, RoleGoods equipGoods, byte strengthenType){
		StrengthenResult result = this.baseCond(role, equipGoods, strengthenType);
		if(!result.isSuccess()){
			return result;
		}
		//目标强化等级
		//获得当前装备强化等级、品质
		RoleGoods equRG = result.getRoleGoods();
		int strengthenNum  = equRG.getStarNum();
		int targetStrengthenNum  = strengthenNum + 1;
		EquStrengthenstar strengthenObj = GameContext.getGoodsApp().getStrengthenstar(targetStrengthenNum);
		Map<Integer, Integer> needGoods = new HashMap<Integer, Integer>();
		//强化材料
		int materialId = strengthenObj.getMaterialId();
		int mustMaterialNum = strengthenObj.getMaterialNum();
		if(materialId > 0 && mustMaterialNum > 0){
			needGoods.put(materialId, mustMaterialNum);
		}
		//是否需要保底符
		int stoneId = strengthenObj.getStoneId();
		int mustStoneNum = strengthenObj.getStoneNum();
		GoodsStrengthenType st = result.getStrengthenType();
		if(st == GoodsStrengthenType.no_downgrade && stoneId > 0 && mustStoneNum>0){
			//保底强化
			needGoods.put(stoneId, mustStoneNum);
		}
		int money = strengthenObj.getSilverMoney();
		//判断游戏币是否够一次
		int moneyNum = 0;
		boolean needMoney = false;
		if(money > 0){
			needMoney = true;
			moneyNum = role.getSilverMoney() / money;
			//游戏币不足一次，返回-576消息
			if(moneyNum == 0){
				result.setInfo(GameContext.getI18n().getText(TextId.SYS_Game_Money_Not_Enough));
				result.failure();
				return result;
			}
		}
		int goodsNum = 0; //物品能强化的最小次数
		boolean counsumGoods = false;
		if(!Util.isEmpty(needGoods)){
			counsumGoods = true;
			for(Entry<Integer, Integer> entry : needGoods.entrySet()){
				int ngId = entry.getKey();
				int ngNum = entry.getValue();
				int haveGoodsNum = role.getRoleBackpack().countByGoodsId(ngId);
				//一个都没有
				if(haveGoodsNum <= 0){
					goodsNum = 0;
					break;
				}
				if(goodsNum == 0){
					goodsNum = haveGoodsNum / ngNum;
				}
				else{
					goodsNum = Math.min(goodsNum, haveGoodsNum / ngNum);
				}
			}
			//需要物品并且不够一次使用
			if(goodsNum == 0){
				Message resp576 = Converter.buildOneKeyOpFailMessage(OneKeyOpType.STRENGTHEN, money, needGoods);
				role.getBehavior().sendMessage(resp576);
				result.success();
				return result;
			}
		}
		if(!needMoney && !counsumGoods){
			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
			return result;
		}
		int canExCount = (needMoney && counsumGoods) ? Math.min(moneyNum, goodsNum) : Math.max(moneyNum, goodsNum);
		int totalExcount = canExCount;
		int lvChanged = 0;
		while(totalExcount > 0){
			totalExcount --;
			//获得强化结果
			lvChanged = strengthenObj.getStrengthenResult(st);
			if(0 != lvChanged){
				break;
			}
		}
		//扣除消耗
		int realExCount = canExCount - totalExcount;
		result.setExecCount(realExCount);
		//判断是否需要保底强化符
		if(counsumGoods){
			Map<Integer, Integer> delGoodsMap = new HashMap<Integer, Integer>();
			for(Entry<Integer, Integer> entry : needGoods.entrySet()){
				int goodsId = entry.getKey();
				int value = entry.getValue() * realExCount;
				if(delGoodsMap.containsKey(goodsId)){
					value += delGoodsMap.get(goodsId);
				}
				delGoodsMap.put(goodsId, value);
			}
			GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBagByMap(role, delGoodsMap, OutputConsumeType.goods_streng);
			if(!goodsResult.isSuccess()){
				return goodsResult;
			}
			result.setConsumeIdAndNum(needGoods);
		}
		//扣除钱币
		int totalFee = money * realExCount;
		GoodsDeriveSupport.payMoney(role,totalFee,OutputConsumeType.goods_streng);
		result.setFee(money);
		if(0 != lvChanged){
			//强化升级
			this.strengthenUpdate(role, result, strengthenObj, lvChanged);
			result.setFee(totalFee);
			return result;
		}
		result.success();
		return result;
	}
	
	private StrengthenResult baseCond(RoleInstance role, RoleGoods equipGoods, byte strengthenType){
		GoodsStrengthenType st = GoodsStrengthenType.get(strengthenType);
		if(null == st){
			StrengthenResult result = StrengthenResult.newFail();
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		StrengthenResult result = this.strengthenCondition(role, equipGoods);
		if(!result.isSuccess()){
			return result ;
		}
		result.setStrengthenType(st);
		result.setResult(Result.FAIL);
		result.setInfo("");
		
		RoleGoods equRG = result.getRoleGoods();
		GoodsEquipment goodsTemplate = result.getGoodsTemplate();
		//获得当前装备强化等级、品质
		int strengthenNum  = equRG.getStarNum();
		//是否可强化到最大等级
		if(strengthenNum >= goodsTemplate.getMaxStarLevel()){
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_MAX_STRENGTHEN_LV));
			return result;
		}
		result.success();
		return result;
	}
	
}
