package com.game.draco.app.medal.vo;

import java.util.TreeMap;

import lombok.Data;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.draco.app.medal.MedalType;
import com.game.draco.app.medal.config.MedalConfig;
import com.google.common.collect.Maps;

@Data
public class MedalRoleData {
	
	private static final int DEFAULT_INDEX = -1;
	private String roleId;
	/**
	 * KEY=MedalType, VALUE=Index
	 */
	private TreeMap<Integer, Integer> roleMedalMap = Maps.newTreeMap();
	
	@JSONField(serialize=false)
	public void addMedalInfo(MedalType medalType, MedalConfig config){
		this.roleMedalMap.put(medalType.getType(), this.getIndex(config));
	}
	
	@JSONField(serialize=false)
	private int getIndex(MedalConfig config){
		if(null == config){
			return DEFAULT_INDEX;
		}
		return config.getIndex() ;
	}
	
	@JSONField(serialize=false)
	public int getMedalIndex(MedalType medalType){
		return this.getMedalIndex(medalType.getType());
	}
	
	@JSONField(serialize=false)
	public int getMedalIndex(int medalType){
		Integer value = this.roleMedalMap.get(medalType);
		if(null == value){
			return DEFAULT_INDEX;
		}
		return value;
	}
	
	@JSONField(serialize=false)
	public int getRoleMedalCount(){
		return this.roleMedalMap.size();
	}
	
	@JSONField(serialize=false)
	public void update(MedalType medalType, int index){
		this.roleMedalMap.put(medalType.getType(), index);
	}
	
}
