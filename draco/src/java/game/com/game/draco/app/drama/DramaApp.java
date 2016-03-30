package com.game.draco.app.drama;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.drama.config.Drama;
import com.game.draco.app.drama.config.DramaNpc;
import com.game.draco.app.drama.config.DramaTriggerType;
import com.game.draco.message.item.DramaInfoItem;

public interface DramaApp extends Service, AppSupport{
	
	/**
	 * 获得剧情npc
	 * @param npcId
	 * @return
	 */
	public DramaNpc getDramaNpc(short npcId);
	
	/**
	 * 触发剧情接口
	 * @param role
	 * @param triggerType 触发类型
	 * @param dramaId 点触发时用到
	 * @param mapId 进入地图触发用到
	 * @param questId 交接任务触发用到
	 * @param npcId npc死亡,出生触发用到
	 */
	public void triggerDrama(RoleInstance role, DramaTriggerType triggerType, short dramaId
			, String mapId, int questId, String npcId);
	
	/**
	 * 进入地图时主动推送点触发类的点信息
	 * @param role
	 * @param mapId
	 */
	public void enterMap(RoleInstance role, String mapId);
	
	public boolean canNpcTrigger(String npcId,String mapId,DramaTriggerType triggerType) ;
	
	public Drama triggerNpcBornDrama(RoleInstance role,String mapId,String npcId) ;
	
	public DramaInfoItem createDramaInfoItem(Drama drama,String dramaMapId) ;
}
