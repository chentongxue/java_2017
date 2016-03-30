package com.game.draco.app.drama.config;

import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseNpcAttackItem;

import lombok.Data;

public @Data class DramaNpcAttack extends DramaBase {
	private short npcId; //npcId
	private short anim; //动画
	private short effectId; //特效
	private short sound; //音效
	private short lastTime; //持续时间
	
	//npc
	private DramaNpc dramaNpc;
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseNpcAttackItem item  = new DramaBaseNpcAttackItem();
		item.setNpcId(this.npcId);
		item.setAnim(this.anim);
		item.setEffectId(this.effectId);
		item.setSound(this.sound);
		item.setLastTime(this.lastTime);
		return item;
	}
}
