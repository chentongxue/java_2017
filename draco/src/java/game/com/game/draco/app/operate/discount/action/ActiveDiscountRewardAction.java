package com.game.draco.app.operate.discount.action;

import java.util.Date;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.config.DiscountReward;
import com.game.draco.app.operate.discount.domain.RoleDiscount;
import com.game.draco.app.operate.discount.type.DiscountRewardStat;
import com.game.draco.app.operate.vo.OperateAwardType;
import com.game.draco.message.request.C2317_ActiveDiscountRewardReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C2317_ActiveDiscountRewardRespMessage;

public class ActiveDiscountRewardAction extends BaseAction<C2317_ActiveDiscountRewardReqMessage> {

	@Override
	public Message execute(ActionContext context, C2317_ActiveDiscountRewardReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		Discount discount = GameContext.getDiscountApp().getDiscount(reqMsg.getId());
		if (null == discount) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		// 如果是不可领取状态直接返回
		int condIndex = reqMsg.getIndex();
		RoleDiscount roleDiscount = GameContext.getDiscountApp().getRoleDiscount(role.getRoleId(), discount.getActiveId());
		if (discount.getRewardStatus(roleDiscount, condIndex) != DiscountRewardStat.REWARD_CAN) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return message;
		}
		DiscountReward reward = discount.getRewardList().get(condIndex);
		if (null == reward) {
			C0002_ErrorRespMessage message = new C0002_ErrorRespMessage();
			message.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return message;
		}
		// 可以领取,添加物品
		C2317_ActiveDiscountRewardRespMessage resp = new C2317_ActiveDiscountRewardRespMessage();
		GoodsResult goodsResult = GameContext.getUserGoodsApp().addGoodsBeanForBag(role, reward.getGoodsList(), OutputConsumeType.active_discount_award);
		if (!goodsResult.isSuccess()) {
			resp.setInfo(goodsResult.getInfo());
			return resp;
		}
		// 修改折扣数据库记录
		roleDiscount.updateRewardCount(condIndex);
		roleDiscount.setUpdateDB(true);
		roleDiscount.setOperateDate(new Date());
		int goldMoney = reward.calcRealGainGold(roleDiscount);
		int silverMoney = reward.getSilverMoney();
		if (goldMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Add, goldMoney, OutputConsumeType.active_discount_award);
		}
		if (silverMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Add, silverMoney, OutputConsumeType.active_discount_award);
		}
		if (goldMoney > 0 || silverMoney > 0) {
			role.getBehavior().notifyAttribute();
		}
		resp.setStatus(Status.SUCCESS.getInnerCode());
		resp.setRewardStatus((byte) discount.getRewardStatus(roleDiscount, condIndex).getType());
		try {
			if (!discount.canReward(role)) {
				GameContext.getOperateActiveApp().pushHintChange(role, discount.getActiveId(), OperateAwardType.have_receive.getType());
			}
			// 只有领取后，没有其他可领取的，才发送
			if (!GameContext.getOperateActiveApp().hasHint(role)) {
				GameContext.getHintApp().hintChange(role, HintType.operate, false);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
		resp.setId(discount.getActiveId());
		resp.setIndex((byte) condIndex);
		return resp;
	}

}
