package com.game.draco.app.levelgift;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface LevelGiftApp extends Service{
	
	/**
	 * 冲级活动列表消息
	 * @return
	 */
	public Message getLevelGiftListMessage(RoleInstance role);
	
	/**
	 * 领奖
	 * @param role 角色
	 * @param level 所领取奖励的等级
	 * @return
	 */
	public Result takeReward(RoleInstance role,int level);
	
}
