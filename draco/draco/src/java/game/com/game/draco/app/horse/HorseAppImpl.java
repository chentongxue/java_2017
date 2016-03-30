package com.game.draco.app.horse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsHorse;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseAdditionProp;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseExp;
import com.game.draco.app.horse.config.HorseRace;
import com.game.draco.app.horse.config.ManshipAdditionProp;
import com.game.draco.app.horse.config.ManshipConsume;
import com.game.draco.app.horse.config.ManshipDes;
import com.game.draco.app.horse.config.ManshipLevelFilter;

public class HorseAppImpl implements HorseApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//坐骑属性加成 Map<坐骑Id,坐骑对象>
	@Getter @Setter private Map<String,HorseAdditionProp> horseAdditionPropMap = null;
	//坐骑基础数据 Map<坐骑Id,坐骑对象>
	@Getter @Setter private Map<Integer,HorseBase> horseBaseMap = null;
	//坐骑等级经验 Map<等级,经验>
	@Getter @Setter private Map<String,HorseExp> horseExpMap = null;
	//坐骑等级经验 Map<品质,<等级,经验>>
	@Getter @Setter private Map<Byte,Map<Short,Integer>> horseExpListMap = null;
	//坐骑骑术属性加成 Map<骑术等级,骑术对象>
	@Getter @Setter private Map<Integer,ManshipAdditionProp> manshipAdditionPropMap = null;
	//坐骑骑术消耗 Map<骑术等级,骑术对象>
	@Getter @Setter private Map<Integer,ManshipConsume> manshipConsumeMap = null;
	//坐骑骑术等级验证 Map<种族,骑术验证对象>
	@Getter @Setter private Map<String,ManshipLevelFilter> manshipLevelFilterMap = null;
	//坐骑骑术描述 
	@Getter @Setter private List<ManshipDes> manshipDesList = null;
	//坐骑种族骑术数据(种族类型、种族名称、骑术名称)
	@Getter @Setter private Map<Byte,HorseRace> horseRaceMap = null;
	/**
	 * 加载坐骑基础数据
	 */
	private void loadHorseBaseConfig(){
		try{
			String fileName = XlsSheetNameType.horse_base_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_base_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			horseBaseMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HorseBase.class);
			if(horseBaseMap == null || horseBaseMap.isEmpty()){
				Log4jManager.CHECK.error("not config the horseBaseMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadHorseBaseConfig is error",e);
		}
	}
	
	/**
	 * 加载坐骑等级经验数据
	 */
	private void loadHorseExpConfig(){
		try{
			String fileName = XlsSheetNameType.horse_exp_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_exp_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			horseExpMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HorseExp.class);
			if(horseExpMap == null  || horseExpMap.isEmpty()){
				Log4jManager.CHECK.error("not config the horseExpMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			horseExpListMap = new HashMap<Byte,Map<Short,Integer>>();
			for(Entry<String,HorseExp> expMap : horseExpMap.entrySet()){
				if(horseExpListMap.containsKey(expMap.getValue().getQuality())){
					Map<Short,Integer> exp = horseExpListMap.get(expMap.getValue().getQuality());
					exp.put(expMap.getValue().getLevel(), expMap.getValue().getLevel()*expMap.getValue().getExp());
				}else{
					Map<Short,Integer> exp = new HashMap<Short,Integer>();
					exp.put(expMap.getValue().getLevel(), expMap.getValue().getLevel()*expMap.getValue().getExp());
					horseExpListMap.put(expMap.getValue().getQuality(),exp );
				}
				
			}
		}catch(Exception e){
			logger.error("loadHorseExpConfig is error",e);
		}
	}
	
	@Override
	public Map<Short,Integer> getHorseExpMap(byte quality){
		return horseExpListMap.get(quality);
	}
	/**
	 * 加载坐骑属性加成数据
	 */
	private void loadHorseAdditionPropConfig(){
		try{
			String fileName = XlsSheetNameType.horse_addition_prop_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_addition_prop_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			horseAdditionPropMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HorseAdditionProp.class);
			if(horseAdditionPropMap == null || horseAdditionPropMap.isEmpty()){
				Log4jManager.CHECK.error("not config the horseAdditionPropMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadHorseAdditionPropConfig is error",e);
		}
	}
	
	/**
	 * 加载坐骑骑术属性加成数据
	 */
	private void loadManshipAdditionPropConfig(){
		try{
			String fileName = XlsSheetNameType.horse_manship_addition_prop_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_manship_addition_prop_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			manshipAdditionPropMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, ManshipAdditionProp.class);
			if(manshipAdditionPropMap == null || manshipAdditionPropMap.isEmpty()){
				Log4jManager.CHECK.error("not config the manshipAdditionPropMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadManshipAdditionPropConfig is error",e);
		}
	}
	
	/**
	 * 加载坐骑骑术消耗数据
	 */
	private void loadManshipConsumeConfig(){
		try{
			String fileName = XlsSheetNameType.horse_manship_consume_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_manship_consume_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			manshipConsumeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, ManshipConsume.class);
			if(manshipConsumeMap == null || manshipConsumeMap.isEmpty()){
				Log4jManager.CHECK.error("not config the manshipConsumeMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadManshipConsumeConfig is error",e);
		}
	}
	
	/**
	 * 加载坐骑骑术等级过滤数据
	 */
	private void loadManshipLevelFilterConfig(){
		try{
			String fileName = XlsSheetNameType.horse_manship_level_filter_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_manship_level_filter_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			manshipLevelFilterMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, ManshipLevelFilter.class);
			if(manshipLevelFilterMap == null || manshipLevelFilterMap.isEmpty()){
				Log4jManager.CHECK.error("not config the manshipLevelFilterMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadManshipLevelFilterConfig is error",e);
		}
	}
	
	/**
	 * 加载坐骑骑术等级过滤数据
	 */
	private void loadManshipDesConfig(){
		try{
			String fileName = XlsSheetNameType.horse_manship_des_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_manship_des_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			manshipDesList = XlsPojoUtil.sheetToList(sourceFile, sheetName, ManshipDes.class);
			if(manshipDesList == null || manshipDesList.isEmpty()){
				Log4jManager.CHECK.error("not config the manshipDesList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadManshipDesConfig is error",e);
		}
	}
	
	/**
	 * 加载坐骑种族骑术数据
	 */
	private void loadHorseRaceConfig(){
		try{
			String fileName = XlsSheetNameType.horse_race_type_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_race_type_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			horseRaceMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HorseRace.class);
			if(horseRaceMap == null || horseRaceMap.isEmpty()){
				Log4jManager.CHECK.error("not config the horseRaceMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadHorseRaceConfig is error",e);
		}
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		try{
			//加载坐骑基础数据
			loadHorseBaseConfig();
			//加载坐骑等级经验数据
			loadHorseExpConfig();
			//加载坐骑属性加成数据
			loadHorseAdditionPropConfig();
			//加载坐骑骑术属性加成数据
			loadManshipAdditionPropConfig();
			//加载坐骑骑术消耗数据
			loadManshipConsumeConfig();
			//加载坐骑骑术等级过滤数据
			loadManshipLevelFilterConfig();
			//加载坐骑骑术描述
			loadManshipDesConfig();
			//家族坐骑种族数据
			loadHorseRaceConfig();
		}catch(Exception e){
			logger.error("start is error",e);
		}
		
	}

	@Override
	public void stop() {
		
	}

	@Override
	public HorseBase getHorseBaseById(int horseId) {
		return horseBaseMap.get(horseId);
	}

	@Override
	public HorseAdditionProp getHorseAdditionPropById(String horseIdLevel) {
		return horseAdditionPropMap.get(horseIdLevel);
	}

	@Override
	public HorseExp getHorseExpByLevelQuality(String levelQ) {
		return horseExpMap.get(levelQ);
	}

	@Override
	public ManshipAdditionProp getManshipAdditionPropByLevel(int level) {
		return manshipAdditionPropMap.get(level);
	}

	@Override
	public ManshipConsume getManshipConsumeByLevel(int level) {
		return manshipConsumeMap.get(level);
	}

	@Override
	public ManshipLevelFilter getManshipLevelFilterByType(String type) {
		return manshipLevelFilterMap.get(type);
	}

	@Override
	public HorseRace getHorseRaceByType(byte raceType) {
		return horseRaceMap.get(raceType);
	}

	@Override
	public Result useHorseGoods(RoleInstance role, RoleGoods roleGoods) throws ServiceException{
		try {
			Result result = new Result();
			GoodsHorse goodsHorse = GameContext.getGoodsApp().getGoodsTemplate(
					GoodsHorse.class, roleGoods.getGoodsId());
			if (null == goodsHorse) {
				result.setInfo(GameContext.getI18n().getText(
						TextId.ERROR_INPUT));
				return result;
			}
			GoodsResult gr = GameContext.getUserGoodsApp()
					.deleteForBagByInstanceId(role, roleGoods.getId(), 1,
							OutputConsumeType.horse_goods_use);
			if (!gr.isSuccess()) {
				return gr;
			}
			return this.useHorseTemplate(role, goodsHorse);
		}catch(Exception ex){
			throw new ServiceException("useHorseGoods error",ex) ;
		}
	}

	@Override
	public Result useHorseTemplate(RoleInstance role, GoodsHorse goodsHorse) throws ServiceException{
		try {
			GameContext.getRoleHorseApp().addRoleHorse(role,goodsHorse.getHorseId());
			return new Result().success();
		}catch(Exception ex){
			throw new ServiceException("useHorseTemplate error",ex) ;
		}
	}
	
}
