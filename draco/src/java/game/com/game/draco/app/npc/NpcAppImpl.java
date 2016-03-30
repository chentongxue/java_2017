package com.game.draco.app.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.config.ForceConfig;
import com.game.draco.app.npc.config.ForceNpcConfig;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.npcfunction.NpcFunctionSupport;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.message.item.NpcFunctionItem;

public class NpcAppImpl implements NpcApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, NpcTemplate> npcMap = new HashMap<String, NpcTemplate>();
	private List<NpcFunctionSupport> npcFunctionSupports ;
	private Map<Integer, ForceConfig> forceConfigMap = new HashMap<Integer, ForceConfig>();
	private Map<String, ForceRelation> forceNpcConfigMap = new HashMap<String, ForceRelation>();
	
	public void setNpcFunctionSupports(List<NpcFunctionSupport> npcFunctionSupports) {
		this.npcFunctionSupports = npcFunctionSupports;
	}
	
	public NpcTemplate[] getAllNpc() {
		return npcMap.values().toArray(new NpcTemplate[] {});
	}

	public boolean reload() throws ServiceException {
		this.loadNpcTemplate();
		return true;
	}

	private void loadNpcTemplate(){
		String fileName = "";
		String sheetName = "";
		try {
			fileName = XlsSheetNameType.npc_template.getXlsName();
			sheetName = XlsSheetNameType.npc_template.getSheetName();
			String path = GameContext.getPathConfig().getXlsPath();
			String sourceFile = path + fileName;
			npcMap = XlsPojoUtil.sheetToGenericMap(sourceFile,sheetName, NpcTemplate.class);
			for(NpcTemplate nt : this.npcMap.values()){
				if(NpcType.isCorrectType(nt.getNpctype())){
					continue ;
				}
				Log4jManager.CHECK.error("npctemplate npctype config error,npcType=" + nt.getNpctype() + " npcId=" + nt.getNpcid());
				Log4jManager.checkFail();
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void start() {
		this.loadNpcTemplate();
		this.loadForceConfig();
		this.loadForceNpcConfig();
	}
	
	private void loadForceConfig(){
		String fileName = "";
		String sheetName = "";
		try {
			String path = GameContext.getPathConfig().getXlsPath();
			fileName = XlsSheetNameType.force_config.getXlsName();
			sheetName = XlsSheetNameType.force_config.getSheetName();
			forceConfigMap = XlsPojoUtil.sheetToGenericMap(path + fileName, sheetName,ForceConfig.class);
		}catch(Exception e){
			Log4jManager.CHECK.error("loadForceConfig error,filename=" + fileName + " sheetName=" + sheetName,e);
			Log4jManager.checkFail();
		}
	}
	
	private void loadForceNpcConfig(){
		String fileName = "";
		String sheetName = "";
		try {
			String path = GameContext.getPathConfig().getXlsPath();
			fileName = XlsSheetNameType.force_npc_config.getXlsName();
			sheetName = XlsSheetNameType.force_npc_config.getSheetName();
			String sourceFile = path + fileName;
			List<ForceNpcConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ForceNpcConfig.class);
			if (Util.isEmpty(list)) {
				return;
			}
			for (ForceNpcConfig forceNpcConfig : list) {
				String key = Math.min(forceNpcConfig.getForce1(), forceNpcConfig.getForce2()) + Cat.underline
						+ Math.max(forceNpcConfig.getForce1(), forceNpcConfig.getForce2());
				forceNpcConfigMap.put(key, ForceRelation.getByType(forceNpcConfig.getRelation()));
			}
		}catch(Exception e){
			Log4jManager.CHECK.error("loadForceNpcConfig error,filename=" + fileName + " sheetName=" + sheetName,e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void stop() {

	}
	@Override
	public void setArgs(Object args) {
		
	}

	@Override
	public NpcTemplate getNpcTemplate(String templateId) {
		if(Util.isEmpty(templateId)){
			return null ;
		}
		return npcMap.get(templateId);
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,
			NpcInstance npc) {
		List<NpcFunctionItem>  values = new ArrayList<NpcFunctionItem>();
		if(null == npcFunctionSupports){
			return values ;
		}
		for (NpcFunctionSupport func : npcFunctionSupports) {
			try {
				List<NpcFunctionItem> list = func.getNpcFunction(role, npc);
				if (Util.isEmpty(list)) {
					continue;
				}
				values.addAll(list);
			} catch (Exception ex) {
				logger.error("",ex);
			}
		}
		//从ai中获取
		List<NpcFunctionItem> list = npc.getAi().talkTo(role);
		if(!Util.isEmpty(list)){
			values.addAll(list);
		}
		return values;
	}

	
	public ForceConfig getForceConfig (int forceId) {
		return this.forceConfigMap.get(forceId);
	}
	
	@Override
	public ForceRelation getNpcForceRelation(byte type1,byte type2){
		if(type1==type2){
			return ForceRelation.friend;
		}
		String key = Math.min(type1, type2) + Cat.underline + Math.max(type1, type2);
		ForceRelation fr = forceNpcConfigMap.get(key);
		if(null == fr){
			return ForceRelation.neutral ;
		}
		return fr ;
	}
}
