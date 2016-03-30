package com.game.draco.app.social.config;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;

public @Data class SocialIntimateConfig {
	
	private int level;// 亲密度等级
	private int minIntimate;// 最小亲密度
	private int maxIntimate;// 最大亲密度
	private int buffId;
	private int buffLevel;
	private byte buffAddition;// Buff加成（100 = 100%）

	public void init(String fileInfo) {
		String info = fileInfo + ": level=" + this.level + ".";
		if (this.level < 0) {
			this.checkFail(info + "level is config error.");
		}
		if (0 == this.level && 0 != this.minIntimate) {
			this.checkFail(info + "level is 0, the minIntimate must be 0");
		}
		if (this.minIntimate < 0 || this.maxIntimate < 0 || this.minIntimate > this.maxIntimate) {
			this.checkFail(info + "minIntimate or maxIntimate is config error.");
		}
		if (buffId > 0 && null == GameContext.getBuffApp().getBuffDetail((short) buffId, buffLevel)) {
			this.checkFail(info + "buff config error!");
		}
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	/**
	 * 亲密度是否在区间之内
	 * 
	 * @param intimate
	 * @return
	 */
	public boolean isWithin(int intimate) {
		return intimate >= this.minIntimate && intimate <= this.maxIntimate;
	}

}
