package com.game.draco.app.inslogic;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.GameContext;
import com.game.draco.app.inslogic.config.InsLogic;
import com.game.draco.app.inslogic.config.InsLogicGroup;


public class InsLogicDataAppImpl implements InsLogicDataApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//<副本逻辑>
	@Getter @Setter private Map<String,List<InsLogic>> insLogicMap = null;
	
	/**
	 * 加载副本逻辑数据
	 */
	private void loadInsLogicConfig(){
		try{
			String fileName = XlsSheetNameType.ins_logic_config.getXlsName();
			String sheetName = XlsSheetNameType.ins_logic_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<InsLogic> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, InsLogic.class);
			
			String groupFileName = XlsSheetNameType.ins_logic_group_config.getXlsName();
			String groupSheetName = XlsSheetNameType.ins_logic_group_config.getSheetName();
			String groupSourceFile = GameContext.getPathConfig().getXlsPath() + groupFileName;
			List<InsLogicGroup> groupList = XlsPojoUtil.sheetToList(groupSourceFile, groupSheetName, InsLogicGroup.class);
			
			if(Util.isEmpty(list) || Util.isEmpty(groupList)){
				Log4jManager.CHECK.error("not config the insLogicMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				insLogicMap = Maps.newHashMap();
				for(InsLogic logic : list){
					for(InsLogicGroup group : groupList){
						String key = group.getMapId()  + Cat.underline + group.getSpriteId();
						List<InsLogic> logicList = null;
						if(group.getGroupId() == logic.getGroupId()){
							if(insLogicMap.containsKey(key)){
								logicList = insLogicMap.get(key);
								logicList.add(logic);
							}else{
								logicList = Lists.newArrayList();
								logicList.add(logic);
								insLogicMap.put(key,logicList);
							}
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("loadInsLogicConfig is error",e);
		}
	}

	@Override
	public void start() {
		try{
			//加载副本逻辑数据
			loadInsLogicConfig();
		}catch(Exception e){
			logger.error("start is error",e);
		}
		
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void setArgs(Object args) {
	}

	@Override
	public List<InsLogic> getInsLogic(String key) {
		if(Util.isEmpty(key)){
			return null;
		}
		if(getInsLogicMap().containsKey(key)){
			return getInsLogicMap().get(key);
		}
		return null;
	}

}
