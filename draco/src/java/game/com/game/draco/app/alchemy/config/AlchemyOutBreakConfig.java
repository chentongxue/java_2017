package com.game.draco.app.alchemy.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
/**
 * 炼金暴击配置信息alchemy.xls->alchemy_stricke_config
 * countNumber是连续第几次，假如第10次已经暴击，则滴11次算作第一次
 * @author gaibaoning@moogame.cn
 * @date 2014-4-3 下午02:40:46
 */
public @Data
class AlchemyOutBreakConfig implements KeySupport<String>{
	private int countNumber; // 连续第几次
	private int outBreakPercentage;// 实际暴击概率百分点
	private int outBreakPercentageShow;// 客户端显示暴击概率百分点
	private int diamandsConsume; // 使用钻石

	@Override
	public String getKey(){
		return String.valueOf(countNumber);
	}
}
