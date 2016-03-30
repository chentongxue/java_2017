package com.game.draco.app.luckybox.config;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
/**
 * 刷新时间配置
 */
public @Data class LuckyBoxRefreshConfig implements Comparable<LuckyBoxRefreshConfig>{
	private String refreshTime;//刷新时间

	
	//
	private int hour;
	private int minutes;
	@Override
	public int compareTo(LuckyBoxRefreshConfig o) {
		int diff = hour - o.hour;
		if(diff != 0)
			return diff;
		return minutes - o.minutes;
	}
	public void init(){
		try{
			String[] ss = refreshTime.split(":");
			hour = Integer.parseInt(ss[0]);
			minutes = Integer.parseInt(ss[1]);
		}catch(Exception e){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("luckybox LuckyBoxRefreshConfig init fail, the refreshTime is " + refreshTime);
		}
	}
}
