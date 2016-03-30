package sacred.alliance.magic.action;

import java.util.Date;
import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2317_ActiveDiscountRewardReqMessage;
import com.game.draco.message.response.C2317_ActiveDiscountRewardRespMessage;

import sacred.alliance.magic.app.active.discount.type.DiscountRewardStat;
import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountReward;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.hint.HintId;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveDiscountRewardAction extends BaseAction<C2317_ActiveDiscountRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C2317_ActiveDiscountRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		C2317_ActiveDiscountRewardRespMessage respMsg = new C2317_ActiveDiscountRewardRespMessage();
		respMsg.setStatus(Status.FAILURE.getInnerCode());
		Map<Integer, Discount> allListMap = GameContext.getActiveDiscountApp().getAllListMap();
		if(null==allListMap || allListMap.size()==0){
			return respMsg;
		}
		Discount discount = allListMap.get(reqMsg.getId());
		if(null == discount){
			return respMsg;
		}
		int condIndex = reqMsg.getIndex();
		//如果是不可领取状态直接返回
		DiscountDbInfo discountDbInfo = role.getDiscountDbInfo().get(discount.getId());
		if(discount.getRewardStatus(discountDbInfo, condIndex) != DiscountRewardStat.REWARD_CAN){
			return respMsg;
		}
		//可以领取,添加物品
		DiscountReward reward = discount.getRewardList().get(condIndex);
		if(null == reward){
			return respMsg;
		}
		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, reward.getGoodsList(),
				OutputConsumeType.active_discount_award);
		if(!goodsResult.isSuccess()){
			respMsg.setInfo(goodsResult.getInfo());
			return respMsg ;
		}
		// 修改折扣数据库记录
		discountDbInfo.updateRewardCount(condIndex);
		discountDbInfo.setOperateDate(new Date());
		int bindingGoldMoney = reward.getBindMoney();
		int goldMoney = reward.calcRealGainGold(discountDbInfo);
		int silverMoney = reward.getSilverMoney();
		if (bindingGoldMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.bindingGoldMoney, OperatorType.Add,
					bindingGoldMoney, OutputConsumeType.active_discount_award);
		}
		if (goldMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.goldMoney, OperatorType.Add, goldMoney,
					OutputConsumeType.active_discount_award);
		}
		if (silverMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.silverMoney, OperatorType.Add, silverMoney,
					OutputConsumeType.active_discount_award);
		}
		if (bindingGoldMoney > 0 || goldMoney > 0 || silverMoney > 0) {
			role.getBehavior().notifyAttribute();
		}
		respMsg.setStatus(Status.SUCCESS.getInnerCode());
		respMsg.setRewardStatus((byte) discount.getRewardStatus(discountDbInfo,
				condIndex).getType());

		// 只有领取后，没有其他可领取的，才发送
		try {
			if (!GameContext.getActiveDiscountApp().canRecvReward(role)) {
				GameContext.getHintApp().hintChange(role, HintId.Discount,
						false);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}

		return respMsg;
	}

}
