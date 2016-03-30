package com.game.draco.app.skill.vo.scope;

import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;

/**
 * 矩形区域
 */
public class Cross implements Area{
	private final static String CAT = "_" ;
	
	public Cross(int originX1,int originY1,int originX2,int originY2,int rectangleWidth,int rectangleHeight){
		if(rectangleWidth <=0 || rectangleHeight <=0){
			throw new java.lang.RuntimeException("rectangleWide or rectangleLong must >0") ;
		}
		this.originX1 = originX1 ;
		this.originY1 = originY1 ;
		this.originX2 = originX2 ;
		this.originY2 = originY2 ;
		this.rectangleWidth = rectangleWidth ;
		this.rectangleHeight = rectangleHeight ;
	}
	
	/**矩形起始X1坐标*/
	private int originX1 ;
	/**矩形起始Y1坐标*/
	private int originY1 ;
	/**矩形起始X2坐标*/
	private int originX2 ;
	/**矩形起始Y2坐标*/
	private int originY2 ;
	/**宽度*/
	private int rectangleWidth;
	/**高度*/
	private int rectangleHeight;

	@Override
	public boolean inArea(int x, int y, Direction dir) {
		if(Util.inRectangle(originX1, originY1, x, y, rectangleWidth, rectangleHeight)) {
			return true ;
		}
		if(Util.inRectangle(originX2, originY2, x, y, rectangleHeight, rectangleWidth)) {
			return true;
		}
		return false;
	}

	@Override
	public String getKey() {
		return "Cross" +CAT + originX1 + CAT + originY1 + CAT + originX2 + CAT + originY2 + CAT + rectangleWidth + CAT + rectangleHeight ;
	}

    @Override
	 public Point getCenterPoint() {
         return null;
     }

	@Override
	public Direction getDir() {
		return null;
	}

	
}
