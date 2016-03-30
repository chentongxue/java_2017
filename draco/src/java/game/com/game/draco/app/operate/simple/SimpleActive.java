package com.game.draco.app.operate.simple;

import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.simple.config.SimpleActiveConfig;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.message.response.C2465_SimpleActiveInfoRespMessage;

public class SimpleActive implements OperateActive {
	private SimpleActiveConfig simpleActiveConfig;
	
	public SimpleActive(SimpleActiveConfig simpleActiveConfig) {
		this.simpleActiveConfig = simpleActiveConfig;
	}

	@Override
	public Message getOperateActiveDetail(RoleInstance role) {
		C2465_SimpleActiveInfoRespMessage message = new C2465_SimpleActiveInfoRespMessage();
		message.setActiveDesc(this.simpleActiveConfig.getActiveDesc());
		message.setActiveTitle(this.simpleActiveConfig.getActiveTitle());
		return message;
	}

	@Override
	public int getOperateActiveId() {
		return this.simpleActiveConfig.getActiveId();
	}

	@Override
	public String getOperateActiveName() {
		return this.simpleActiveConfig.getActiveName();
	}

	@Override
	public byte getOperateActiveStatus(RoleInstance role) {
		return 0;
	}

	@Override
	public OperateActiveType getOperateActiveType() {
		return OperateActiveType.simple;
	}

	@Override
	public boolean isOpen(RoleInstance role) {
		return false;
	}

	@Override
	public boolean isShow(RoleInstance role) {
		return this.simpleActiveConfig.isShow();
	}

	@Override
	public void onConsume(RoleInstance role, int moneyValue, OutputConsumeType outputConsumeType) {
	}

	@Override
	public void onPay(RoleInstance role, int moneyValue, OutputConsumeType outputConsumeType) {
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
