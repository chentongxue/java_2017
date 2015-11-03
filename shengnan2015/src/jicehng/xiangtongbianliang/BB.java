package jicehng.xiangtongbianliang;

import jicehng.A;
import jicehng.B;

public class BB extends AA {
	public String a = "BB.a";
	public static String aa ="BB.aa";
	public static void main(String args[]){
		AA a = new BB();
		System.out.println(a.a+"--"+a.aa);
		AA a1 = new BB();
		BB b = (BB)a1;
		System.out.println(b.a+"--"+b.aa);
	}
}
