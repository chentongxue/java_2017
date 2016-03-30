package com.game.draco.app.copy.domain;

import java.util.Date;

import com.game.draco.GameContext;
import com.game.draco.app.copy.CopyConfig;
import com.game.draco.app.copy.CopyCountType;

import lombok.Data;
import sacred.alliance.magic.util.DateUtil;

public @Data class CopyCount {
	
	public static final String ROLEID = "roleId";
	public static final String COPYID = "copyId";
	
	private int roleId;//角色ID
	private short copyId;//副本ID
	private int enterNum;//进入次数
	private Date updateTime;//更新时间
	
	/**
	 * 获取今日副本进入次数
	 * @return
	 */
	public int getCurrCount(){
		this.reset();
		return this.enterNum;
	}
	
	private void reset(){
		Date now = new Date();
		//如果是每周类型，则判断是否是同一周；否则判断是否是同一天。
		if(CopyCountType.Weekly == this.getCopyCountType()){
			if(DateUtil.isSameWeek(this.updateTime, now)){
				return;
			}
		}else if(DateUtil.sameDay(this.updateTime, now)){
			return;
		}
		this.enterNum = 0;
		this.updateTime = now;
	}
	
	private CopyCountType getCopyCountType(){
		CopyConfig config = GameContext.getCopyLogicApp().getCopyConfig(this.copyId);
		if(null != config){
			return config.getCopyCountType();
		}
		return null;
	}
	
}
