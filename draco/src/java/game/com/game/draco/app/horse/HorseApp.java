package com.game.draco.app.horse;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.core.Service;

import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseExchange;
import com.game.draco.app.horse.config.HorseLuckProb;
import com.game.draco.app.horse.config.HorseProp;
import com.game.draco.app.horse.config.HorseSkill;
import com.game.draco.app.horse.config.HorseSkillLimit;
import com.game.draco.app.horse.config.HorseStar;

public interface HorseApp extends Service{

	/**
	 * 坐骑基础数据
	 * @param horseId
	 * @return
	 */
	HorseBase getHorseBaseById(int horseId);
	
	/**
	 * 获得所有坐骑基础数据
	 * @return
	 */
	Map<Integer,HorseBase> getHorseBaseMap();
	
	/**
	 * 坐骑属性加成
	 * @param horseId
	 * @return
	 */
	HorseProp getHorsePropById(String horseId);
	
	/**
	 * 获得坐骑技能数据
	 */
	List<HorseSkill> getHorseSkillList(int horseId);
	
	/**
	 * 获取坐骑星级数据
	 */
	HorseStar getHorseStar(byte quality);
	
	/**
	 * 获得坐骑兑换数据
	 */
	HorseExchange  getHorseExchange(int horseId);
	
	/**
	 * 获得坐骑最高星级
	 */
	byte getHorseHighStar(int horseId,byte quality);
	
	/**
	 * 获得坐骑最高品质
	 */
	byte getHorseHighQuailty(int horseId);
	
	/**
	 * 获得坐骑最低星级
	 */
	byte getHorseLowStar(int horseId,byte quality);
	
	/**
	 * 坐骑幸运值
	 * @return
	 */
	List<HorseLuckProb> getLuckProbList(int schemeId);
	
	/**
	 * 坐骑最大幸运值
	 * @return
	 */
	int getMaxLuckProb(int schemeId);
	
	/**
	 * 技能消耗
	 * @return
	 */
	HorseSkillLimit getSkillLimit(String key);
	
}
