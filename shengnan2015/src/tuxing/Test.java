package tuxing;

public class Test {
	public static void main(String args[]){
		System.out.println("haha");
		double x = 10;
		double y = 10;
		double tg = 180*Math.atan2(10, 10)/Math.PI;
		System.err.println(tg);
		double r = Math.sqrt(x*x + y*y);
		System.err.println(r);
		
		
		double x1 = r * Math.cos(tg);
		System.err.println(tg+"aaa"+Math.cos(tg));
		double y1 = r * Math.sin(tg);
		System.out.println((int)x1+","+(int)y1);
	}

}
