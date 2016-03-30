package com.game.draco.app.operate.donate;

import lombok.Data;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.donate.config.DonateInfo;
import com.game.draco.app.operate.vo.OperateActiveType;

public @Data
class DonateActive implements OperateActive {

	private DonateInfo donateInfo;

	public DonateActive(DonateInfo donateInfo) {
		this.donateInfo = donateInfo;
	}

	@Override
	public boolean isOpen(RoleInstance role) {
		return donateInfo.isInDate();
	}

	@Override
	public void onPay(RoleInstance role, int rmbMoneyValue, OutputConsumeType outputConsumeType) {
	}

	@Override
	public void onConsume(RoleInstance role, int rmbMoneyValue, OutputConsumeType outputConsumeType) {
	}

	@Override
	public int getOperateActiveId() {
		return this.donateInfo.getId();
	}

	@Override
	public String getOperateActiveName() {
		return this.donateInfo.getName();
	}

	@Override
	public OperateActiveType getOperateActiveType() {
		return OperateActiveType.donate;
	}

	@Override
	public Message getOperateActiveDetail(RoleInstance role) {
		return GameContext.getDonateApp().createDonateDetailMsg(role, this.getOperateActiveId());
	}

	@Override
	public byte getOperateActiveStatus(RoleInstance role) {
		return 0;
	}

	@Override
	public boolean isShow(RoleInstance role) {
		return GameContext.getDonateApp().isOpen(this.getOperateActiveId());
	}

	@Override
	public boolean hasHint(RoleInstance role) {
		return GameContext.getDonateApp().canRecvReward(role);
	}

	@Override
	public void onLogin(RoleInstance role) {
	}

	@Override
	public void onOpen(RoleInstance role) {
	}

}
