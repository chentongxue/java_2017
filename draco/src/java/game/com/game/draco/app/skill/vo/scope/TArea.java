package com.game.draco.app.skill.vo.scope;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

public class TArea extends Area {
	private final static String CAT = "_";
	private double INFINITY = 1e5;
	private double ESP = 1e-5;
	// T形起始边中心X坐标
	private int startX;
	// T形起始边中心Y坐标
	private int startY;
	// 底边半径长
	private int downLength;
	// 高度
	private int hight;
	// 顶边半径长
	private int upLength;

	private List<Point> polygon;

	public TArea(int attackerX, int attackerY, int downLength, int upLength,
			int hight, byte dir)// 构造方法
	{
		this.startX = attackerX;
		this.startY = attackerY;
		this.downLength = downLength;
		this.upLength = upLength;
		this.hight = hight;

		List<Point> polygon = new ArrayList<Point>();
		Point point = new Point();
		Point point1 = new Point();
		Point point2 = new Point();
		Point point3 = new Point();

		// if (getDir(dir) == Direction.UP.getType()) {
		// 左下角
		// point.setX(startX - downLength);
		// point.setY(startY);
		// // 右下角
		// point1.setX(startX + downLength);
		// point1.setY(startY);
		// // 左上角
		// point2.setX(startX - upLength);
		// point2.setY(startY - hight);
		// // 右上角
		// point3.setX(startX + upLength);
		// point3.setY(startY - hight);

		// } else if (getDir(dir) == Direction.LEFT.getType()) {
		// // 左下角
		// point.setX(startX);
		// point.setY(startY + downLength);
		// // 右下角
		// point1.setX(startX);
		// point1.setY(startY - downLength);
		// // 左上角
		// point2.setX(startX - hight);
		// point2.setY(startY + upLength);
		// // 右上角
		// point3.setX(startX - hight);
		// point3.setY(startY - upLength);
		//
		// } else if (getDir(dir) == Direction.DOWN.getType()) {
		// // 左下角
		// point = new Point();
		// point.setX(startX - downLength);
		// point.setY(startY);
		// // 右下角
		// point1.setX(startX + downLength);
		// point1.setY(startY);
		// // 左上角
		// point2.setX(startX + upLength);
		// point2.setY(startY + hight);
		// // 右上角
		// point3.setX(startX - upLength);
		// point3.setY(startY + hight);
		//
		// } else if (getDir(dir) == Direction.RIGHT.getType()) {
		// // 左下角
		point.setX(startX);
		point.setY(startY - downLength);
		// 右下角
		point1.setX(startX);
		point1.setY(startY + downLength);
		// 左上角
		point2.setX(startX + hight);
		point2.setY(startY - upLength);
		// 右上角
		point3.setX(startX + hight);
		point3.setY(startY + upLength);
		// }

		short atkDir = (short) getDir(dir);
		calcNewPoint(point, getCenterPoint(), -atkDir);
		calcNewPoint(point1, getCenterPoint(), -atkDir);
		calcNewPoint(point2, getCenterPoint(), -atkDir);
		calcNewPoint(point3, getCenterPoint(), -atkDir);

		polygon.add(point);
		polygon.add(point1);
		polygon.add(point2);
		polygon.add(point3);
		this.polygon = polygon;
	}

	private void calcNewPoint(Point p, Point pCenter, float angle) {
		// calc arc 
		float l = (float) (((angle) * Math.PI) /180);
		
		//sin/cos value
		float cosv = (float) Math.cos(l);
		float sinv = (float) Math.sin(l);

		float newX=(p.getX() - pCenter.getX())*cosv + (p.getY() - pCenter.getY())*sinv + pCenter.getX();
		float newY=(p.getY() - pCenter.getY())*cosv - (p.getX() - pCenter.getX())*sinv + pCenter.getY();
		p.setX((int)newX);
		p.setY((int)newY);
	}

	@Override
	public boolean inArea(AbstractRole attacker, int x, int y, byte dir) {
		if (Util.isEmpty(polygon)) {
			return false;
		}
		Point checkpoint = new Point();
		checkpoint.setX(x);
		checkpoint.setY(y);
		boolean flag = inPolygon(polygon, checkpoint);
		return flag;
	}

	@Override
	public String getKey() {
		return "TArea" + CAT + startX + CAT + startY + CAT + downLength + CAT
				+ upLength + CAT + hight;
	}

	@Override
	public Point getCenterPoint() {
		return new Point("", startX, startY);
	}

	double multiply(Point p1, Point p2, Point p0) {
		return ((p1.getX() - p0.getX()) * (p2.getY() - p0.getY()) - (p2.getX() - p0
				.getX()) * (p1.getY() - p0.getY()));
	}

	// 判断线段是否包含点point

	private boolean isOnline(Point point, LineSegment line) {
		return ((Math.abs(multiply(line.pt1, line.pt2, point)) < ESP)
				&&
				((point.getX() - line.pt1.getX())
						* (point.getX() - line.pt2.getX()) <= 0) &&
		((point.getY() - line.pt1.getY()) * (point.getY() - line.pt2.getY()) <= 0));
	}

	// 判断线段相交
	private boolean intersect(LineSegment L1, LineSegment L2) {
		return ((Math.max(L1.pt1.getX(), L1.pt2.getX()) >= Math.min(
				L2.pt1.getX(), L2.pt2.getX()))
				&&
				(Math.max(L2.pt1.getX(), L2.pt2.getX()) >= Math.min(
						L1.pt1.getX(), L1.pt2.getX()))
				&&
				(Math.max(L1.pt1.getY(), L1.pt2.getY()) >= Math.min(
						L2.pt1.getY(), L2.pt2.getY()))
				&&
				(Math.max(L2.pt1.getY(), L2.pt2.getY()) >= Math.min(
						L1.pt1.getY(), L1.pt2.getY()))
				&&
				(multiply(L2.pt1, L1.pt2, L1.pt1)
						* multiply(L1.pt2, L2.pt2, L1.pt1) >= 0) &&
		(multiply(L1.pt1, L2.pt2, L2.pt1) * multiply(L2.pt2, L1.pt2, L2.pt1) >= 0)

		);
	}

	class LineSegment {
		public Point pt1;
		public Point pt2;

		public LineSegment() {
			this.pt1 = new Point();
			this.pt2 = new Point();
		}
	}

	public boolean inPolygon(List<Point> polygon, Point point)	{
		int n = polygon.size();
		int count = 0;
		LineSegment line = new LineSegment();

		line.pt1 = point;
		line.pt2.setY(point.getY());
		line.pt2.setX((int)-INFINITY);
		for (int i = 0; i < n; i++) {
			// 得到多边形的一条边
			LineSegment side = new LineSegment();
			side.pt1 = polygon.get(i);
			side.pt2 = polygon.get((i + 1) % n);
			if (isOnline(point, side)) {
				return false;
			}
			// 如果side平行x轴则不作考虑
			if (Math.abs(side.pt1.getY() - side.pt2.getY()) < ESP) {
				continue;
			}
			if (isOnline(side.pt1, line)) {
				if (side.pt1.getY() > side.pt2.getY())
					count++;
			} else if (isOnline(side.pt2, line)) {
				if (side.pt2.getY() > side.pt1.getY())
					count++;
			} else if (intersect(line, side)) {
				count++;
			}
		}
		if (count % 2 == 1) {
			return true;
		}
		else {
			return false;
		}
	}

}
