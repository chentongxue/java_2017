package com.game.draco.app.npc.npcfunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.npcfunction.vo.NpcFunctionVO;
import com.game.draco.message.item.NpcFunctionItem;

import lombok.Data;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;


public @Data class NpcFunctionAppImpl implements NpcFunctionApp{
	private Map<String,List<NpcFunctionVO>> npcFunctionMap = new HashMap<String,List<NpcFunctionVO>>();
	
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		try {
			this.reload();
		} catch (ServiceException e) {
			Log4jManager.CHECK.error("load npc func error",e);
		}
	}

	@Override
	public void stop() {
		
	}
	public boolean reload() throws ServiceException {
		Map<String,List<NpcFunctionVO>> data = this.loadData() ;
		if(null == data){
			return false ;
		}
		npcFunctionMap.clear();
		npcFunctionMap.putAll(data);
		return true;
	}
	
	private Map<String,List<NpcFunctionVO>> loadData() {
		List<NpcFunctionVO> npcFuncList = null ;
		String fileName = "";
		String sheetName = "";
		String sourceFile = "";
		try {
			String path = GameContext.getPathConfig().getXlsPath();
			// ¹¦ÄÜNPC¼Ó²Ã
			fileName = XlsSheetNameType.npc_function.getXlsName();
			sheetName = XlsSheetNameType.npc_function.getSheetName();
			sourceFile = path + fileName;
			npcFuncList = XlsPojoUtil.sheetToList(sourceFile, sheetName,NpcFunctionVO.class);
		}catch(Exception e){
			Log4jManager.CHECK.error("load npc func error,fileName=" + fileName + " sheetName=" + sheetName,e);
			Log4jManager.checkFail() ;
			return null ;
		}
		if(null == npcFuncList){
			return new HashMap<String,List<NpcFunctionVO>>();
		}
		Map<String,List<NpcFunctionVO>> functionMap = new HashMap<String,List<NpcFunctionVO>>();
		for(NpcFunctionVO function:npcFuncList){
			if(!functionMap.containsKey(function.getNpcId())){
				functionMap.put(function.getNpcId(),new ArrayList<NpcFunctionVO>());
			}
			functionMap.get(function.getNpcId()).add(function);
		}
		return functionMap;
	}
	
	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,
			NpcInstance npc) {
		List<NpcFunctionItem> list = new ArrayList<NpcFunctionItem>();
		String npcId = npc.getNpc().getNpcid();
		List<NpcFunctionVO> funcList = this.npcFunctionMap.get(npcId);
		if (Util.isEmpty(funcList)) {
			return list;
		}
		for (NpcFunctionVO function : funcList) {
			if (null == function) {
				continue;
			}
			if (!function.canDisplay(role)) {
				continue;
			}
			NpcFunctionItem item = new NpcFunctionItem();
			item.setCommandId(function.getCommandId());
			item.setTitle(function.getFunctionName());
			item.setParam(function.getParam());
			list.add(item);
		}
		return list;
	}
	
}
