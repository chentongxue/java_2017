package and;

public class Test {

	public static void main(String [] args){
//		System.out.println(getAngle(-1,-1));
		int a = 10;
		float f = a*360/256;
		System.out.println(f+"");
		System.out.println((a&0xff)*360/256);//
		int b = ((a&0xff)*360)>>8;
		System.out.println(b);
		System.out.println((((b&0xff)/360)<<8));
		
		
//		int a = 10;
//		int b = a*360>>8;
//		int c = (a*360)&0xff;
//		System.out.println(b);
//		System.out.println(c);
//		System.out.println((b<<8)+c);
//		System.out.println(c);
	}
}
