package a_effective.annotation.sample2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
//150
/**
 * 捕获指定异常java.lang.ArithmeticException
 *
 */
public class RunTests {
	
	public static void main(String args[]) throws ClassNotFoundException{
		int c = 0;
		c = c/c;
		int tests = 0;
		int passed = 0;
		Class testClass = Class.forName("a_effective.annotation.sample2.Sample2");
		for(Method m : testClass.getDeclaredMethods()){
			if(m.isAnnotationPresent(ExceptionTest.class)){
			tests ++;
				try {
					m.invoke(null);
					System.out.printf("Test %s failed: no exception %n", m);
				} catch (InvocationTargetException e) {
					Throwable exc = e.getCause();
					Class<? extends Exception> excType = m.getAnnotation(ExceptionTest.class).value();
					if(excType.isInstance(exc)){//捕获java.lang.ArithmeticException异常
						passed ++;
					} else {
						System.err.printf("Test %s failed: expected %s, got %s%n", m, excType.getName(), exc);
					}
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