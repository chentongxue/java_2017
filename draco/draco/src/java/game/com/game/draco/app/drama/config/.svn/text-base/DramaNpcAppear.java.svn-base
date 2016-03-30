package com.game.draco.app.drama.config;

import com.game.draco.GameContext;
import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseNpcAppearItem;

import lombok.Data;

public @Data class DramaNpcAppear extends DramaBase {
	private short npcId; //剧情npcid
	private byte toward; //npc朝向0：向下45度	1：向上45度
	private short posX; 
	private short posY; 
	private byte appearType; //出现方式
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseNpcAppearItem item = new DramaBaseNpcAppearItem();
		item.setNpcId(this.npcId);
		DramaNpc npc = GameContext.getDramaApp().getDramaNpc(this.npcId);
		item.setResId(npc.getResId());
		item.setToward(this.toward);
		item.setPosX(this.posX);
		item.setPosY(this.posY);
		item.setAppearType(this.appearType);
		item.setResType(npc.getResType());
		return item;
	}
}
