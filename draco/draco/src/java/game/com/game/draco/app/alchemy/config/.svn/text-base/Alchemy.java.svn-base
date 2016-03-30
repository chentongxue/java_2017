package com.game.draco.app.alchemy.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
/**
 * 炼金
 * @author gaibaoning@moogame.cn
 * @date 2014-4-3 下午01:32:14
 */
public @Data
class Alchemy implements KeySupport<String>{

	private byte rewardType;// 5为点石成金得到金币，14为点化潜能获得升级
	private short buttonImageId;// 按钮图形资源ID
	
	@Override
	public String getKey(){
		return String.valueOf(rewardType);
	}

}
