package com.game.draco.app.skill.vo.scope;

import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

/**
 * T形区域
 */
public class TShape extends Area{
	private final static String CAT = "_" ;
	
	public TShape(int originX1,int originY1,int originX2,int originY2,int originX3,int originY3,int rectangleWidth,int rectangleHeight,Direction dir){
		if(rectangleWidth <=0 || rectangleHeight <=0){
			throw new java.lang.RuntimeException("rectangleWide or rectangleLong must >0") ;
		}
		this.originX1 = originX1 ;
		this.originY1 = originY1 ;
		this.originX2 = originX2 ;
		this.originY2 = originY2 ;
		this.originX3 = originX3 ;
		this.originY3 = originY3 ;
		this.rectangleWidth = rectangleWidth ;
		this.rectangleHeight = rectangleHeight ;
		this.dir = dir;
	}
	
	/**矩形起始X1坐标*/
	private int originX1 ;
	/**矩形起始Y1坐标*/
	private int originY1 ;
	/**矩形起始X2坐标*/
	private int originX2 ;
	/**矩形起始Y2坐标*/
	private int originY2 ;
	/**矩形起始X3坐标*/
	private int originX3 ;
	/**矩形起始Y3坐标*/
	private int originY3 ;
	/**宽度*/
	private int rectangleWidth;
	/**高度*/
	private int rectangleHeight;
	/**方向*/
	private Direction dir;

	@Override
	public boolean inArea(AbstractRole attacker, int x,int y, byte dir) {
		if(this.dir == Direction.UP || this.dir == Direction.DOWN) {
			if(Util.inRectangle(originX1, originY1, x, y, rectangleWidth, rectangleHeight)) {
				return true;
			}
			if(Util.inRectangle(originX2, originY2, x, y, rectangleHeight, rectangleWidth)) {
				return true ;
			}
			if(Util.inRectangle(originX3, originY3, x, y, rectangleHeight, rectangleWidth)) {
				return true;
			}
		}else{
			if(Util.inRectangle(originX1, originY1, x, y, rectangleHeight, rectangleWidth)) {
				return true ;
			}
			if(Util.inRectangle(originX2, originY2, x, y, rectangleWidth, rectangleHeight)) {
				return true;
			}
			if(Util.inRectangle(originX3, originY3, x, y, rectangleWidth, rectangleHeight)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getKey() {
		return "T" +CAT + originX1 + CAT + originY1 + CAT + originX2 + CAT + originY2 + CAT + originX3 + CAT + originY3 + CAT + rectangleWidth + CAT + rectangleHeight ;
	}

    @Override
	 public Point getCenterPoint() {
         return null;
     }

}
