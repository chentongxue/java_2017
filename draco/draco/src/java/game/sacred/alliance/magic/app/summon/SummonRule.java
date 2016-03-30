package sacred.alliance.magic.app.summon;

import lombok.Data;
import sacred.alliance.magic.app.map.data.NpcBorn;

public @Data class SummonRule {
	
	private int ruleId;//规则ID
	private String bornNpcid;//NPC模板ID
	private int bornNpccount;//NPC数量
	private byte bornNpcDir;//方位
	private short bornmapgxbegin;//坐标X最小值
	private short bornmapgxend;//坐标X最大值
	private short bornmapgybegin;//坐标Y最小值
	private short bornmapgyend;//坐标Y最大值
	
	public NpcBorn getNpcBorn(){
		NpcBorn npcBorn = new NpcBorn();
		npcBorn.setBornnpcid(this.bornNpcid);
		npcBorn.setBornnpccount(this.bornNpccount);
		npcBorn.setBornNpcDir(this.bornNpcDir);
		npcBorn.setBornmapgxbegin(this.bornmapgxbegin);
		npcBorn.setBornmapgxend(this.bornmapgxend);
		npcBorn.setBornmapgybegin(this.bornmapgybegin);
		npcBorn.setBornmapgyend(this.bornmapgyend);
		return npcBorn;
	}
	
}
