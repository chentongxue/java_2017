package com.game.draco.app.buff.stat;

import java.util.Date;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.vo.AbstractRole;

import com.game.draco.app.buff.Buff;
import com.game.draco.app.skill.domain.RoleSkillStat;

public @Data class BuffStat {
	//自己
	protected AbstractRole owner;
	//谁加的buff就是谁
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
	
	/** buff执行技能当前次数 */
	private int execSkillTimes = 0;
	/** 变身产生的技能 */
	private Map<Short,RoleSkillStat> skillMap = null;
	
	/**
	 * 地图buff中心区域
	 */
	private Object contextInfo ;
	
	//层 叠层数 
	private short layer = 1;
	
	public BuffStat(Buff buff,int buffLevel,int intervalTime){
		this.buff = buff;
		this.buffLevel = buffLevel ;
		this.intervalTime = intervalTime ;
	}
	
	public boolean isTimeOver(long now){
		/*return (lastExecuteTime - createTime.getTime() + buff
				.getEffectIntervalTime()) > buff.getPersistTime() ? true
				: false;*/
		
		return ( now < lastExecuteTime 
				|| (now - lastExecuteTime >= this.remainTime)); 
		
		/*boolean value =( now < lastExecuteTime 
				|| (now - lastExecuteTime >= this.remainTime));
		if(buff.getBuffId() == 2076 && value){
			System.out.println("/////////////// now=" + now + " lastExecuteTime=" 
					+ lastExecuteTime + " remainTime=" + remainTime + " now-last=" + (now - lastExecuteTime));
		}
		return value ;*/
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
	
	public void addExecSkillTimes() {
		this.execSkillTimes++;
	}
	
	
}
