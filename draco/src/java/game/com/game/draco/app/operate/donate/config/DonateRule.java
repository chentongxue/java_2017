package com.game.draco.app.operate.donate.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.util.KeySupport;

import com.game.draco.app.operate.donate.DonateApp;
import com.game.draco.app.operate.donate.domain.RoleDonate;
import com.game.draco.message.item.ActiveDonateRuleItem;

public @Data class DonateRule implements KeySupport<Integer>{
	private int ruleId;// 条件id
	private int condValue1;// 档位1
	private int awardId1;// 奖励1
	private int condValue2;// 档位2
	private int awardId2;// 奖励2
	private int condValue3;// 档位3
	private int awardId3;// 奖励3
	private int condValue4;// 档位4
	private int awardId4;// 奖励4
	private int condValue5;// 档位5
	private int awardId5;// 奖励5
	
	private Map<Integer, DonateWorldReward> condAwardMap = new TreeMap<Integer, DonateWorldReward>();
	private Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
	@Override
	public Integer getKey() {
		return this.ruleId;
	}
	
	public Result init(Map<Integer, DonateWorldReward> allRewardMap) {
		Result result = this.init(condValue1, awardId1, allRewardMap);
		if(!result.isSuccess()){
			return result;
		}
		indexMap.put(condValue1, 0);
		
		result = this.init(condValue2, awardId2, allRewardMap);
		if(!result.isSuccess()){
			return result;
		}
		indexMap.put(condValue2, 1);
		
		result = this.init(condValue3, awardId3, allRewardMap);
		if(!result.isSuccess()){
			return result;
		}
		indexMap.put(condValue3, 2);
		
		result = this.init(condValue4, awardId4, allRewardMap);
		if(!result.isSuccess()){
			return result;
		}
		indexMap.put(condValue4, 3);
		
		result = this.init(condValue5, awardId5, allRewardMap);
		indexMap.put(condValue5, 4);
		return result;
	}
	
	private Result init(int condValue, int awardId, Map<Integer, DonateWorldReward> allRewardMap) {
		Result result = new Result();
		result.failure();
		if(condValue <= 0 && awardId <= 0){
			result.success();
			return result;
		}
		if(condValue <= 0 || condAwardMap.containsKey(condValue)) {
			result.setInfo("WorldDonateRule ruleId=" + this.ruleId + ", condValue == 0 or has duplicate!");
			return result;
		}
		DonateWorldReward reward = allRewardMap.get(awardId);
		if(null == reward) {
			result.setInfo("WorldDonateRule ruleId=" + this.ruleId + "condValue= " + condValue
					+ ", awardId=" + awardId + "do not exsit");
			return result;
		}
		condAwardMap.put(condValue, reward);
		result.success();
		return result;
	}
	
	public byte getCondReward(int condValue, int curCount, RoleDonate roleDonate) {
		//未参加活动
		if(null == roleDonate) {
			return DonateApp.REWARD_STATE_NO;
		}
		if(condValue > curCount) {
			return DonateApp.REWARD_STATE_NO;
		}
		
		Integer index = indexMap.get(condValue);
		if(null == index) {
			return DonateApp.REWARD_STATE_NO;
		}
		int result = (roleDonate.getWorldReward() & (1 << index));
		if(result == 0){
			return DonateApp.REWARD_STATE_ENABLE;
		}
		return DonateApp.REWARD_STATE_REWARDED;
	}
	
	/**
	 * 修改数据库中全民领奖字段
	 * @param condValue
	 * @param roleDonate
	 */
	public void updateRoleDonateWorldAward(int condValue, RoleDonate roleDonate) {
		Integer index = indexMap.get(condValue);
		if(null == index) {
			return ;
		}
		int result = (roleDonate.getWorldReward() | (1 << index));
		roleDonate.setWorldReward(result);
	}
	
	public List<ActiveDonateRuleItem> getDonateRuleItemList(int curCount, RoleDonate roleDonate) {
		List<ActiveDonateRuleItem> itemList = new ArrayList<ActiveDonateRuleItem>();
		for(Entry<Integer, DonateWorldReward> entry : condAwardMap.entrySet()) {
			ActiveDonateRuleItem item = new ActiveDonateRuleItem();
			int condValue = entry.getKey();
			DonateWorldReward reward = entry.getValue();
			item.setCondValue(condValue);
			item.setState(this.getCondReward(condValue, curCount, roleDonate));
			item.setAwardName(reward.getName());
			item.setAwardGoodsList(reward.getGoodsLiteNamedList());
			itemList.add(item);
		}
		return itemList;
	}
}
