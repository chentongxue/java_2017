package com.game.draco.app.speciallogic;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.speciallogic.config.SpecialLogic;
import com.game.draco.app.speciallogic.config.WorldLevelGroupLogic;
import com.game.draco.app.speciallogic.config.WorldLevelLogic;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SpecialLogicAppImpl implements SpecialLogicApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//特殊技能逻辑数据
	@Getter @Setter private Map<String,SpecialLogic> specialMap = null;
	

	//世界等级特殊逻辑数据
	@Getter @Setter private Map<String,Integer> worldLevelMap = null;
	
	//世界等级组特殊逻辑数据
	@Getter @Setter private Map<Integer,Map<Integer,WorldLevelGroupLogic>> worldLevelGroupMap = null;
	
	/**
	 * 加载特殊技能逻辑数据
	 */
	private void loadSpecialLogicConfig(){
		try{
			String fileName = XlsSheetNameType.special_logic_config.getXlsName();
			String sheetName = XlsSheetNameType.special_logic_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			specialMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, SpecialLogic.class);
			if(Util.isEmpty(specialMap)){
				Log4jManager.CHECK.error("not config the loadSpecialLogicConfig,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
				return;
			}
			
			for(Entry<String,SpecialLogic> specialLogic : specialMap.entrySet()){
				if(specialLogic.getKey().equals(SpecialLogicType.unionIntegral.getType() + Cat.underline + specialLogic.getValue().getMapId())){
					NpcTemplate npcT = GameContext.getNpcApp().getNpcTemplate(specialLogic.getValue().getNpcId());
					if(npcT == null){
						continue;
					}
					npcT.getNpcname();
					npcT.setLoadSpecialLogic(SpecialLogicType.unionIntegral.getType());
				}
			}
			
		}catch(Exception e){
			logger.error("loadSpecialLogicConfig is error",e);
		}
	}
	
	/**
	 * 加载世界等级特殊逻辑
	 */
	private void loadWorldLevelLogicConfig(){
		try{
			String fileName = XlsSheetNameType.special_worldlevel_config.getXlsName();
			String sheetName = XlsSheetNameType.special_worldlevel_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<WorldLevelLogic> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, WorldLevelLogic.class);
			if(Util.isEmpty(list)){
				return;
			}
			worldLevelMap = Maps.newHashMap();
			for(WorldLevelLogic treasure : list){
				worldLevelMap.put(treasure.getNpcId(), treasure.getGroupId());
				NpcTemplate npcT = GameContext.getNpcApp().getNpcTemplate(treasure.getNpcId());
				if(npcT == null){
					continue;
				}
				npcT.setLoadSpecialLogic(SpecialLogicType.worldLevel.getType());
			}
		}catch(Exception e){
			logger.error("loadSpecialLogicConfig is error",e);
		}
	}
	
	/**
	 * 加载世界等级组特殊逻辑
	 */
	private void loadWorldLevelGroupLogicConfig(){
		try{
			
			String fileName = XlsSheetNameType.special_worldlevel_group_config.getXlsName();
			String sheetName = XlsSheetNameType.special_worldlevel_group_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<WorldLevelGroupLogic> groupList = XlsPojoUtil.sheetToList(sourceFile, sheetName, WorldLevelGroupLogic.class);
			if(!Util.isEmpty(groupList)){
				worldLevelGroupMap = Maps.newHashMap();
				for(WorldLevelGroupLogic treasureGroup : groupList){
					Map<Integer,WorldLevelGroupLogic> map = null;
					if(worldLevelGroupMap.containsKey(treasureGroup.getGroupId())){
						map = worldLevelGroupMap.get(treasureGroup.getGroupId());
					}else{
						map = Maps.newHashMap();
						worldLevelGroupMap.put(treasureGroup.getGroupId(), map);
					}
					map.put(treasureGroup.getWorldLevel(),treasureGroup);
				}
			}
			
		}catch(Exception e){
			logger.error("loadSpecialLogicConfig is error",e);
		}
	}
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		try{
			//加载特殊数据
			loadSpecialLogicConfig();
			//加载世界等级逻辑
			loadWorldLevelLogicConfig();
			//加载世界等级组逻辑
			loadWorldLevelGroupLogicConfig();
		}catch(Exception e){
			logger.error("start is error",e);
		}
	}

	@Override
	public void stop() {
	}
	
	@Override
	public SpecialLogic getSpecialLogic(String key){
		if(Util.isEmpty(specialMap)){
			return null;
		}
		return specialMap.get(key);
	}
	
	private void frozenLogic(AbstractRole abstractRole,String key,Point point){
		
		SpecialLogic logic = getSpecialLogic(key);
		if(logic == null){
			return;
		}
		
		int mapX = point.getX();
		int mapY = point.getY();
		
		NpcBorn npcBorn = new NpcBorn();
		NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(logic.getNpcId());
		if (npcTemplate == null) {
			return;
		}
		npcBorn.setBornmapgxbegin(mapX);
		npcBorn.setBornmapgybegin(mapY+10);
		npcBorn.setBornmapgxend(mapX);
		npcBorn.setBornmapgyend(mapY+10);
		npcBorn.setBornnpccount(1);
		npcBorn.setBornnpcid(logic.getNpcId());
		npcBorn.setBornNpcDir(Direction.DOWN.getType());
		abstractRole.getMapInstance().summonCreateNpc(npcBorn,abstractRole.getRoleId());
	}
	
	@Override
	public WorldLevelGroupLogic getWorldLevelGroupLogic(String key){

		if(Util.isEmpty(worldLevelMap) || Util.isEmpty(worldLevelGroupMap)){
			return null;
		}
		
		if(!worldLevelMap.containsKey(key)){
			return null;
		}
		
		int groupId = worldLevelMap.get(key);
		
		if(!worldLevelGroupMap.containsKey(groupId)){
			return null;
		}
		
		Map<Integer,WorldLevelGroupLogic> map = worldLevelGroupMap.get(groupId);
		if(Util.isEmpty(map)){
			return null;
		}
		
		int worldLevel = GameContext.getWorldLevelApp().getWorldLevel();
		
		if(!map.containsKey(worldLevel)){
			return null;
		}
		return map.get(worldLevel);
	}

	@Override
	public void logic(AbstractRole role, SpecialLogicType type, Point point) {
		String key = type.getType() + Cat.underline + point.getMapid();
		switch(type){
			case frozen:
				frozenLogic(role,key,point);
				break;
			default :
				break;
		}
	}
	
	@Override
	public String getNpcName(String npcname,byte logicType){
		if(logicType == SpecialLogicType.unionIntegral.getType()){
		}
		return npcname;
	}
	
}
