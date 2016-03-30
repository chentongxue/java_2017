package com.game.draco.app.npc.domain;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;

import lombok.Data;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class NpcTeach {
	
	private int menuId;
	private String npcId;
	private String title;
	private String content;
	private int sonMenuId1;
	private int sonMenuId2;
	private int sonMenuId3;
	private int sonMenuId4;
	private int sonMenuId5;
	private int questId;//任务ID，玩家接受了这个任务，并且位完成时，才显示菜单
	
	public Status canChoose(RoleInstance role){
		if(0 == this.questId){//普通的NPC教学菜单，并不是任务菜单
			return Status.SUCCESS;
		}
		Quest quest = GameContext.getQuestApp().getQuest(this.questId);
		if(null == quest){
			return Status.Quest_Not_Exist;
		}
		RoleQuestLogInfo info = role.getQuestLogMap().get(this.questId);
		if(null == info){
			return Status.Quest_Not_Own;
		}
		if(quest.canSubmit(role)){
			return Status.Quest_Phase_Invalid;
		}
		return Status.SUCCESS;
	}
	
	public List<Integer> getSonMenuList(){
		List<Integer> sonMenuList = new ArrayList<Integer>();
		this.addSonMenu(sonMenuList, this.sonMenuId1);
		this.addSonMenu(sonMenuList, this.sonMenuId2);
		this.addSonMenu(sonMenuList, this.sonMenuId3);
		this.addSonMenu(sonMenuList, this.sonMenuId4);
		this.addSonMenu(sonMenuList, this.sonMenuId5);
		return sonMenuList;
	}
	
	private void addSonMenu(List<Integer> sonMenuList, int sonMenuId){
		if(0 != sonMenuId){
			sonMenuList.add(sonMenuId);
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("menuId:"+menuId)
			.append(" npcId:"+npcId)
			.append(" title:"+title)
			.append(" content:"+content)
			.append(" sonMenuId1:"+sonMenuId1)
			.append(" sonMenuId2:"+sonMenuId2)
			.append(" sonMenuId3:"+sonMenuId3)
			.append(" sonMenuId4:"+sonMenuId4)
			.append(" sonMenuId5:"+sonMenuId5)
		.append(" questId:"+questId);
		return sb.toString();
	}
	
}
