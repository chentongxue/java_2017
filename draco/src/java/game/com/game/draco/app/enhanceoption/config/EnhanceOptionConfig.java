package com.game.draco.app.enhanceoption.config;

import java.util.Map;

import com.game.draco.message.item.EnhanceOptionItem;
import com.google.common.collect.Multimap;

import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import lombok.Data;

public @Data class EnhanceOptionConfig{
	private int minLevel;
	private int maxLevel;
	private String forwards;
	
	//起服检查
	public void init(){
		if(minLevel > maxLevel||minLevel<0||maxLevel<0){
			Log4jManager.CHECK.error("EnhanceOptionConfig init err: forwards =" +forwards +",minLevel="+minLevel+",maxLevel=maxLevel");
			Log4jManager.checkFail();
			return;
		}if(Util.isEmpty(forwards)){
			Log4jManager.CHECK.error("EnhanceOptionConfig init err: forwards =" +forwards +",forwards or forwardName is null or empty");
			Log4jManager.checkFail();
			return;
		}
	}
	public void add2OptionItemMap(Multimap<Integer, EnhanceOptionItem> itemMap, final Map<Short, EnhanceOptionBase> baseMap){
		init();
		for (int i = minLevel; i <= maxLevel; i++) {
			add2OptionItemMap(itemMap,baseMap,i,forwards);
		}
	}
	private void add2OptionItemMap(Multimap<Integer, EnhanceOptionItem> itemMap, final Map<Short, EnhanceOptionBase> baseMap,int level, String forwards) {
		String ids[] = forwards.split(",");
		for (String id : ids) {
			EnhanceOptionItem it = new EnhanceOptionItem();
			Short optionId = Short.parseShort(id);
			if(!baseMap.containsKey(optionId)){
				Log4jManager.CHECK.error("EnhanceOptionConfig add2OptionItemMap err: optionId =" +optionId+" not configured in enhance_options.xls ->options");
				Log4jManager.checkFail();
				return;
			}
			it.setForwardId(optionId);
			it.setForwardName(baseMap.get(optionId).getForwardName());
			it.setForwardResId(baseMap.get(optionId).getForwardResId());
			itemMap.put(level, it);
		}
	}
}
