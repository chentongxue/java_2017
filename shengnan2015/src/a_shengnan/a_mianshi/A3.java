package a_shengnan.a_mianshi;

import java.util.Date;

/**
 */
public class A3 extends Date{
	private int a;
	public void test(){
		System.out.println(getClass().getSimpleName());//A3
	}
	public void test1(){
		System.out.println(super.getClass().getSimpleName());//A3
	}
	public void test2(){
		System.out.println(super.getClass().getSuperclass().getSimpleName());//Date
	}
	public static void main(String args[]){
		A3 a = new A3();
		a.test();
		a.test1();
		a.test2();
	}
}
