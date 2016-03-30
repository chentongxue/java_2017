package com.game.draco.app.npc.refreshrule;

import lombok.Data;
import sacred.alliance.magic.app.map.data.NpcBorn;

public @Data class RefreshRule {
	
	private int ruleId;//规则ID
	private String bornnpcid;//NPC模板ID
	private int bornnpccount;//NPC数量
	private short bornmapgxbegin;//坐标X最小值
	private short bornmapgxend;//坐标X最大值
	private short bornmapgybegin;//坐标Y最小值
	private short bornmapgyend;//坐标Y最大值
	private int bornNpcDir ;
	private int bornTime;
	private int batchIndex;
	private boolean boss;// 是否Boss
	private String broadcast;
	private int prob;//权重
	private int groupId;
	
	public NpcBorn getNpcBorn(){
		NpcBorn npcBorn = new NpcBorn();
		npcBorn.setBornnpcid(this.bornnpcid);
		npcBorn.setBornnpccount(this.bornnpccount);
		npcBorn.setBornmapgxbegin(this.bornmapgxbegin);
		npcBorn.setBornmapgxend(this.bornmapgxend);
		npcBorn.setBornmapgybegin(this.bornmapgybegin);
		npcBorn.setBornmapgyend(this.bornmapgyend);
		npcBorn.setBornNpcDir(bornNpcDir);
		return npcBorn;
	}
	
}
