package jicehng;

import java.util.Arrays;

public class B extends A {
	public void say(){
		super.say();
		System.out.println("I am B");
	}
	public static void main(String args[]){
		A a = new B();
		a.say();
		
		boolean b = isInstanceof(B.class, A.class);
		boolean b2 = isInstanceof(a.getClass(), B.class);
		System.out.println(b);
		System.out.println(b2);
			Arrays.asList(a);
	}
	public static boolean isInstanceof(Class<?> c, Class<?> p) {
		return p.isAssignableFrom(c);
	}
}
