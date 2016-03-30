package com.game.draco.app.choicecard;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.hint.HintTimeSupport;
import com.game.draco.message.response.C2810_CardGoldRespMessage;
import com.game.draco.message.response.C2811_CardGemRespMessage;
import com.game.draco.message.response.C2812_CardActivityRespMessage;
import com.game.draco.message.response.C2809_CardPreviewRespMessage;


public interface RoleChoiceCardApp extends AppSupport,HintTimeSupport{
	
	/**
	 * 抽卡
	 * @param type
	 * @return
	 */
	Result choiceCard(RoleInstance role,byte funType,byte specificType);
	
	/**
	 * 抽卡检查
	 * @param roleId
	 * @param type
	 * @return
	 */
	Result isChoiceCondition(RoleInstance role,byte funType,byte specificType);
	
	/**
	 * 金币抽卡
	 * @param role
	 * @return
	 */
	C2810_CardGoldRespMessage sendC2810_GoldCardRespMessage(RoleInstance role);
	
	/**
	 * 钻石抽卡
	 * @param role
	 * @return
	 */
	C2811_CardGemRespMessage sendC2811_GemCardRespMessage(RoleInstance role);
	
	/**
	 * 活动抽卡
	 * @param role
	 * @return
	 */
	C2812_CardActivityRespMessage sendC2812_ActivityCardRespMessage(RoleInstance role);
	
	/**
	 * 抽卡预览
	 */
	C2809_CardPreviewRespMessage sendC2820_CardPreviewRespMessage(byte type);

}
