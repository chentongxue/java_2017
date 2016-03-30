package com.game.draco.app.operate.payextra;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.payextra.config.PayExtraBaseConfig;
import com.game.draco.app.operate.vo.OperateActiveType;

public class PayExtraActive implements OperateActive {
	
	private PayExtraBaseConfig payExtraBaseConfig;
	
	public PayExtraActive(PayExtraBaseConfig payExtraBaseConfig) {
		this.payExtraBaseConfig = payExtraBaseConfig;
	}

	@Override
	public Message getOperateActiveDetail(RoleInstance role) {
		return null;
	}

	@Override
	public int getOperateActiveId() {
		return this.payExtraBaseConfig.getActiveId();
	}

	@Override
	public String getOperateActiveName() {
		return this.payExtraBaseConfig.getActiveName();
	}

	@Override
	public byte getOperateActiveStatus(RoleInstance role) {
		return 0;
	}

	@Override
	public OperateActiveType getOperateActiveType() {
		return OperateActiveType.pay_extra;
	}

	@Override
	public boolean isOpen(RoleInstance role) {
		return true;
	}

	@Override
	public boolean isShow(RoleInstance role) {
		return false;
	}

	@Override
	public void onConsume(RoleInstance role, int moneyValue, OutputConsumeType outputConsumeType) {
	}

	@Override
	public void onPay(RoleInstance role, int moneyValue, OutputConsumeType outputConsumeType) {
		GameContext.getPayExtraApp().onPay(role, moneyValue);
	}

	@Override
	public boolean hasHint(RoleInstance role) {
		return false;
	}

	@Override
	public void onLogin(RoleInstance role) {
	}

	@Override
	public void onOpen(RoleInstance role) {
	}

}
