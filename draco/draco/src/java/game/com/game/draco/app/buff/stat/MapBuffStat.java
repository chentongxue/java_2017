package com.game.draco.app.buff.stat;

import com.game.draco.app.buff.Buff;

import sacred.alliance.magic.vo.Point;


public class MapBuffStat extends BuffStat {

	/**
	 * µØÍ¼buffÐ§¹ûµã
	 */
	private Point effectPoint ;

	public MapBuffStat(Buff buff,int buffLevel,int intervalTime,Point effectPoint){
		super(buff,buffLevel,intervalTime);
		this.effectPoint = effectPoint ;
	}

	public Point getEffectPoint() {
		return effectPoint;
	}

	public void setEffectPoint(Point effectPoint) {
		this.effectPoint = effectPoint;
	}

	
	
}