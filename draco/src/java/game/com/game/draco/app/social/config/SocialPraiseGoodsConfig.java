package com.game.draco.app.social.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

public @Data class SocialPraiseGoodsConfig implements KeySupport<Integer> {
	private int level;	//角色等级
	private int goodsId;	//奖励物品
	private int goodsNum;	//奖励物品数量
	private short bindType;	//绑定类型
	
	
	public void init(String fileInfo) {
		String info = fileInfo + this.level + ".";
		if (this.level <= 0) {
			this.checkFail(info + "level is config error!");
		}
		if (this.goodsId <= 0) {
			this.checkFail(info + "goodsId is config error!");
		}
		if (this.goodsNum <= 0) {
			this.checkFail(info + "goodsNum is config error!");
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	@Override
	public Integer getKey() {
		return this.level;
	}
}
