package com.game.draco.app.quest.phase;

import com.game.draco.app.quest.QuestPhaseAdator;

import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class SubmitQuestPhase extends QuestPhaseAdator{

	private String npcId;
	private String dialogContent;
	
	@Override
	public Point getEventPoint(RoleInstance role) {
		return this.getPoint(mapId, npcId);
	}
	
	public SubmitQuestPhase(String npcId) {
		this(npcId,"");
	}
	
	public SubmitQuestPhase(String npcId, String mapId){
		this(npcId, "", mapId);
	}
	
	public SubmitQuestPhase(String npcId, String dialogContent, String mapId) {
		this.npcId = npcId;
		this.dialogContent = dialogContent;
		this.mapId = mapId;
	}
	
	public String getNpcId() {
		return npcId;
	}

	public void setNpcId(String npcId) {
		this.npcId = npcId;
	}

	public String getDialogContent() {
		return dialogContent;
	}

	public void setDialogContent(String dialogContent) {
		this.dialogContent = dialogContent;
	}
	
	@Override
	public boolean isPhaseComplete(RoleInstance role) {
		return true ;
	}
}
