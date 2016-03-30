package com.game.draco.app.hero;

import lombok.Data;

public @Data class HeroLoveStatus {
	
	public static final byte un_activated = 0 ;
	public static final byte activated = 1 ;
	public static final byte not_open = -1 ;
	
	private int quality = -1 ;
	private int star = -1 ;
	private int reachNum = 0 ;
	private byte status = -1 ;
	private int targetId = 0 ;
	
	public void reset(int quality,int star,int targetId){
		this.quality = quality ;
		this.star = star ;
		this.reachNum = 1 ;
		this.targetId = targetId ;
	}
	
	public void incrReachNum(int targetId){
		if(targetId == this.targetId){
			return ;
		}
		this.reachNum++ ;
	}
	
	public void notOpen(){
		this.status = not_open ;
	}
	
	public void unActivated(){
		this.status = un_activated ;
	}
	
	public void activated(){
		this.status = activated ;
	}

	public boolean equal(int quality,int star){
		return quality == this.quality && star == this.star ;
	}
	
	public boolean isNull(){
		return -1 == this.quality  && -1 == this.star ;
	}

	public boolean greaterThan(int quality,int star){
		if(this.quality > quality){
			return true ;
		}
		if(this.quality < quality){
			return false ;
		}
		return this.star > star ;
	}
	
	public boolean lessThan(int quality,int star){
		if(this.quality < quality){
			return true ;
		}
		if(this.quality > quality){
			return false ;
		}
		return this.star < star ;
	}
}
