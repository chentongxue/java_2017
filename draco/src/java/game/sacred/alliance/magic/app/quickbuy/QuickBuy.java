package sacred.alliance.magic.app.quickbuy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class QuickBuy{
	
	private List<QuickBuyGoods> quickBuyGoodsList;
	
	public boolean isQuickBuyGoodsEmpty(){
		return Util.isEmpty(this.quickBuyGoodsList);
	}
	
	/**
	 * 判断道具是否足够
	 * @param role
	 * @return
	 */
	public boolean isGoodsEnough(RoleInstance role){
		if(this.isQuickBuyGoodsEmpty()){
			return false;
		}
		boolean enough = true;
		for(QuickBuyGoods quickBuyGoods : this.quickBuyGoodsList){
			if(null == quickBuyGoods){
				continue;
			}
			//计算需要购买的道具数量
			boolean itemEnough = quickBuyGoods.isRoleGoodsEnough(role);
			//所有物品都足够时，才算道具足够
			enough = enough && itemEnough;
		}
		return enough;
	}
	
	/**
	 * 获取需要支付的元宝数量
	 * @param role
	 * @return
	 */
	public int getPayGoldMoney(RoleInstance role){
		int payGoldMoney = 0;
		for(QuickBuyGoods quickBuyGoods : this.quickBuyGoodsList){
			payGoldMoney += quickBuyGoods.getPayGoldMoney(role);
		}
		return payGoldMoney;
	}
	
	/**
	 * 获取快速购买的二次确认信息
	 * @return
	 */
	public String getConfirmMsgInfo(RoleInstance role){
		StringBuffer buffer = new StringBuffer();
		int payGoldMoney = 0;
		for(QuickBuyGoods quickBuyGoods : this.quickBuyGoodsList){
			int diffNum = quickBuyGoods.getPayGoodsNum(role);
			if(diffNum <= 0){
				continue;
			}
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(quickBuyGoods.getGoodsId());
			String goodsInfo = Status.QuickBuy_Goods_Info.getTips()
				.replace(Wildcard.GoodsName, goodsBase.getColorName()).replace(Wildcard.Number, String.valueOf(diffNum));
			buffer.append(goodsInfo);
			payGoldMoney += quickBuyGoods.getPayGoldMoney(role);
		}
		return Status.QuickBuy_Confirm_Info.getTips()
			.replace(Wildcard.QuickBuy_Goods_Name_Num, buffer.toString())
			.replace(Wildcard.Number, String.valueOf(payGoldMoney));
	}
	
	public QuickBuyResult deductMoney(RoleInstance role, OutputConsumeType consumeType){
		QuickBuyResult result = new QuickBuyResult();
		result.setPayFailure(Status.QuickBuy_Param_Error.getTips());
		//如果不是确认购买的，返回失败
		if(!role.isQuickBuyconfirm()){
			return result;
		}
		//清掉角色身上快速购买的确认标记
		role.setQuickBuyconfirm(false);
		//--------------------------------------------------
		Map<Integer,Integer> delMap = new HashMap<Integer,Integer>();//角色拥有的物品，需要扣除
		int payGoldMoney = 0;//相差的物品，需要支付元宝总数
		for(QuickBuyGoods quickBuyGoods : this.quickBuyGoodsList){
			if(null == quickBuyGoods){
				continue;
			}
			//计算需要购买的道具数量
			quickBuyGoods.isRoleGoodsEnough(role);
			int delNum = quickBuyGoods.getDelRoleGoodsNum(role);
			if(delNum > 0){
				delMap.put(quickBuyGoods.getGoodsId(), delNum);
			}
			payGoldMoney += quickBuyGoods.getPayGoldMoney(role);
		}
		//--------------------------------------------------
		if(payGoldMoney <= 0){
			return result;
		}
		//首先，判断元宝是否足够
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, payGoldMoney);
		if(ar.isIgnore()){
			result.setIgnore(true);
			return result;
		}
		if(!ar.isSuccess()){
			return result.setPayFailure(Status.QuickBuy_Gold_Not_Enough.getTips());
		}
//		if(role.getGoldMoney() < payGoldMoney){
//			return result.setPayFailure(Status.QuickBuy_Gold_Not_Enough.getTips());
//		}
		//其次，扣除物品
		GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBagByMap(role, delMap, consumeType);
		if(!goodsResult.isSuccess()){
			return result.setPayFailure(goodsResult.getInfo());
		}
		//最后，扣元宝
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, 
				OperatorType.Decrease, payGoldMoney, consumeType);
		role.getBehavior().notifyAttribute();
		//快速购买日志
		GameContext.getStatLogApp().roleQuickBuyLog(role, quickBuyGoodsList, consumeType);
		return result.setPaySuccess();
	}
	
}
