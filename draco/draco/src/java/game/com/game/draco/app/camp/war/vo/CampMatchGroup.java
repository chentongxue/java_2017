package com.game.draco.app.camp.war.vo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import sacred.alliance.magic.util.RandomUtil;

import com.game.draco.GameContext;
import com.game.draco.base.CampType;
import com.google.common.collect.Lists;

public @Data class CampMatchGroup {
	private byte[] match =  null ;
	private byte[] endFlag = null ;
	private AtomicBoolean status = new AtomicBoolean(false) ;
	private AtomicInteger[] maxHp = new AtomicInteger[]{};
	private int maxConfigHp = 1000 ;
	
	public CampMatchGroup(){
		//!!!! 构造函数中不能调用init方法
		//因为阵营首领的hp是由活动开启时刻最近x天内登录的角色数决定的。
		//this.init();
	}
	
	public boolean isEnd(int campId){
		return endFlag[campId] == 1 ;
	}
	
	public boolean isGroupEnd(int campId){
		return this.isEnd(campId) 
				|| this.isEnd(this.getTargetCampId(campId)) ;
	}
	
	public void flagEnd(int campId){
		endFlag[campId] = 1 ;
	}
	
	public void flagGroupEnd(int campId){
		this.flagEnd(campId);
		this.flagEnd(this.getTargetCampId(campId));
	}
	
	public int addHp(int campId,int hp){
		AtomicInteger value = this.maxHp[campId] ;
		int currentValue = value.get();
		if(currentValue <=0){
			//已经死亡
			return currentValue ;
		}
		return value.addAndGet(hp);
	}
	
	public int reduHp(int campId,int hp){
		AtomicInteger value = this.maxHp[campId] ;
		int currentValue = value.get();
		if(currentValue <=0){
			//已经死亡
			return currentValue ;
		}
		return value.addAndGet(-hp);
	}
	
	public int reduTargetHp(int campId,int hp){
		int targetCampId = this.getTargetCampId(campId);
		return this.reduHp(targetCampId, hp);
	}
	
	public int getHp(int campId){
		return Math.max(0, this.maxHp[campId].get());
	}
	
	public int getTargetHp(int campId){
		int targetCampId = this.getTargetCampId(campId);
		return this.getHp(targetCampId);
	}
	
	public byte getTargetCampId(int campId){
		return this.match[campId];
	}
	
	public void reset(){
		this.status.set(false);
		for(int i=0;i<maxHp.length;i++){
			maxHp[i].set(0);
		}
		for(int i=0;i<endFlag.length;i++){
			endFlag[i] = 0 ;
		}
		this.maxConfigHp = 1000 ;
	}
	
	private void initMaxHp(){
		int campNum = CampType.getRealCampNum();
		int configMaxHp = GameContext.getCampWarApp().getLeaderConfigMaxHp();
		maxHp = new AtomicInteger[campNum];
		for(int i=0;i<campNum;i++){
			maxHp[i].set(configMaxHp);
		}
		this.maxConfigHp = configMaxHp ;
	}
	
	private void initMatch(){
		int campNum = CampType.getRealCampNum();
		this.match = new byte[campNum];
		this.endFlag = new byte[campNum]; //标识为都未结束
		for (int c = 0; c < 3; c++) {
			List<Byte> list = Lists.newArrayList();
			for (byte i = 0; i < campNum; i++) {
				list.add(i);
			}
			for (int r = 0; r < campNum / 2 * (campNum - 1); r++) {
				Collections.sort(list, new Comparator<Byte>() {
					@Override
					public int compare(Byte o1, Byte o2) {
						return RandomUtil.absRandomInt(3) - 1;
					}
				});
			}
			for (int i = 0; i < list.size(); i = i + 2) {
				byte a = list.get(i);
				byte b = list.get(i + 1);
				this.match[a] = b;
				this.match[b] = a;
			}
		}
	}
	
	public void init() {
		if (status.compareAndSet(false, true)) {
			this.initMatch();
			this.initMaxHp();
		}
	}
}
