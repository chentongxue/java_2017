package com.game.draco.app.hero.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class HeroLove implements KeySupport<String>{

	private int heroId ;
	private byte loveType ;
	private String ids	;
	private String desc	;
	private String attriValue ;
	private String attriPercent ;
	
	private Set<String> idSet = new HashSet<String>() ;
	private List<AttriItem> attriItemList ;
	
	public boolean containId(String id){
		return (null != idSet && idSet.contains(id));
	}
 
	@Override
	public String getKey(){
		return heroId + "_" + loveType ;
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
			idSet.add(id.trim());
		}
	
	}
	
	public void init(){
		this.initId();
		this.initAttri();
	}
	
	private void initAttri(){
		List<AttriItem> arriItemList = new ArrayList<AttriItem>();
		this.initAttri(arriItemList, attriValue, true);
		this.initAttri(arriItemList, attriPercent, false);
		this.attriItemList = arriItemList ;
	}
	
	private void initAttri(List<AttriItem> arriItemList,String attri,boolean isValue){
		if(Util.isEmpty(attri)){
			return ;
		}
		String[] arr = Util.splitString(attri);
		if(0 != arr.length%2){
			Log4jManager.CHECK.error("HeroLove attri config error,heroId=" + this.heroId + " loveType=" + this.loveType);
			Log4jManager.checkFail();
			return ;
		}
		for(int i=0;i<arr.length;i=i+2){
			float f = Float.parseFloat(arr[i+1]) ;
			if(!isValue){
				f = f/RespTypeStatus.FULL_RATE ;
			}
			AttriItem item = new AttriItem(Byte.parseByte(arr[i]),f,!isValue);
			arriItemList.add(item);
		}
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
