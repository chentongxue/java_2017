package com.game.draco.app.quest.phase;

import java.util.HashSet;
import java.util.Set;

import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.QuestPhaseAdator;
import com.game.draco.app.quest.QuestTerm;
import com.game.draco.app.quest.base.QuestTermType;
import com.game.draco.app.quest.phase.term.MenuTerm;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class ChooseMenuQuestPhase extends QuestPhaseAdator {
	
	private Set<String> menuSet = new HashSet<String>();
	
	@Override
	public int chooseMenu(RoleInstance role, int menuId) {
		if(!this.menuSet.contains(String.valueOf(menuId))){
			return 0;
		}
		String id = Cat.colon + String.valueOf(menuId) + Cat.colon;
		int index = startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			String termId = Cat.colon + term.getParameter() + Cat.colon;
			int i = termId.indexOf(id);
			if(i < 0){
				continue;
			}
			int nowNum = this.getCurrentNum(role, index);
			if(nowNum >= term.getCount()){
				return 0;
			}
			this.incrCurrentNum(role, index);
			//发进度提示
			QuestHelper.pushQuestTipMessage(role, this.master, nowNum+1, term, index);
			return 1;
		}
		return 0;
	}
	
	public ChooseMenuQuestPhase(String menuId, String mapId, String npcId){
		this(menuId, mapId, npcId,
			 null, null, null,
			 null, null, null);
	}
	
	public ChooseMenuQuestPhase(String menuId1, String mapId1, String npcId1,
			String menuId2, String mapId2, String npcId2){
		this(menuId1, mapId1, npcId1,
			 menuId2, mapId2, npcId2,
			 null, null, null);
	}
	
	public ChooseMenuQuestPhase(String menuId1, String mapId1, String npcId1,
								String menuId2, String mapId2, String npcId2,
								String menuId3, String mapId3, String npcId3){
		this.init(menuId1, mapId1, npcId1);
		this.init(menuId2, mapId2, npcId2);
		this.init(menuId3, mapId3, npcId3);
	}
	
	private void init(String menuId, String mapId, String npcId) {
		if (Util.isEmpty(menuId)) {
			return;
		}
		for (String id : menuId.split(Cat.colon)) {
			if (Util.isEmpty(id)) {
				continue;
			}
			this.menuSet.add(id);
		}
		this.questTermList.add(new MenuTerm(QuestTermType.ChooseMenu, 1, menuId, npcId, mapId));
	}
	
	@Override
	public Point getEventPoint(RoleInstance role) {
		int index = this.startIndex ;
		for(QuestTerm term : this.questTermList){
			index ++ ;
			// 获得当前阶段数目
			int nowNum = this.getCurrentNum(role, index);
			if (nowNum >= term.getCount()) {
				// 此种已经满足数量
				continue ;
			}
			MenuTerm thisTerm = (MenuTerm)term ;
			return this.getPoint(thisTerm.getMapId(), thisTerm.getNpcId());
		}
		return null ;
	}
}
