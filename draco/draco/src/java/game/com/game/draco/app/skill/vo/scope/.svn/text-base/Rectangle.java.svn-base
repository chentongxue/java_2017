package com.game.draco.app.skill.vo.scope;

import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;

/**
 * 矩形区域
 */
public class Rectangle implements Area{
	private final static String CAT = "_" ;
	
	public Rectangle(int originX,int originY,int rectangleWidth,int rectangleHeight,Direction dir){
		if(rectangleWidth <=0 || rectangleHeight <=0){
			throw new java.lang.RuntimeException("rectangleWide or rectangleLong must >0") ;
		}
		this.originX = originX ;
		this.originY = originY ;
		this.rectangleWidth = rectangleWidth ;
		this.rectangleHeight = rectangleHeight ;
		this.dir = dir;
	}
	
	/**矩形起始X坐标*/
	private int originX ;
	/**矩形起始Y坐标*/
	private int originY ;
	/**宽度*/
	private int rectangleWidth;
	/**高度*/
	private int rectangleHeight;
	/**方向*/
	private Direction dir;

	@Override
	public boolean inArea(int x, int y, Direction dir) {
		if(!Util.inRectangle(originX, originY, x, y, rectangleWidth, rectangleHeight)){
			return false ;
		}
		return true;
	}

	@Override
	public String getKey() {
		return "Rectangle" +CAT + originX + CAT + originY + CAT + rectangleWidth + CAT + rectangleHeight ;
	}

    @Override
	 public Point getCenterPoint() {
    	return new Point("",originX,originY);
     }

	@Override
	public Direction getDir() {
		return dir;
	}
}
