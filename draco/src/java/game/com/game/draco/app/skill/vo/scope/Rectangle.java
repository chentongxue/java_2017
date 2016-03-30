//package com.game.draco.app.skill.vo.scope;
//
//import sacred.alliance.magic.base.Direction;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.vo.AbstractRole;
//import sacred.alliance.magic.vo.Point;
//
///**
// * 矩形区域
// */
//public class Rectangle implements Area{
//	private final static String CAT = "_" ;
//	
//	public Rectangle(int attackerX,int attackerY,int targetX,
//			int targetY, int rectangleWidth,int rectangleHeight, Direction dir){
//		if(rectangleWidth <=0 || rectangleHeight <=0){
//			throw new java.lang.RuntimeException("rectangleWide or rectangleLong must >0") ;
//		}
//		this.startX = attackerX ;
//		this.startY = attackerY ;
//		this.rectangleWidth = rectangleWidth ;
//		this.rectangleHeight = rectangleHeight ;
//		this.getEndPoint(targetX, targetY, dir);
//	}
//	
//	/**矩形起始边中心X坐标*/
//	private int startX ;
//	/**矩形起始边中心Y坐标*/
//	private int startY ;
//	/**矩形结束边中心X坐标*/
//	private int endX ;
//	/**矩形结束边中心Y坐标*/
//	private int endY ;
//	/**宽度*/
//	private int rectangleWidth;
//	/**高度*/
//	private int rectangleHeight;
//
//	@Override
//	public boolean inArea(AbstractRole attacker, int x,int y, Direction dir) {
//		int minX = startX, minY = startY, maxX = endX, maxY = endY;
//		if(startX > endX) {
//			minX = endX;
//			maxX = startX;
//		}
//		if(startY > endY) {
//			minY = endY;
//			maxY = startY;
//		}
//		
//		if(x < minX || x > maxX) {
//			return false;
//		}
//		
//		if(y < minY || y > maxY) {
//			return false;
//		}
//		
//		//x,y到矩形中心的距离 < rectHeight / 2 则在范围内
//		//点到直线距离(fabs((y2 - y1) * x0 +(x1 - x2) * y0 + ((x2 * y1) -(x1 * y2)))) / (sqrt(pow(y2 - y1, 2) + pow(x1 - x2, 2)))
//		float factor1 = (endY - startY) * x + (startX - endX) * y + endX * startY - startX * endY;
//		int disY = (endY - startY), disX = startX - endX;
//		float factor2 = disY * disY + disX * disX;
//		
//		if(factor1 * factor1 > (factor2 * rectangleHeight * rectangleHeight / 4)) {
//			return false;
//		}
//		
//		return true;
//	}
//
//	@Override
//	public String getKey() {
//		return "Rectangle" +CAT + startX + CAT + startY + CAT + rectangleWidth + CAT + rectangleHeight ;
//	}
//
//    @Override
//	 public Point getCenterPoint() {
//    	return new Point("",startX,startY);
//     }
//
//	@Override
//	public Direction getDir() {
//		return null;
//	}
//	
//	/**
//	 * 返回矩形范围最远边的中心点
//	 * @return
//	 */
//	private void getEndPoint(int targetX, int targetY, Direction dir) {
//		//矩形水平向右
//		if(targetX > startX && targetY == startY) {
//			endX = startX + rectangleWidth;
//			endY = startY;
//			return ;
//		}
//		
//		//矩形朝向右下
//		if(targetX > startX && targetY > startY) {
//			int dis = Util.distance(startX, startY, targetX, startY);
//			int yDis = (targetY - startY) * rectangleWidth / dis;
//			int xDis = (targetX - startX) * rectangleWidth / dis;
//			endX = startX + xDis;
//			endY = startY + yDis;
//			return ;
//		}
//		
//		//矩形朝向向下
//		if(targetX == startX && targetY > startY) {
//			endX = startX;
//			endY = startY + rectangleWidth;
//			return ;
//		}
//		
//		//矩形朝向左下
//		if(targetX < startX && targetY > startY) {
//			int dis = Util.distance(startX, startY, targetX, startY);
//			int yDis = (targetY - startY) * rectangleWidth / dis;
//			int xDis = (startX - targetX) * rectangleWidth / dis;
//			Point endPoint = new Point();
//			endX = startX - xDis;
//			endY = startY + yDis;
//			return ;
//		}
//		
//		//矩形水平向左
//		if(targetX < startX && targetY == startY) {
//			Point endPoint = new Point();
//			endX = startX - rectangleWidth;
//			endY = startY;
//			return ;
//		}
//		
//		//矩形朝向左上
//		if(targetX < startX && targetY < startY) {
//			int dis = Util.distance(startX, startY, targetX, startY);
//			int yDis = (startY - targetY) * rectangleWidth / dis;
//			int xDis = (startX - targetX) * rectangleWidth / dis;
//			Point endPoint = new Point();
//			endX = startX - xDis;
//			endY = startY - yDis;
//			return ;
//		}
//		
//		//矩形朝向向上
//		if(targetX == startX && targetY < startY) {
//			Point endPoint = new Point();
//			endX = startX;
//			endY = startY - rectangleWidth;
//			return ;
//		}
//		
//		//矩形朝向右上
//		if(targetX > startX && targetY < startY) {
//			int dis = Util.distance(startX, startY, targetX, startY);
//			int yDis = (startY - targetY) * rectangleWidth / dis;
//			int xDis = (targetX - startX) * rectangleWidth / dis;
//			Point endPoint = new Point();
//			endX = startX + xDis;
//			endY = startY - yDis;
//			return ;
//		}
//		
//		if(targetX == startX && targetY == startY) {
//			//攻击者和防御者在一点上
//			if(dir == Direction.LEFT){
//				Point endPoint = new Point();
//				endX = startX - rectangleWidth;
//				endY = startY;
//				return ;
//			}
//			
//			if(dir == Direction.RIGHT){
//				Point endPoint = new Point();
//				endX = startX + rectangleWidth;
//				endY = startY;
//				return ;
//			}
//			
//			if(dir == Direction.UP){
//				Point endPoint = new Point();
//				endX = startX;
//				endY = startY - rectangleWidth;
//				return ;
//			}
//			
//			if(dir == Direction.DOWN){
//				Point endPoint = new Point();
//				endX = startX;
//				endY = startY + rectangleWidth;
//				return ;
//			}
//		}
//		
//	}
//	
//}
