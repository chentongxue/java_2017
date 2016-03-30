package com.game.draco.app.camp.war.vo;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;

import com.game.draco.GameContext;
import com.game.draco.base.CampType;

public @Data class CampMatchGroup {
	private final static int PRE_RUN =  0 ;
	private final static int RUN = 1 ;
	private final static int OVER = 2 ;
	
	private AtomicInteger[] curHp = new AtomicInteger[]{};
	private AtomicInteger status = new AtomicInteger(0);
	private int maxConfigHp = 1000 ;
	private Date createOn = new Date() ;
	private byte winCampId = -1 ;
	
	
	public CampMatchGroup(){
		this.init(this.maxConfigHp);
	}
	
	public int getLiveNum(){
		int num = 0 ;
		for(AtomicInteger hp : curHp){
			if(hp.get()>0){
				num ++ ;
			}
		}
		return num ;
	}
	
	public int addHp(int campId,int hp){
		AtomicInteger value = this.curHp[campId] ;
		int currentValue = value.get();
		if(currentValue <=0){
			//已经死亡
			return currentValue ;
		}
		return value.addAndGet(hp);
	}
	
	public byte whichCampWin(boolean timeover){
		if( -1 != this.winCampId){
			return this.winCampId ;
		}
		//timeover取hp最多的
		if(timeover){
			byte win = 0 ;
			int maxHp = 0 ;
			for(byte i =0 ;i< curHp.length;i++){
				if(curHp[i].get() > maxHp){
					maxHp = curHp[i].get() ;
					win = i ;
				}
			}
			this.winCampId = win ;
			return this.winCampId ;
		}
		//只一个阵营活着才分出胜负
		byte win = 0 ;
		int zeroNum = 0 ;
		for(byte i =0 ;i< curHp.length;i++){
			if(curHp[i].get() <=0){
				zeroNum ++ ;
			}else{
				win = i ;
			}
		}
		if(zeroNum >= curHp.length-1){
			this.winCampId = win ;
			return this.winCampId ;
		}
		//未分出胜负
		return -1 ;
	}
	
	public void over(){
		this.status.set(OVER);
	}
	
	public boolean isOver(){
		return OVER == this.status.get() ;
	}
	
	public boolean isRun(){
		return RUN == this.status.get() ;
	}
	
	public boolean toStart(){
		return this.status.compareAndSet(PRE_RUN, RUN);
	}
	
	public int reduHp(int campId,int hp){
		AtomicInteger value = this.curHp[campId] ;
		int currentValue = value.get();
		if(currentValue <=0){
			//已经死亡
			return currentValue ;
		}
		return value.addAndGet(-hp);
	}
	
	public int getHp(int campId){
		return Math.max(0, this.curHp[campId].get());
	}
	
	
	private void initMaxHp(int configMaxHp){
		int campNum = CampType.getRealCampNum();
		curHp = new AtomicInteger[campNum];
		for(int i=0;i<campNum;i++){
			curHp[i] = new AtomicInteger(configMaxHp);
		}
		this.maxConfigHp = configMaxHp ;
	}
	
	public void init(int configMaxHp) {
		this.initMaxHp(configMaxHp);
	}
}
