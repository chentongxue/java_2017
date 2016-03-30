package com.game.draco.app.drama.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.game.draco.message.item.DramaBaseComposeItem;
import com.game.draco.message.item.DramaBaseItem;

public @Data class DramaCompose extends DramaBase{
	private String dramaIds;
	
	private List<DramaBase> dramaBaseList = new ArrayList<DramaBase>();
	
	DramaBaseComposeItem item = new DramaBaseComposeItem();
	
	public void init(){
		List<DramaBaseItem> dramaBaseItemList = new ArrayList<DramaBaseItem>();
		for(DramaBase info : dramaBaseList){
			dramaBaseItemList.add(info.getDramaBaseInfo());
		}
		item.setDramaBaseItemList(dramaBaseItemList);
	}
	
	@Override
	public DramaBaseItem getDramaBaseInfo() {
		return item;
	}
}

