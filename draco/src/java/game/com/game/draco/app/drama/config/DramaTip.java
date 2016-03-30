package com.game.draco.app.drama.config;

import java.util.List;

import lombok.Data;

import com.game.draco.message.item.DramaBaseItem;
import com.game.draco.message.item.DramaBaseTipItem;
import com.google.common.collect.Lists;

public @Data class DramaTip extends DramaBase {
	
	private String tip1;
	private String tip2;
	private String tip3;
	private String tip4;
	private String tip5;
	
	private byte times ;
	
	private String[] tips = null ;
	
	private void init(String tip,List<String> list){
		if(null == tip || 0 == tip.trim().length()){
			return ;
		}
		list.add(tip);
	}

	@Override
	public DramaBaseItem getDramaBaseInfo() {
		DramaBaseTipItem item = new DramaBaseTipItem();
		if(null == tips){
			List<String> list = Lists.newArrayList() ;
			this.init(tip1, list);
			this.init(tip2, list);
			this.init(tip3, list);
			this.init(tip4, list);
			this.init(tip5, list);
			String[] t = list.toArray(new String[list.size()]) ;
			tips = t ;
		}
		item.setTip(this.tips);
		item.setTimes(this.times) ;
		return item;
	}

}
