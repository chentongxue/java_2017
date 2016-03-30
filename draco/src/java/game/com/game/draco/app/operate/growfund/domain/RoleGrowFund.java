package com.game.draco.app.operate.growfund.domain;

import java.util.Set;

import lombok.Data;

import sacred.alliance.magic.util.Util;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.game.draco.GameContext;
import com.game.draco.app.operate.domain.RoleOperateActive;
import com.google.common.collect.Sets;

public @Data class RoleGrowFund extends RoleOperateActive {
	
	@Protobuf(fieldType = FieldType.INT32, order = 10)
	private Set<Integer> rewardLevelSet = Sets.newHashSet();
	
	/**
	 * 创建成长基金数据
	 * @return
	 */
	public static RoleGrowFund createRoleGrowFund() {
		RoleGrowFund fund = new RoleGrowFund();
		fund.setInsertDB(true);
		return fund;
	}
	
	/**
	 * 是否已领取档位奖励
	 * @param level
	 * @return
	 */
	public boolean isReward(int level) {
		if (Util.isEmpty(this.rewardLevelSet)) {
			return false;
		}
		return this.rewardLevelSet.contains(level);
	}
	
	/**
	 * 是否已领取所有的档位奖励
	 * @return
	 */
	public boolean isRewardAll() {
		if (Util.isEmpty(this.rewardLevelSet)) {
			return false;
		}
		return this.rewardLevelSet.size() >= GameContext.getGrowFundApp().getStageCount();
	}
	
	/**
	 * 领取档位奖励
	 * @param level
	 */
	public void reward(int level) {
		this.rewardLevelSet.add(level);
		this.setUpdateDB(true);
	}

}
