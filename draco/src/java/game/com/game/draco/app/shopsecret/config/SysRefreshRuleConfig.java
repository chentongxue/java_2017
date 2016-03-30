package com.game.draco.app.shopsecret.config;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.TimeSupport;
@Data
public class SysRefreshRuleConfig implements TimeSupport, KeySupport<String>,Comparable<SysRefreshRuleConfig>{
	private String shopId;
	private String refreshTime;
	
	//
	@Override
	public String getKey() {
		return String.valueOf(shopId);
	}
	private int hour;
	private int minute;
	public void init(){
		try{
			String[] ss = refreshTime.split(":");
			hour = Integer.parseInt(ss[0]);
			minute = Integer.parseInt(ss[1]);
		}catch(Exception e){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("shop secret SysRefreshRuleConfig init fail, the refreshTime is " + refreshTime);
		}
	}
	@Override
	public int compareTo(SysRefreshRuleConfig o) {
		int diff = hour - o.hour;
		if(diff != 0)
			return diff;
		return minute - o.minute;
	}
}
