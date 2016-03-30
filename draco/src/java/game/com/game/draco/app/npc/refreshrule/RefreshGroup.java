package com.game.draco.app.npc.refreshrule;

import sacred.alliance.magic.app.map.data.NpcBorn;
import lombok.Data;

public @Data class RefreshGroup{
	
	private int groupId;//分组ID
	private String npcId;//npcId
	private int bornnpccount;//NPC数量
	private short bornmapgxbegin;//坐标X最小值
	private short bornmapgxend;//坐标X最大值
	private short bornmapgybegin;//坐标Y最小值
	private short bornmapgyend;//坐标Y最大值
	
	public NpcBorn getNpcBorn(){
		NpcBorn npcBorn = new NpcBorn();
		npcBorn.setBornnpcid(this.npcId);
		npcBorn.setBornnpccount(this.bornnpccount);
		npcBorn.setBornmapgxbegin(this.bornmapgxbegin);
		npcBorn.setBornmapgxend(this.bornmapgxend);
		npcBorn.setBornmapgybegin(this.bornmapgybegin);
		npcBorn.setBornmapgyend(this.bornmapgyend);
		return npcBorn;
	}
}
