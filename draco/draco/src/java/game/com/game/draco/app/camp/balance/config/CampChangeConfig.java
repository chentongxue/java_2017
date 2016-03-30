package com.game.draco.app.camp.balance.config;

import java.util.Date;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;


public @Data class CampChangeConfig{
	private int openDay;
	private int openChangeRate;
	private int goodsId;
	private int num;
	private String npcId;
	private Date openDate;
	private String desc;
	private String openDesc;
	private int level;
	private int winBoom;
	private int loseBoom;
	private String broadcast;
	private int broadcastHour;
	private int timeGap;
	
	public void init(){
		openDate = DateUtil.getStartDate(GameContext.gameStartDate, openDay);
		if(null == openDate){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("CampChangeConfig openDate config error!");
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("CampChangeConfig error! goodsId=" + goodsId + " not exist");
		}
	}
	
	public boolean isOpen(){
		if(null == openDate){
			return false;
		}
		Date now = new Date();
		if(now.before(openDate)){
			return false;
		}
		return true;
	}
}
