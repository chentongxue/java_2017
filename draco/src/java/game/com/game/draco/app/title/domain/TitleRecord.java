package com.game.draco.app.title.domain;

import java.util.Calendar;
import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.util.DateUtil;

import com.game.draco.GameContext;
import com.game.draco.app.title.TitleStatus;


public @Data class TitleRecord {
	public static final String TITLE_ID = "titleId" ;
	public static final String ROLE_ID = "roleId" ;
	
	private int titleId;//号称ID',
	private String roleId;//'色角ID',
	private byte activateState;//'活激状态(1:激活,0:未激活)',
	private Date dueTime;//到期时间',
	private Date useDate;//使用日期
	
	/**以下字段不入库*/
	//是否修改过
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm" ;
	
	
	
	public String getStrDueTime(){
		GoodsTitle gt = GameContext.getGoodsApp().getGoodsTemplate(GoodsTitle.class, titleId);
		if(gt.isPermanent()){
			return GameContext.getI18n().getText(TextId.Title_effecttime_permanent);
		}
		Date nowDate = new Date();
		if(nowDate.before(dueTime)){
			return DateUtil.date2Str(dueTime,DATE_FORMAT);
		}
		return GameContext.getI18n().getText(TextId.Title_effecttime_timeout);
		
	}
	
	public void update(){
		GameContext.getBaseDAO().update(this);
	}
	
	public void insert(){
		GameContext.getBaseDAO().insert(this);
	}
	
	public void delete(){
		GameContext.getBaseDAO().delete(TitleRecord.class, ROLE_ID, String.valueOf(this.getRoleId()),
				TITLE_ID, String.valueOf(this.getTitleId()));
	}
	
	/**过期*/
	public boolean isTimeout(){
		GoodsTitle gt = GameContext.getGoodsApp().getGoodsTemplate(GoodsTitle.class, titleId);
		if(null == gt){
			return true ;
		}
		if(gt.isPermanent()){
			return false;
		}
		if(null == dueTime){
			return true;
		}
		Date nowdate = new Date();
		if(nowdate.before(dueTime)){
			return false;
		}
		return true;
	}
	
	/**增加天数*/
	public void addDueTimeMinute(int minute){
		if(null == dueTime){
			return ;
		}
		if(isTimeout()){
			dueTime = new Date();
		}
		dueTime = DateUtil.add(dueTime, Calendar.MINUTE, minute);
	}
	
	/**激活状态*/
	public boolean isActivate(){
		return this.activateState == TitleStatus.Wear.getType() ;
	}
	
	/**激活称号*/
	public void activate(){
		activateState = TitleStatus.Wear.getType();
	}
	
	/**取消称号*/
	public void cancel(){
		activateState = TitleStatus.Have.getType();
	}
}
