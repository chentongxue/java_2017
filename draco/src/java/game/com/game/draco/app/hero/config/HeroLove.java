package com.game.draco.app.hero.config;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.hero.HeroLoveType;
import com.game.draco.app.horse.config.HorseBase;

public @Data class HeroLove implements KeySupport<String>{
	
	//情缘id
	private int loveId ;
	private String loveName ;
	private int heroId ;
	private byte loveType ;
	private String ids	;
	private String desc = ""	;
	
	private Set<Integer> idSet = new HashSet<Integer>() ;
	private int[] intIdArr = null ;
	
	private int minQuality = Integer.MAX_VALUE ;
	private int minStar = Integer.MAX_VALUE ;
	
	public void resetMinQualityStar(int quality,int star){
		if(quality > this.minQuality){
			return ;
		}
		if(quality == this.minQuality && star >= minStar){
			return ;
		}
		this.minQuality = quality ;
		this.minStar = star ;
	}
	
	public boolean containId(int id){
		return (null != idSet && idSet.contains(id));
	}
 
	@Override
	public String getKey(){
		return String.valueOf(this.loveId) ;
	}
	
	private void initId(){
		idSet.clear();
		if(Util.isEmpty(ids)){
			return ;
		}
		String[] idArr = Util.splitString(ids);
		for(String id : idArr){
			if(null == id){
				continue ;
			}
			if(!Util.isNumber(id.trim())){
				Log4jManager.CHECK.error("HeroLove ids not number, heroId=" + heroId + " loveType" + loveType);
				Log4jManager.checkFail();
				continue ;
			}
			idSet.add(Integer.parseInt(id.trim()));
		}
		intIdArr = new int[idSet.size()];
		int index = 0 ;
		for(int id : idSet){
			intIdArr[index++] = id ;
		}
	}
	
	private void initDesc() {
		String template = this.getDescTemplate() ;
		if(Util.isEmpty(template)){
			this.desc = "" ;
			return ;
		}
		StringBuilder buffer = new StringBuilder();
		String cat = "" ;
		for(int id : this.idSet){
			String name = this.getName(id);
			if(Util.isEmpty(name)){
				continue ;
			}
			buffer.append(cat);
			buffer.append(name);
			cat = "," ;
		}
		this.desc = MessageFormat.format(template, buffer.toString());
	}
	
	private String getName(int id){
		if(HeroLoveType.horse.getType() == this.loveType){
			HorseBase hb = GameContext.getHorseApp().getHorseBaseById(id);
			return (null == hb)?"" : "[\\C]FFFFDD77[C]" + hb.getName() + "[\\C]FFFFFFFF[C]" ;
		}
		if(HeroLoveType.hero.getType() == this.loveType
				|| HeroLoveType.pet.getType() == this.loveType){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(id);
			return (null == gb)?"" : "[\\C]FFFFDD77[C]" + gb.getName() + "[\\C]FFFFFFFF[C]" ;
		}
		return "" ;
	}
	
	private String getDescTemplate(){
		if(HeroLoveType.horse.getType() == this.loveType){
			return GameContext.getI18n().getText(TextId.Hero_love_horse_type_desc);
		}
		if(HeroLoveType.hero.getType() == this.loveType){
			return GameContext.getI18n().getText(TextId.Hero_love_hero_type_desc);
		}
		if(HeroLoveType.pet.getType() == this.loveType){
			return GameContext.getI18n().getText(TextId.Hero_love_pet_type_desc);
		}
		return "" ;
	}
	
	public void init(){
		this.initId();
		this.initDesc(); 
	}
	
}
