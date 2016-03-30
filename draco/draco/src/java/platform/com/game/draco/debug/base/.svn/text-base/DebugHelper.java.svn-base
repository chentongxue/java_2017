package com.game.draco.debug.base;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.BroadcastItem;
import com.game.draco.debug.message.item.RateItem;

import sacred.alliance.magic.domain.Rate;
import sacred.alliance.magic.domain.SysAnnouncement;

public class DebugHelper {
	
	/**
	 * 倍率列表
	 * @return
	 */
	public static List<RateItem> getRateList(){
		List<RateItem> rateList = new ArrayList<RateItem>();
		for(Rate rate : GameContext.getRateApp().getRateList()){
			if(null == rate){
				continue;
			}
			RateItem item = new RateItem();
			item.setType(rate.getType());
			item.setStartTime(rate.getStartTime());
			item.setEndTime(rate.getEndTime());
			item.setRate(rate.getRate());
			item.setRate1(rate.getRate1());
			rateList.add(item);
		}
		return rateList;
	}
	
	/**
	 * 系统广播列表
	 * @return
	 */
	public static List<BroadcastItem> getBroadcastList(){
		List<BroadcastItem> broadcaseList = new ArrayList<BroadcastItem>();
		for(SysAnnouncement cast : GameContext.getAnnounceApp().getAnnounceMap().values()){
			if(null == cast){
				continue;
			}
			BroadcastItem item = new BroadcastItem();
			item.setId(cast.getId());
			item.setContent(cast.getContent());
			item.setBeginTime(cast.getStartTime().getTime());
			item.setEndTime(cast.getEndTime().getTime());
			item.setGa(cast.getTimeGap());
			item.setState((byte)cast.getState());
			broadcaseList.add(item);
		}
		return broadcaseList;
	}
	
}
