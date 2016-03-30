package com.game.draco.app.skill.vo.scope;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

/**
 * 弧度区域
 * @author tiefengKuang 
 * @date 2009-12-10 
 * @version 0.0.0.1
 * @changeLog
 *  1) create: 2009-12-10
 */
public class Radian extends Area{
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
	public boolean inArea(AbstractRole attacker, int x, int y, byte dir) {
		return Util.inCircle(centerX, centerY, x, y, radius);
//		if(maxDegrees - minDegrees == 360){
//			//圆
//			if(!Util.inCircle(centerX, centerY, x, y, radius)){
//				return false ;
//			}
//			return Util.inCircle(centerX, centerY, x, y, radius) ;
//		}
//		
//		//弧度计算
//		Direction attackerDir = attacker.getDir();
//		int ux = 0, uy = 0;
//		if(attackerDir == Direction.LEFT){
//			ux = -1;
//		}else if(attackerDir == Direction.RIGHT){
//			ux = 1;
//		}else if(attackerDir == Direction.UP){
//			uy = -1;
//		}else if(attackerDir == Direction.DOWN){
//			uy = 1;
//		}
		
//		// D = P - C
//		int dx = x - centerX;
//		int dy = y - centerY;
//		
//		// |D|^2 = (dx^2 + dy^2)
//		int squaredLength = dx * dx + dy * dy;
//		
//		// |D|^2 > r^2
//	    if (squaredLength > radius * radius)
//	        return false;
//	    
//	    // D dot U
//	    int ddotU = dx * ux + dy * uy;
	    
	    // D dot U > |D| cos(theta)
	    // <=>
	    // (D dot U)^2 > |D|^2 (cos(theta))^2 if D dot U >= 0 and cos(theta) >= 0
	    // (D dot U)^2 < |D|^2 (cos(theta))^2 if D dot U <  0 and cos(theta) <  0
	    // true                               if D dot U >= 0 and cos(theta) <  0
	    // false                              if D dot U <  0 and cos(theta) >= 0
//	    int disDegree = (maxDegrees - minDegrees) / 2;
//	    double cosTheta = Math.cos(Math.toRadians(disDegree));
//	    if (ddotU >= 0 && cosTheta >= 0)
//	        return ddotU * ddotU > squaredLength * cosTheta * cosTheta;
//	    else if (ddotU < 0 && cosTheta < 0)
//	        return ddotU * ddotU < squaredLength * cosTheta * cosTheta;
//	    else
//	        return ddotU >= 0;
	}

	@Override
	public String getKey() {
		return "Radian" +CAT + centerX + CAT + centerY + CAT + minDegrees + CAT + maxDegrees ;
	}

    @Override
	 public Point getCenterPoint() {
         return new Point("",centerX,centerY);
     }

}
