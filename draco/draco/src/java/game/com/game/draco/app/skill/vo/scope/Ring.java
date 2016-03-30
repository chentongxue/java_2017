package com.game.draco.app.skill.vo.scope;

import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;

public class Ring implements Area{
	private final static String CAT = "_" ;
	
	private int centerX ;
	private int centerY ;
	private int minRadius ;
	private int maxRadius ;

	public Ring(int centerX,int centerY,int minRadius,int maxRadius){
		this.centerX = centerX ;
		this.centerY = centerY ;
		this.minRadius = minRadius ;
		this.maxRadius = maxRadius ;
	}
	
	@Override
	public Point getCenterPoint() {
		return new Point("",centerX,centerY);
	}

	@Override
	public Direction getDir() {
		return null;
	}

	@Override
	public String getKey() {
		return "Ring" +CAT + centerX + CAT + centerY + CAT + minRadius + CAT + maxRadius ;
	}

	@Override
	public boolean inArea(int x, int y, Direction dir) {
		return ! Util.inCircle(centerX, centerY, x, y, minRadius) 
		         && Util.inCircle(centerX, centerY, x, y, maxRadius) ;
	}

}
