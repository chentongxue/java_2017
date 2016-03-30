package com.game.draco.app.skill.vo.scope;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

/**
 * 矩形区域
 */
public class Cross extends Area{
	private final static String CAT = "_" ;
	
	public Cross(int originX,int originY,int rectangleWidth,int rectangleHeight){
		if(rectangleWidth <=0 || rectangleHeight <=0){
			throw new java.lang.RuntimeException("rectangleWide or rectangleLong must >0") ;
		}
		this.originX = originX ;
		this.originY = originY ;
		this.rectangleWidth = rectangleWidth ;
		this.rectangleHeight = rectangleHeight ;
	}
	
	/**矩形起始X1坐标*/
	private int originX ;
	/**矩形起始Y1坐标*/
	private int originY ;
	/**宽度*/
	private int rectangleWidth; //以人方向 向上为准的矩形
	/**高度*/
	private int rectangleHeight;//以人方向 向上为准的矩形

	@Override
	public boolean inArea(AbstractRole attacker,int x, int y, byte dir) {
		//人物为圆心
		//矩形1为竖向 
		int	_originX1 = originX - rectangleWidth/2;
		int	_originY1 = originY - rectangleHeight/2;
		//矩形2为横向 高变宽 宽变高
		int	_originX2 = originX - rectangleHeight/2;
		int	_originY2 = originY - rectangleWidth/2;
		
		if(Util.inRectangle(_originX1, _originY1, x, y, rectangleWidth, rectangleHeight)){
			return true;
		}
		//矩形2为横向 高变宽 宽变高
		if(Util.inRectangle(_originX2, _originY2, x, y, rectangleHeight, rectangleWidth)){
			return true;
		}
		return false;
	}

	@Override
	public String getKey() {
		return "Cross" +CAT + originX + CAT + originY + CAT + rectangleWidth + CAT + rectangleHeight ;
	}

    @Override
	 public Point getCenterPoint() {
         return null;
     }

}
