package com.game.draco.app.equip.config;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.util.Initable;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Shorts;

public @Data class StarMaterialWays implements KeySupport<String>,Initable{

	private int material ;
	private String waysList ;
	
	private List<Short> waysIdList = Lists.newArrayList() ;
	
	public String getKey(){
		return String.valueOf(material);
	}
	
	public void init(){
		if(Util.isEmpty(waysList)){
			waysIdList.clear();
			return ;
		}
		String[] arr = Util.splitString(waysList);
		if(null == arr){
			waysIdList.clear();
			return ;
		}
		waysIdList.clear();
		for(String s : arr){
			if(!Util.isNumber(s)){
				continue ;
			}
			short value = Short.parseShort(s);
			if(!waysIdList.contains(value)){
				waysIdList.add(value);
			}
		}
	}
}
	
	
