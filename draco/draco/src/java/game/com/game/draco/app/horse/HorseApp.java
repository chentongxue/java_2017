package com.game.draco.app.horse;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsHorse;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.horse.config.HorseAdditionProp;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseExp;
import com.game.draco.app.horse.config.HorseRace;
import com.game.draco.app.horse.config.ManshipAdditionProp;
import com.game.draco.app.horse.config.ManshipConsume;
import com.game.draco.app.horse.config.ManshipDes;
import com.game.draco.app.horse.config.ManshipLevelFilter;

public interface HorseApp extends Service{

	/**
	 * 坐骑基础数据
	 * @param horseId
	 * @return
	 */
	HorseBase getHorseBaseById(int horseId);
	
	/**
	 * 坐骑属性加成
	 * @param horseId
	 * @return
	 */
	HorseAdditionProp getHorseAdditionPropById(String horseId);
	
	/**
	 * 坐骑等级经验
	 * @param level
	 * @return
	 */
	HorseExp getHorseExpByLevelQuality(String levelQ);
	
	/**
	 * 坐骑等级经验列表
	 * @param level
	 * @return
	 */
	Map<Short,Integer> getHorseExpMap(byte quality);
	
	/**
	 * 坐骑骑术属性加成
	 * @param level
	 * @return
	 */
	ManshipAdditionProp getManshipAdditionPropByLevel(int level);
	
	/**
	 * 坐骑骑术消耗
	 * @param level
	 * @return
	 */
	ManshipConsume getManshipConsumeByLevel(int level);
	
	/**
	 * 坐骑骑术等级限制
	 * @param type
	 * @return
	 */
	ManshipLevelFilter getManshipLevelFilterByType(String type);
	
	/**
	 * 坐骑骑术描述
	 * @return
	 */
	List<ManshipDes> getManshipDesList();
	
	/**
	 * 坐骑种族骑术数据(种族类型、种族名称、骑术名称)
	 * @param horseId
	 * @return
	 */
	HorseRace getHorseRaceByType(byte raceType);
	
	/**
	 * 使用物品获得坐骑
	 * @throws ServiceException 
	 */
	Result useHorseGoods(RoleInstance role,RoleGoods roleGoods) throws ServiceException;
	
	Result useHorseTemplate(RoleInstance role, GoodsHorse goodsHorse)throws ServiceException;
	
}
