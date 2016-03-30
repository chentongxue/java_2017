package com.game.draco.app.buff.stat;

import java.util.Date;

import com.game.draco.app.buff.Buff;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.vo.AbstractRole;

public @Data class BuffStat {
	protected AbstractRole owner;
	protected AbstractRole caster;
	protected String casterRoleId = "" ;
	protected short buffId;
	protected int remainTime;// 剩余(单位毫秒)时间或者叫持续时间
	protected long lastExecuteTime;// 上次执行时间
	protected Date createTime;
	protected int buffLevel;
	protected int intervalTime ; //间隔时间(单位毫秒)
	protected int buffSeries;//buff系列id
	protected Buff buff;
	/**buff相关信息,存储吸收余量,状态,或者修改属性*/
	private String buffInfo ;
	public BuffStat(Buff buff,int buffLevel,int intervalTime){
		this.buff = buff;
		this.buffLevel = buffLevel ;
		this.intervalTime = intervalTime ;
	}
	
	public boolean isTimeOver(long now){
		/*return (lastExecuteTime - createTime.getTime() + buff
				.getEffectIntervalTime()) > buff.getPersistTime() ? true
				: false;*/
		return now - lastExecuteTime >= this.remainTime;
	}
	
	public AttriBuffer getAttriBuffer(){
		return buff.getEffectType().getEffect().getAttriBuffer(this);
	}
	
	public void setCaster(AbstractRole caster){
		this.caster = caster ;
		if(null != caster){
			this.casterRoleId = caster.getRoleId();
		}
	}
	
	public String getCasterRoleId(){
		if(null == this.casterRoleId){
			return "" ;
		}
		return this.casterRoleId ;
	}
}
