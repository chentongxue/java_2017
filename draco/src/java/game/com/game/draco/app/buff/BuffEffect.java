package com.game.draco.app.buff;

public class BuffEffect{
	/**buffId*/
	protected short buffId ;
	/**buff级别(等于0时为删除buff效果)*/
	protected int buffLv ;
	/**buff持续时间 */
	protected int effectTime;
	/**附加信息*/
	protected Object info ;
	
	public BuffEffect(short buffId,int buffLv, int effectTime,Object info){
		this.buffId = buffId ;
		this.buffLv = buffLv ;
		this.effectTime = effectTime;
		this.info = info ;
	}
	
	public BuffEffect(short buffId,int buffLv, int effectTime){
		this(buffId,buffLv, effectTime,null);
	}
	
	/**buff级别(等于0时为删除buff效果)*/
	public BuffEffect(short buffId){
		this(buffId,0, 0,null);
	}
	
	public short getBuffId() {
		return buffId;
	}
	public void setBuffId(short buffId) {
		this.buffId = buffId;
	}
	public int getBuffLv() {
		return buffLv;
	}
	public void setBuffLv(int buffLv) {
		this.buffLv = buffLv;
	}

	public int getEffectTime() {
		return effectTime;
	}

	public void setEffectTime(int effectTime) {
		this.effectTime = effectTime;
	}

	public Object getInfo() {
		return info;
	}

	public void setInfo(Object info) {
		this.info = info;
	}
	
	
}
