package com.game.draco.app.skill.vo.scope;

import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;

/**
 * 弧度区域
 * @author tiefengKuang 
 * @date 2009-12-10 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-12-10
 */
public class Radian implements Area{
	private final static String CAT = "_" ;
	public Radian(int centerX,int centerY,int radius){
		this(centerX,centerY,radius,0,360);
	}
	
	public Radian(int centerX,int centerY,int radius,int minDegrees,int maxDegrees){
		if(radius <=0){
			throw new java.lang.RuntimeException("radius must >0") ;
		}
		this.radius = radius ;
		this.centerX = centerX ;
		this.centerY = centerY ;
		this.minDegrees = minDegrees ;
		this.maxDegrees = maxDegrees ;
	}
	
	/**圆心X坐标*/
	private int centerX ;
	/**圆心Y坐标*/
	private int centerY ;
	/**半径*/
	private int radius ;
	/**起始角度*/
	private int minDegrees = 0  ;
	/**结束角度*/
	private int maxDegrees = 360;
	public int getCenterX() {
		return centerX;
	}
	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}
	public int getCenterY() {
		return centerY;
	}
	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public int getMinDegrees() {
		return minDegrees;
	}
	public void setMinDegrees(int minDegrees) {
		this.minDegrees = minDegrees;
	}
	public int getMaxDegrees() {
		return maxDegrees;
	}
	public void setMaxDegrees(int maxDegrees) {
		this.maxDegrees = maxDegrees;
	}

	@Override
	public boolean inArea(int x, int y, Direction dir) {
		if(!Util.inCircle(centerX, centerY, x, y, radius)){
			return false ;
		}
		if(maxDegrees - minDegrees == 360){
			//圆
			return true ;
		}
		//弧度计算
		//判断象限
		int quadrant = 1;
		if(centerX<=x&&centerY>=y)quadrant=1;
		else if(centerX<=x&&centerY<=y)quadrant=2;
		else if(centerX>=x&&centerY<=y)quadrant=3;
		else if(centerX>=x&&centerY>=y)quadrant=4;
		
		if(dir == Direction.LEFT){
			if(quadrant==3||quadrant==4)return true;
			return false;
		}else if(dir == Direction.RIGHT){
			if(quadrant==1||quadrant==2)return true;
			return false;
		}else if(dir == Direction.UP){
			if(quadrant==1||quadrant==4)return true;
			return false;
		}else if(dir == Direction.DOWN){
			if(quadrant==2||quadrant==3)return true;
			return false;
		}
		return true;
	}

	@Override
	public String getKey() {
		return "Radian" +CAT + centerX + CAT + centerY + CAT + minDegrees + CAT + maxDegrees ;
	}

    @Override
	 public Point getCenterPoint() {
         return new Point("",centerX,centerY);
     }

	@Override
	public Direction getDir() {
		return null;
	}
}
