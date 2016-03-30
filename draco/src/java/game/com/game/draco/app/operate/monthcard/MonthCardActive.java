package com.game.draco.app.operate.monthcard;

import lombok.Data;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.monthcard.config.MonthCardConfig;
import com.game.draco.app.operate.monthcard.domain.RoleMonthCard;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.app.operate.vo.OperateAwardType;
import com.game.draco.message.response.C2456_MonthCardInfoRespMessage;

public @Data class MonthCardActive implements OperateActive {
	
	private MonthCardConfig monthCardConfig;
	
	public MonthCardActive(MonthCardConfig monthCardConfig) {
		this.monthCardConfig = monthCardConfig;
	}

	@Override
	public int getOperateActiveId() {
		return this.monthCardConfig.getActiveId();
	}

	@Override
	public String getOperateActiveName() {
		return this.monthCardConfig.getActiveName();
	}

	@Override
	public OperateActiveType getOperateActiveType() {
		return OperateActiveType.month_card;
	}

	@Override
	public boolean isOpen(RoleInstance role) {
		return true;
	}

	@Override
	public void onConsume(RoleInstance role, int rmbMoneyValue, OutputConsumeType outputConsumeType) {
	}

	@Override
	public void onPay(RoleInstance role, int pointValue, OutputConsumeType outputConsumeType) {
		GameContext.getMonthCardApp().onPay(role, pointValue);
	}

	@Override
	public Message getOperateActiveDetail(RoleInstance role) {
		if (null == role) {
			return null;
		}
		C2456_MonthCardInfoRespMessage message = new C2456_MonthCardInfoRespMessage();
		message.setDesc(GameContext.getMonthCardApp().getRoleMonthCardDesc());
		MonthCardConfig config = GameContext.getMonthCardApp().getMonthCardConfig();
		message.setRechargeMoney(config.getRechargeMoney());
		message.setMaxRewardPoint(config.getRewardPoint() * MonthCardAppImpl.EFFECTIVE_TIME + config.getRechargePoint());
		RoleMonthCard roleMonthCard = GameContext.getMonthCardApp().getRoleMonthCard(role);
		if (null == roleMonthCard) {
			message.setType(OperateAwardType.default_receive.getType());
			return message;
		}
		message.setType(roleMonthCard.getReceiveAwardsType());
		message.setBeginTime(roleMonthCard.getStrBeginDate());
		message.setEndTime(roleMonthCard.getStrEndDate());
		message.setRewardPoint(config.getRewardPoint());
		message.setActiveId(this.monthCardConfig.getActiveId());
		return message;
	}
	
	@Override
	public byte getOperateActiveStatus(RoleInstance role) {
		RoleMonthCard roleMonthCard = GameContext.getMonthCardApp().getRoleMonthCard(role);
		if (null == roleMonthCard) {
			return OperateAwardType.default_receive.getType();
		}
		return roleMonthCard.getReceiveAwardsType();
	}

	@Override
	public boolean isShow(RoleInstance role) {
		return true;
	}

	@Override
	public boolean hasHint(RoleInstance role) {
		return this.getOperateActiveStatus(role) == OperateAwardType.can_receive.getType();
	}

	@Override
	public void onLogin(RoleInstance role) {
	}

	@Override
	public void onOpen(RoleInstance role) {
	}
	
}
