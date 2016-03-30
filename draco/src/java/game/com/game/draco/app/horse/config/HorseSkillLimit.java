package com.game.draco.app.horse.config;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

/**
 * 坐骑基础数据
 * @author zhouhaobing
 *
 */
public @Data class HorseSkillLimit implements KeySupport<String>{

	//技能ID
	private short skillId;
	//技能等级
	private int level;
	//金币
	private  int silverMoney;
	//物品ID
	private int goodsId;
	//物品数量
	private short goodsNum;
	//品质
	private byte quality;
	//星级
	private byte star;
	//幸运值方案ID
	private int schemeId;
	//随机最小幸运值
	private int randMinLuck;
	//随机最大幸运值
	private int randMaxLuck;
	
	@Override
	public String getKey() {
		return getSkillId() + Cat.underline + getLevel();
	}

	
}
