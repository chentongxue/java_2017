package com.game.draco.app.buff;

public class BuffEffect{
	/**buffId*/
	protected short buffId ;
	/**buff级别(等于0时为删除buff效果)*/
	protected int buffLv ;
	
	public BuffEffect(short buffId,int buffLv){
		this.buffId = buffId ;
		this.buffLv = buffLv ;
	}
	
	/**buff级别(等于0时为删除buff效果)*/
	public BuffEffect(short buffId){
		this(buffId,0);
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
	
}
