package com.game.draco.app.operate.growfund;

import lombok.Getter;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.operate.OperateActive;
import com.game.draco.app.operate.growfund.config.GrowFundBaseConfig;
import com.game.draco.app.operate.growfund.domain.RoleGrowFund;
import com.game.draco.app.operate.vo.OperateActiveType;
import com.game.draco.app.operate.vo.OperateAwardType;
import com.game.draco.message.response.C2462_GrowFundInfoRespMessage;

public class GrowFundActive implements OperateActive {
	
	private @Getter GrowFundBaseConfig growFundBaseConfig;
	
	public GrowFundActive(GrowFundBaseConfig growFundBaseConfig) {
		this.growFundBaseConfig = growFundBaseConfig;
	}

	@Override
	public Message getOperateActiveDetail(RoleInstance role) {
		C2462_GrowFundInfoRespMessage resp = new C2462_GrowFundInfoRespMessage();
		resp.setType(this.getOperateActiveStatus(role));
		resp.setActiveDesc(this.growFundBaseConfig.getActiveDesc());
		resp.setRewardPoint(this.growFundBaseConfig.getRechargePoint());// 返回即为充值钻石数量
		resp.setOperateGrowFundList(GameContext.getGrowFundApp().getOperateGrowFundList(role));
		resp.setActiveId(this.growFundBaseConfig.getActiveId());
		return resp;
	}
	
	@Override
	public int getOperateActiveId() {
		return this.growFundBaseConfig.getActiveId();
	}

	@Override
	public String getOperateActiveName() {
		return this.growFundBaseConfig.getActiveName();
	}

	@Override
	public byte getOperateActiveStatus(RoleInstance role) {
		RoleGrowFund roleGrowFund = GameContext.getGrowFundApp().getRoleGrowFund(role.getRoleId());
		if (null == roleGrowFund) {
			return OperateAwardType.default_receive.getType();// 未开启成长基金
		}
		if (roleGrowFund.isRewardAll()) {
			return OperateAwardType.have_receive.getType();// 已领取全部成长基金
		}
		return OperateAwardType.can_receive.getType();// 已开启成长基金
	}

	@Override
	public OperateActiveType getOperateActiveType() {
		return OperateActiveType.grow_fund;
	}

	@Override
	public boolean isOpen(RoleInstance role) {
		return true;
	}

	@Override
	public boolean isShow(RoleInstance role) {
		return this.getOperateActiveStatus(role) != OperateAwardType.have_receive.getType();
	}

	@Override
	public void onConsume(RoleInstance role, int pointValue, OutputConsumeType outputConsumeType) {
	}

	@Override
	public void onPay(RoleInstance role, int pointValue, OutputConsumeType outputConsumeType) {
		GameContext.getGrowFundApp().onPay(role, pointValue);
	}

	@Override
	public boolean hasHint(RoleInstance role) {
		return GameContext.getGrowFundApp().haveReward(role);
	}

	@Override
	public void onLogin(RoleInstance role) {
	}

	@Override
	public void onOpen(RoleInstance role) {
	}

}
