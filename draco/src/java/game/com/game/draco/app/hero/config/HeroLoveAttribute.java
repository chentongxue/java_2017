package com.game.draco.app.hero.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Util;

public @Data class HeroLoveAttribute implements KeySupport<String>{

	private int loveId ;
	private byte quality ;
	private byte star ;
	private byte attriType ;
	private int attriValue ;
	private byte valueType ;
	private List<AttriItem> attriItemList ;
	private String desc = "";
	
	
	@Override
	public String getKey(){
		return this.loveId  + "_" + this.quality + "_" + this.star;
	}
	
	public void init(){
		this.initAttri();
		this.initDesc();
	}
	
	private void initDesc(){
		AttributeType at = AttributeType.get(attriType);
		if(null == at){
			this.desc = "" ;
			return ;
		}
		if(0 == valueType){
			this.desc =  at.getName() + " +" + this.attriValue ;
			return ;
		}
		this.desc =  at.getName() + " +" + this.attriValue/100 + "%" ;
		return ;
	}
	
	private void initAttri(){
		List<AttriItem> arriItemList = new ArrayList<AttriItem>();
		AttriItem item = new AttriItem() ;
		item.setAttriTypeValue(attriType);
		if(0 == valueType){
			item.setValue(this.attriValue);
		}else{
			item.setPrecValue(this.attriValue/RespTypeStatus.FULL_RATE);
		}
		arriItemList.add(item);
		this.attriItemList = arriItemList ;
	}
	
	public List<AttriItem> getAttriItemList(){
		if(Util.isEmpty(attriItemList)){
			return null ;
		}
		List<AttriItem> list = new ArrayList<AttriItem>();
		for(AttriItem ai : attriItemList){
			list.add(ai.clone());
		}
		return list;
	}
}
