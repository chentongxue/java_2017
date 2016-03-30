package sacred.alliance.magic.util;

import sacred.alliance.magic.vo.DefaultPoint;

public class PointUtil {
	/**
	 * 获取以目标为圆心，r为半径，面向方向的半圆内的随机坐标
	 * @param x	目标x坐标
	 * @param y 目标y坐标
	 * @param dir 目标面对方向
	 * @param r 目标半径长度
	 * @return
	 */
//	public static DefaultPoint randomPoint(int x,int y,Direction dir,int r){
//		int nx = getRandom(1,2*r);
//		int ny = getRandom(1,r);
//		switch(dir){
//			case UP:
//				return new DefaultPoint(x-nx,y-ny);
//			case DOWN:
//				return new DefaultPoint(x-nx,y+ny);
//			case LEFT:
//				return new DefaultPoint(x-ny,y-nx);
//			case RIGHT:
//				return new DefaultPoint(x+ny,y-nx);
//			default:
//				return new DefaultPoint(x,y);
//		}
//	}
	/**
	 * 获取以目标为圆心，r为半径，圆内的随机坐标
	 * @param x	目标x坐标
	 * @param y 目标y坐标
	 * @param r 目标半径长度
	 * @return
	 */
	public static DefaultPoint randomPoint(int x,int y,int r){
		if ((x - r) < 0) {
			r = x > y ? y : x;
		}
		else if ((y - r) < 0) {
			r = y > x ? x : y;
		}
		
		int startX = x - r;
		int endX = x + r;

		int startY = y - r;
		int endY = y + r;
		
		int random = (int) ((Math.random() * startX ) + startX);
		int rx = random > endX ? x : random;
		
		random = (int) ((Math.random() * startY) + startY);
		int ry = random > endY ? y : random;
		return new DefaultPoint(rx,ry);
	}
}
