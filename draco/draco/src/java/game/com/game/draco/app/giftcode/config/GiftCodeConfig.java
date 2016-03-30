package com.game.draco.app.giftcode.config;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;

public @Data class GiftCodeConfig {
	private static final String ALL = "-1" ;
	private int activeId;
	private int channelId = -1 ;
	private String context;
	private String beginShowDate ;
	private String beginDate;
	private String endDate;
	private int rewardGoodsId;
	private int silverMoney;
	private int bindingGoldMoney;
	private int goldMoney;
	private String serverId = "" ;
	
	private Date startShowDate ;
	private Date startDate;
	private Date overDate;
	
	private GoodsBase goodsBase = null ;
	private Set<String> serverIdSet = new HashSet<String>();
	
	public boolean nowShow(int channelId,int serverId){
		long now = System.currentTimeMillis() ;
		boolean time =  now >= this.startShowDate.getTime()
			&& now <= this.overDate.getTime() ;
		if(!time){
			return false ;
		}
		return (this.channelId < 0 || this.channelId == channelId )
				&& this.isServerCanShow(serverId);
	}
	
	public boolean nowOpen(int channelId,int serverId){
		long now = System.currentTimeMillis() ;
		boolean time =   now >= this.startDate.getTime()
			&& now <= this.overDate.getTime() ;
		if(!time){
			return false ;
		}
		return (this.channelId < 0 || this.channelId == channelId )
			&& this.isServerCanShow(serverId);
	}
	
	public String init(){
		StringBuffer buffer = new StringBuffer("");
		this.startShowDate = this.resolveStringDate(beginShowDate, 0, 0);
		this.startDate = this.resolveStringDate(beginDate, 0, 0);
		this.overDate = this.resolveStringDate(endDate, 23, 59);
		if(null == this.startShowDate
				|| null == this.startDate
				|| null == this.overDate){
			buffer.append("giftCode  date config error activeId=" + this.activeId);
		}
		this.goodsBase = GameContext.getGoodsApp().getGoodsBase(rewardGoodsId);
		if(null == goodsBase){
			buffer.append("giftCode config error,goods not exist,goodsId=" 
					+ this.rewardGoodsId + " activeId=" + this.activeId);
		}
		if(!Util.isEmpty(this.serverId)){
			String[] serverIds = this.serverId.trim().split(Cat.comma);
			for(String s : serverIds){
				if(Util.isEmpty(s)){
					continue ;
				}
				this.serverIdSet.add(s.trim());
			}
		}
		return buffer.toString();
	}
	
	
	/**
	 * 解析时间字符串，格式为（2012-6-13）
	 */
	private Date resolveStringDate(String date, int hour, int minute){
		if(Util.isEmpty(date)){
			return null;
		}
		String[] dateTime = date.split(Cat.strigula);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.parseInt(dateTime[0]));
		calendar.set(Calendar.MONTH, Integer.parseInt(dateTime[1]) - 1);
		calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateTime[2]));
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		return calendar.getTime();
	}
	
	
	private boolean isServerCanShow(int serverId){
		return this.serverIdSet.contains(ALL) 
			|| this.serverIdSet.contains(String.valueOf(serverId));
	}
}
