package com.game.draco.app.operate.discount;

import lombok.Getter;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.discount.config.Discount;
import com.game.draco.app.operate.discount.domain.RoleDiscount;
import com.game.draco.app.operate.discount.type.DiscountType;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.app.operate.vo.OperateAwardType;

public class DiscountActive implements OperateActive {

	/**
	 * 折扣活动配置
	 */
	private @Getter Discount discount;

	public DiscountActive(Discount discount) {
		this.discount = discount;
	}

	@Override
	public boolean isOpen(RoleInstance role) {
		return this.discount.isOpen(role);
	}

	@Override
	public boolean isShow(RoleInstance role) {
		return this.discount.canShow(role);
	}

	@Override
	public void onPay(RoleInstance role, int moneyValue, OutputConsumeType outputConsumeType) {
		// 活动必须是充值触发
		if (!this.discount.getDiscountType().isPay()) {
			return;
		}
		// 判断是否符合活动的消费类型
		if (!this.discount.canCount(outputConsumeType)) {
			return;
		}
		// 统计并更新数据库领奖状态
		this.discount.getDiscountTypeLogic().count(role, this.discount, moneyValue);
	}

	@Override
	public void onConsume(RoleInstance role, int moneyValue, OutputConsumeType outputConsumeType) {
		// 活动必须是充值消费类型且不是充值
		if (this.discount.getDiscountType().getType() != DiscountType.TYPE_MONEY || this.discount.getDiscountType().isPay()) {
			return;
		}
		// 判断是否符合活动的消费类型
		if (!this.discount.canCount(outputConsumeType)) {
			return;
		}
		// 统计并更新数据库领奖状态
		this.discount.getDiscountTypeLogic().count(role, this.discount, moneyValue);
	}

	@Override
	public int getOperateActiveId() {
		return this.discount.getActiveId();
	}

	@Override
	public String getOperateActiveName() {
		return this.discount.getActiveName();
	}

	@Override
	public OperateActiveType getOperateActiveType() {
		return OperateActiveType.discount;
	}

	@Override
	public Message getOperateActiveDetail(RoleInstance role) {
		return GameContext.getDiscountApp().buildDiscountDetailMessage(role, discount);
	}

	@Override
	public byte getOperateActiveStatus(RoleInstance role) {
		RoleDiscount info = GameContext.getDiscountApp().getRoleDiscount(role.getRoleId(), this.discount.getActiveId());
		if (null == info) {
			// 如果没有该玩家的信息记录，返回默认状态
			return OperateAwardType.default_receive.getType();
		}
		return this.discount.canReward(role) ? OperateAwardType.can_receive.getType() : OperateAwardType.have_receive.getType();
	}

	@Override
	public boolean hasHint(RoleInstance role) {
		return this.discount.canReward(role);
	}

	@Override
	public void onLogin(RoleInstance role) {
		// 如果活动不是登录触发
		if (this.discount.getDiscountType().getType() != DiscountType.TYPE_LOGIN) {
			return;
		}
		this.discount.getDiscountTypeLogic().count(role, discount, 0);
	}

	@Override
	public void onOpen(RoleInstance role) {
		// 如果活动不是属性类型的
		if (this.discount.getDiscountType().getType() != DiscountType.TYPE_ATTRI) {
			return;
		}
		this.discount.getDiscountTypeLogic().count(role, discount, 0);
	}

}
