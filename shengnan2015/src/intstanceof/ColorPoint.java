package intstanceof;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class ColorPoint extends Point {
	public final Color color;
	private static final Set<Point> unitCircle;
	static{
		unitCircle = new HashSet<Point>();
		unitCircle.add(new Point(1, 0));
		unitCircle.add(new Point(0, 1));
		unitCircle.add(new Point(-1, 0));
		unitCircle.add(new Point(0, -1));
	}
	public static boolean onUnitCircle(Point p){
		return unitCircle.contains(p);
	}
	//
	public ColorPoint(int x, int y, Color color) {
		super(x, y);
		this.color = color;
	}
	@Override
	public boolean equals(Object o){
		if((o == null) || o.getClass() != getClass()){
			return false;
		}
		Point p = (Point)o;
		return p.getX() ==this.getX() && p.getY() == p.getY();
	}
	public static void main(String args[]){
		ColorPoint cp = new ColorPoint(1, 2, Color.RED);
		Point p = new Point(1, 2);
		System.out.println(p.getClass()==cp.getClass());
		
		System.out.println(null instanceof Point);
		System.out.println(null instanceof Object);
		Integer a = new Integer(2);
		System.out.println(a.hashCode());
	}
}
