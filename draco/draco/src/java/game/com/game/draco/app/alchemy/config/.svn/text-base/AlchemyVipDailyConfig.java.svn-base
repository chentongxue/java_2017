package com.game.draco.app.alchemy.config;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

/**
 * VIP对应的每日炼金次数 alchemy.xls->vip_daily_config
 * @author gaibaoning@moogame.cn
 * @date 2014-4-3 下午02:51:41
 */
public @Data class AlchemyVipDailyConfig implements KeySupport<String>{
	
	private byte vipLevel;//对应的VIP等级
	private byte rewardType;//5为点石成金得到金币，14为点化潜能获得升级
	private short timesLimit;//每日限制次数
	
	
	@Override
	public String getKey(){
		return vipLevel + Cat.underline + rewardType ;
	}
	
}
