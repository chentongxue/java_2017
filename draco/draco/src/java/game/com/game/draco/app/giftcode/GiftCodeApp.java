package com.game.draco.app.giftcode;

import java.util.Collection;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.giftcode.config.GiftCodeConfig;

public interface GiftCodeApp extends Service{
	
	
	public Collection<GiftCodeConfig> getAllGiftCodeConfig();
	
	/**
	 * 领取激活码礼包
	 * @param role
	 * @param codeNumber 激活码
	 * @return
	 */
	public Result takeCdkey(RoleInstance role,String codeNumber);
	
}
