package com.game.draco.app.horse;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.python.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseExchange;
import com.game.draco.app.horse.config.HorseLuckProb;
import com.game.draco.app.horse.config.HorseProp;
import com.game.draco.app.horse.config.HorseSkill;
import com.game.draco.app.horse.config.HorseSkillLimit;
import com.game.draco.app.horse.config.HorseStar;

public class HorseAppImpl implements HorseApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//坐骑属性加成 Map<坐骑Id,坐骑对象>
	@Getter @Setter private Map<String,HorseProp> horsePropMap = null;
	//坐骑基础数据 Map<坐骑Id,坐骑对象>
	@Getter @Setter private Map<Integer,HorseBase> horseBaseMap = null;
	//坐骑技能数据
	@Getter @Setter private Map<Integer,List<HorseSkill>> horseSkillMap = null;
	//坐骑星级数据
	@Getter @Setter private Map<Byte,HorseStar> horseStarMap = null;
	//坐骑兑换数据
	@Getter @Setter private Map<Integer,HorseExchange> horseExchangeMap = null;
	//坐骑技能升级数据
	@Getter @Setter private Map<String,HorseSkillLimit> skillLimitMap = null;
	//坐骑幸运值概率数据
	@Getter @Setter private Map<Integer,List<HorseLuckProb>> luckProbMap = null;
	//坐骑品质星级数据
	@Getter @Setter private Map<Integer,Map<Byte,Set<Byte>>> qualityStarMap = null;
	
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
	 * 加载坐骑属性加成数据
	 */
	private void loadHorsePropConfig(){
		try{
			String fileName = XlsSheetNameType.horse_addition_prop_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_addition_prop_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			horsePropMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HorseProp.class);
			if(horsePropMap == null || horsePropMap.isEmpty()){
				Log4jManager.CHECK.error("not config the HorsePropMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				qualityStarMap = Maps.newHashMap();
				for(Entry<String,HorseProp> horseProp : horsePropMap.entrySet()){
					HorseProp prop = horseProp.getValue();
					Map<Byte,Set<Byte>> qualityMap = null;
					Set<Byte> starSet = null;
					if(qualityStarMap.containsKey(prop.getHorseId())){
						qualityMap = qualityStarMap.get(prop.getHorseId());
						if(qualityMap.containsKey(prop.getQuality())){
							starSet = qualityMap.get(prop.getQuality());
							starSet.add(prop.getStar());
						}else{
							starSet = Sets.newHashSet();
							starSet.add(prop.getStar());
							qualityMap.put(prop.getQuality(), starSet);
						}
					}else{
						qualityMap = Maps.newHashMap();
						starSet = Sets.newHashSet();
						starSet.add(prop.getStar());
						qualityMap.put(prop.getQuality(), starSet);
						qualityStarMap.put(prop.getHorseId(), qualityMap);
					}
				}
			}
		}catch(Exception e){
			logger.error("loadHorsePropConfig is error",e);
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
			//加载坐骑属性加成数据
			loadHorsePropConfig();
			//加载坐骑技能数据
			loadHorseSkillConfig();
			//加载坐骑兑换数据
			loadHorseExChangeConfig();
			//加载坐骑星级数据
			loadHorseStarConfig();
			//加载坐骑技能限制数据
			loadHorseSkillLimitConfig();
			 //加载坐骑幸运值概率数据
			loadHorseLuckProbConfig();
			
		}catch(Exception e){
			logger.error("start is error",e);
		}
		
	}

	/**
	 * 加载坐骑星级数据
	 */
	private void loadHorseStarConfig() {
		try{
			String fileName = XlsSheetNameType.horse_star_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_star_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			horseStarMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HorseStar.class);
			if(horseStarMap == null || horseStarMap.isEmpty()){
				Log4jManager.CHECK.error("not config the horseStarMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadHorseStarConfig is error",e);
		}
	}

	/**
	 * 加载坐骑兑换数据
	 */
	private void loadHorseExChangeConfig() {
		try{
			String fileName = XlsSheetNameType.horse_exchange_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_exchange_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			horseExchangeMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HorseExchange.class);
			if(horseExchangeMap == null || horseExchangeMap.isEmpty()){
				Log4jManager.CHECK.error("not config the horseExchangeList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadHorseExChangeConfig is error",e);
		}
	}

	/**
	 * 加载坐骑技能
	 */
	private void loadHorseSkillConfig() {
		try{
			String fileName = XlsSheetNameType.horse_skill_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_skill_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<HorseSkill> skillList = XlsPojoUtil.sheetToList(sourceFile, sheetName, HorseSkill.class);
			if(skillList == null || skillList.isEmpty()){
				Log4jManager.CHECK.error("not config the horseSkillMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				horseSkillMap = Maps.newHashMap();
				for(HorseSkill skill : skillList){
					if(horseSkillMap.containsKey(skill.getHorseId())){
						List<HorseSkill> list = horseSkillMap.get(skill.getHorseId());
						list.add(skill);
					}else{
						List<HorseSkill> list = Lists.newArrayList();
						list.add(skill);
						horseSkillMap.put(skill.getHorseId(), list);
					}
				}
			}
		}catch(Exception e){
			logger.error("loadHorseSkillConfig is error",e);
		}
	}
	
	/**
	 * 加载坐骑技能限制数据
	 */
	private void loadHorseSkillLimitConfig() {
		try{
			String fileName = XlsSheetNameType.horse_skill_limit_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_skill_limit_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			skillLimitMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, HorseSkillLimit.class);
			if(Util.isEmpty(skillLimitMap)){
				Log4jManager.CHECK.error("not config the skillLimitMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadHorseSkillLimitConfig is error",e);
		}
	}
	
	/**
	 * 加载坐骑幸运值概率数据
	 */
	private void loadHorseLuckProbConfig() {
		try{
			String fileName = XlsSheetNameType.horse_luckprob_config.getXlsName();
			String sheetName = XlsSheetNameType.horse_luckprob_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<HorseLuckProb> luckProbList = XlsPojoUtil.sheetToList(sourceFile, sheetName, HorseLuckProb.class);
			if(Util.isEmpty(luckProbList)){
				Log4jManager.CHECK.error("not config the luckProbList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			luckProbMap = Maps.newHashMap();
			for(HorseLuckProb luckProb : luckProbList){
				List<HorseLuckProb> list = null;
				if(luckProbMap.containsKey(luckProb.getSchemeId())){
					list = luckProbMap.get(luckProb.getSchemeId());
					list.add(luckProb);
				}else{
					list = Lists.newArrayList();
					list.add(luckProb);
					luckProbMap.put(luckProb.getSchemeId(), list);
				}
				
			}
			
		}catch(Exception e){
			logger.error("loadHorseLuckProbConfig is error",e);
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
	public HorseProp getHorsePropById(String horseIdLevel) {
		return horsePropMap.get(horseIdLevel);
	}

//	@Override
//	public HorseExp getHorseExpByLevelQuality(String levelQ) {
//		return horseExpMap.get(levelQ);
//	}

	@Override
	public List<HorseSkill> getHorseSkillList(int horseId) {
		return horseSkillMap.get(horseId);
	}

	@Override
	public HorseStar getHorseStar(byte quality) {
		if(!horseStarMap.containsKey(quality)){
			return null;
		}
		return horseStarMap.get(quality);
	}

	@Override
	public byte getHorseHighStar(int horseId,byte quality) {
		byte star = -1;
		Map<Byte,Set<Byte>> qualityMap = qualityStarMap.get(horseId);
		Set<Byte> starSet = qualityMap.get(quality);
		for(Byte s : starSet){
			if(s > star){
				star = s;
			}
		}
		return star;
	}
	
	@Override
	public byte getHorseLowStar(int horseId,byte quality) {
		byte star = 0;
		Map<Byte,Set<Byte>> qualityMap = qualityStarMap.get(horseId);
		Set<Byte> starSet = qualityMap.get(quality);
		for(Byte s : starSet){
			if(s <= star){
				star = s;
			}
		}
		return star;
	}

	@Override
	public byte getHorseHighQuailty(int horseId) {
		byte quality = -1;
		Map<Byte,Set<Byte>> qualityMap = qualityStarMap.get(horseId);
		for(Entry<Byte,Set<Byte>> q : qualityMap.entrySet()){
			if(q.getKey() > quality){
				quality = q.getKey();
			}
		}
		return quality;
	}
	
	@Override
	public HorseSkillLimit getSkillLimit(String key) {
		return getSkillLimitMap().get(key);
	}

	@Override
	public HorseExchange getHorseExchange(int horseId) {
		return Util.fromMap(this.horseExchangeMap, horseId);
	}

	@Override
	public List<HorseLuckProb> getLuckProbList(int schemeId) {
		return luckProbMap.get(schemeId);
	}

	@Override
	public int getMaxLuckProb(int schemeId) {
		List<HorseLuckProb> list = luckProbMap.get(schemeId);
		int maxLuck = 0;
		for(HorseLuckProb luck : list){
			if(luck.getLuckHigh() >= maxLuck){
				maxLuck = luck.getLuckHigh();
			}
		}
		return maxLuck;
	}

}
