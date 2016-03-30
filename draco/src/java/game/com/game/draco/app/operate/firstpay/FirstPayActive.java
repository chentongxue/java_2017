package com.game.draco.app.operate.firstpay;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.firstpay.config.FirstPayBaseConfig;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.message.response.C2459_FirstPayInfoRespMessage;

public class FirstPayActive implements OperateActive {

	private FirstPayBaseConfig firstPayConfig;

	public FirstPayActive(FirstPayBaseConfig firstPayConfig) {
		this.firstPayConfig = firstPayConfig;
	}

	@Override
	public Message getOperateActiveDetail(RoleInstance role) {
		C2459_FirstPayInfoRespMessage message = new C2459_FirstPayInfoRespMessage();
		message.setStatus(this.getOperateActiveStatus(role));
		message.setGoodsLiteList(GameContext.getFirstPayApp().getFirstPayGift());
		message.setAttriList(GameContext.getFirstPayApp().getFirstPayAttriList());
		message.setShowHero((short) this.firstPayConfig.getShowHero());
		return message;
	}

	@Override
	public int getOperateActiveId() {
		return this.firstPayConfig.getActiveId();
	}

	@Override
	public String getOperateActiveName() {
		return this.firstPayConfig.getActiveName();
	}

	@Override
	public byte getOperateActiveStatus(RoleInstance role) {
		return GameContext.getFirstPayApp().getRoleFirstPayStatus(role);
	}

	@Override
	public OperateActiveType getOperateActiveType() {
		return OperateActiveType.first_pay;
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
		GameContext.getFirstPayApp().onPay(role, pointValue);
	}

	@Override
	public boolean isShow(RoleInstance role) {
		return false;
	}

	@Override
	public boolean hasHint(RoleInstance role) {
		// 首冲不在运营活动菜单中显示
		return false;
	}

	@Override
	public void onLogin(RoleInstance role) {
	}

	@Override
	public void onOpen(RoleInstance role) {
	}

}
