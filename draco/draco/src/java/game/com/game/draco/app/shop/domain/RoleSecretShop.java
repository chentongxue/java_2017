package com.game.draco.app.shop.domain;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;

public @Data class RoleSecretShop {
	
	private String roleId;
	private String goods;
	private Date refreshTime;
	private boolean existRecord = false;
	//id£¬´ÎÊý
	private Map<Integer, Integer> secretShopMap = new LinkedHashMap<Integer, Integer>();
	
	public boolean init() {
		if(Util.isEmpty(goods)) {
			return false;
		}
		String[] arr = goods.split(Cat.semicolon);
		for(String str:arr){
			String[] s  = str.split(Cat.comma);
			secretShopMap.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
		}
		return true;
	}
	
	public boolean unite() {
		if(Util.isEmpty(secretShopMap)) {
			return false;
		}
		
		StringBuffer sb = new StringBuffer();
		String cat = "";
		for(Integer id:secretShopMap.keySet()){
			if(null == id) {
				continue;
			}
			sb.append(cat);
			sb.append(id);
			sb.append(Cat.comma);
			sb.append(secretShopMap.get(id));
			cat = Cat.semicolon;
		}
		goods = sb.toString();
		if(Util.isEmpty(goods)) {
			return false;
		}
		return true;
	}
}
