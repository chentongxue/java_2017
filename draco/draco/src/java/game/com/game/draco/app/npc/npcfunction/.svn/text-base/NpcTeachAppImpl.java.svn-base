package com.game.draco.app.npc.npcfunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTeach;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1605_TeachNpcFunctionReqMessage;
import com.game.draco.message.response.C1605_TeachNpcFunctionRespMessage;
import com.google.common.collect.Maps;

public class NpcTeachAppImpl implements NpcTeachApp,NpcFunctionSupport{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private PathConfig pathConfig;
	private Map<String,List<NpcTeach>> npcRootMenuMap = Maps.newHashMap();
	private Map<String,NpcTeach> allTeachMap = Maps.newHashMap();
	private static final short MenuBeforeOperateReqCmdId = new C1605_TeachNpcFunctionReqMessage().getCommandId() ; //1605;
	
	public void start()throws ServiceException{
		this.initNpcTeach();
	}

	@Override
	public Message getChooseMenuMessage(RoleInstance role, String param) {
		if (Util.isEmpty(param)) {
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		C1605_TeachNpcFunctionRespMessage respMsg = new C1605_TeachNpcFunctionRespMessage();
		String[] arr = param.split(Cat.underline);
		String menuId = arr[0];
		int npcRoleId = Integer.parseInt(arr[1]);
		NpcTeach teach = this.allTeachMap.get(menuId);

		if (null == teach || !teach.canChoose(role).isSuccess()) {
			return respMsg;
		}

		try {
			GameContext.getUserQuestApp().chooseMenu(role, teach.getMenuId());
		} catch (Exception e) {
			logger.error("UserQuestApp chooseMenu error:" + e);
		}
		List<NpcFunctionItem> items = new ArrayList<NpcFunctionItem>();
		// 内容
		respMsg.setInfo(teach.getContent());
		for (int sonMenuId : teach.getSonMenuList()) {
			if (0 == sonMenuId) {
				continue;
			}
			NpcTeach sonTeach = this.allTeachMap.get(String.valueOf(sonMenuId));
			if (null == sonTeach) {
				continue;
			}
			if (!sonTeach.canChoose(role).isSuccess()) {
				continue;
			}
			NpcFunctionItem item = new NpcFunctionItem();
			item.setTitle(sonTeach.getTitle());
			item.setCommandId(MenuBeforeOperateReqCmdId);
			item.setParam(this.formatMenuBeforeOperateParam(sonMenuId,
					npcRoleId));
			items.add(item);
		}
		respMsg.setItems(items);
		return respMsg;
	}

	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,
			NpcInstance npc) {
		List<NpcFunctionItem> items = new ArrayList<NpcFunctionItem>();
		String npcId = npc.getNpc().getNpcid();
		List<NpcTeach> teachList = this.npcRootMenuMap.get(npcId);
		if (Util.isEmpty(teachList)) {
			return items;
		}
		for (NpcTeach teach : teachList) {
			if (null == teach) {
				continue;
			}
			if (!teach.canChoose(role).isSuccess()) {
				continue;
			}
			NpcFunctionItem item = new NpcFunctionItem();
			item.setTitle(teach.getTitle());
			int sonMenuId = teach.getMenuId();
			if (sonMenuId > 0) {
				item.setCommandId(MenuBeforeOperateReqCmdId);
				item.setParam(this.formatMenuBeforeOperateParam(sonMenuId,npc.getIntRoleId()));
			}
			item.setContent(teach.getContent());
			items.add(item);
		}
		return items;
	}
	
	private String formatMenuBeforeOperateParam(int menuId,int npcRoleId){
		return menuId + Cat.underline + npcRoleId ;
	}
	
	
	/**
	 * 初始化NpcTeach，加载配置文件和构建NPC菜单
	 * @throws ServiceException
	 */
	private void initNpcTeach() throws ServiceException {
		try{
			Map<String,NpcTeach> allTeach = this.loadNpcTeachConfig();
			if(null != allTeach){
				this.allTeachMap.clear();
				this.allTeachMap = allTeach;
			}
			Map<String,List<NpcTeach>> npcMenu = this.loadRootMenuMap();
			if(null != npcMenu){
				this.npcRootMenuMap.clear();
				this.npcRootMenuMap = npcMenu;
			}
		}catch(Exception e){
			throw new ServiceException("NpcTeachApplicationImpl.loadNpcTeach() exception",e);
		}
	}
	
	/**
	 * 加载NpcTeach的配置文件
	 * @return
	 */
	private Map<String,NpcTeach> loadNpcTeachConfig(){
		String fileName = XlsSheetNameType.npc_teach.getXlsName();
		String sheetName = XlsSheetNameType.npc_teach.getSheetName();
		String sourceFile = this.pathConfig.getXlsPath() + fileName;
		return XlsPojoUtil.sheetToMap(sourceFile, sheetName, NpcTeach.class);
	}
	
	/**
	 * 加载NPC的一级菜单列表
	 * @return
	 * @throws ServiceException
	 */
	private Map<String,List<NpcTeach>> loadRootMenuMap()throws ServiceException{
		try{
			Map<String,List<NpcTeach>> npcTeachMap = Maps.newHashMap();
			for(NpcTeach npcTeach : this.allTeachMap.values()){
				if(null == npcTeach){
					continue;
				}
				//非一级菜单不加载
				if(Util.isEmpty(npcTeach.getNpcId())){
					continue;
				}
				String npcTemplateId = npcTeach.getNpcId();
				List<NpcTeach> teachList = npcTeachMap.get(npcTemplateId);
				if(Util.isEmpty(teachList)){
					teachList = new ArrayList<NpcTeach>();
					teachList.add(npcTeach);
					npcTeachMap.put(npcTemplateId, teachList);
				} else {
					teachList.add(npcTeach);
				}
			}
			return npcTeachMap;
		}catch(Exception e){
			throw new ServiceException("NpcTeachApplicationImpl.loadNpcItem() exception",e);
		}
	}

	@Override
	public void reload() throws ServiceException {
		this.initNpcTeach();
	}

	public void setPathConfig(PathConfig pathConfig) {
		this.pathConfig = pathConfig;
	}

}
