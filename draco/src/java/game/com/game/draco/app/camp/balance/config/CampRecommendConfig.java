package com.game.draco.app.camp.balance.config;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;

public @Data class CampRecommendConfig {
	private int openDay;
	private int openTotalLevel;
	private int selectCampRoleLevel ;
	private int effectRoleLevel ;
	private int goodsId;
	private int bindType;
	private int openLevelRate;
	private Date openDate;
	private String tips ;
	
	public void init(){
		openDate = DateUtil.getStartDate(GameContext.gameStartDate, openDay);
		if(null == openDate){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("CampRecommendConfig openDate config error!");
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("CampRecommendConfig error! goodsId=" + goodsId + " not exist");
		}
	}
	
	public boolean isOpen(){
		if(null == openDate){
			return false;
		}
		Date now = new Date();
		if(now.after(openDate)){
			return false;
		}
		return true;
	}
}
