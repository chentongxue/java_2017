package a_effective.annotation.sample1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//
public class RunTests {
	
	public static void main(String args[]) throws ClassNotFoundException{
		int tests = 0;
		int passed = 0;
		Class testClass = Class.forName("a_effective.annotation.sample1.Sample");
		for(Method m : testClass.getDeclaredMethods()){
			//是否使用注解
			if(m.isAnnotationPresent(Test.class)){
			tests ++;
				try {
					m.invoke(null);
					passed++;
				} catch (InvocationTargetException e) {
					Throwable exc = e.getCause();
					System.out.println(m + "failed:" + exc);
				} catch (Exception e) {
					System.out.println("INVALID @Test: " + m + ":" + e);
				}
			}
		}
		System.out.printf("Passed:%d, Failed:%d %n", passed, tests - passed);
	}
}
/*
public static void a_effective.annotation.Sample.m3()failed:java.lang.RuntimeException: Boom
INVALID @Test: public void a_effective.annotation.Sample.m5():java.lang.NullPointerException
public static void a_effective.annotation.Sample.m7()failed:java.lang.RuntimeException: Crash
Passed:1, Failed:3 
*/