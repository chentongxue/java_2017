package a_effective.annotation.sample1;


public class Sample {
	@Test public static void m1(){}
	public static void m2(){}
	@Test public static void m3(){
		throw new RuntimeException("Boom");
	}
	public static void m4(){}
	@Test  public void m5(){}
	public static void m6(){}
	@Test public static void m7(){
		throw new RuntimeException("Crash");
	}
	public static void m8(){}
}
/*
public static void a_effective.annotation.Sample.m3()failed:java.lang.RuntimeException: Boom
INVALID @Test: public void a_effective.annotation.Sample.m5():java.lang.NullPointerException
public static void a_effective.annotation.Sample.m7()failed:java.lang.RuntimeException: Crash
Passed:1, Failed:3 
*/
