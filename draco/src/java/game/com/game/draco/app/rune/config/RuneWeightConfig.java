package com.game.draco.app.rune.config;

import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;

import com.google.common.collect.Maps;

public @Data
class RuneWeightConfig implements KeySupport<String> {

	private int level;
	private int atk;
	private int rit;
	private int maxHP;
	private int breakDefense;
	private int critAtk;
	private int hit;
	private int dodge;
	private int critRit;
	
	private Map<Byte,Integer>runeWeightMap=Maps.newHashMap();
	
	@Override
	public String getKey() {
		return ""+level;
	}
	
	public  Map<Byte,Integer> getRuneWeightMap(){
		return runeWeightMap;
	}

	public void init(String fileInfo) {
		String info = fileInfo + this.level + ":";
		if (this.level < 0) {
			this.checkFail(info + "level is config error!");
		}

		if (this.atk < 0) {
			this.checkFail(info + "atk is config error!");
		}
		if (this.rit < 0) {
			this.checkFail(info + "rit is config error!");
		}
		if (this.maxHP < 0) {
			this.checkFail(info + "maxHP is config error!");
		}
		if (this.breakDefense < 0) {
			this.checkFail(info + "breakDefense is config error!");
		}
		if (this.critAtk < 0) {
			this.checkFail(info + "critAtk is config error!");
		}
		if (this.dodge < 0) {
			this.checkFail(info + "dodge is config error!");
		}
		if(this.critRit<0){
			this.checkFail(info+"critWeight is config error!");
		}
		if(this.hit<0){
			this.checkFail(info+"hit is config error!");
		}
		if(critRit+dodge+critAtk+breakDefense+maxHP+rit+atk+level<=0){
			this.checkFail(info+"WeightConfig is config error!");
		}
		this.runeWeightMap.put(AttributeType.atk.getType(), this.atk);
		this.runeWeightMap.put(AttributeType.rit.getType(), this.rit);
		this.runeWeightMap.put(AttributeType.maxHP.getType(), this.maxHP);
		this.runeWeightMap.put(AttributeType.breakDefense.getType(), this.breakDefense);
		this.runeWeightMap.put(AttributeType.critAtk.getType(), this.critAtk);
		this.runeWeightMap.put(AttributeType.dodge.getType(), this.dodge);
		this.runeWeightMap.put(AttributeType.critRit.getType(), this.critRit);
		this.runeWeightMap.put(AttributeType.hit.getType(), this.hit);
	}
	

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

}
