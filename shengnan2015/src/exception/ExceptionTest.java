package exception;

import java.util.Arrays;

import clone.Person;

class Result{
	String result;
	Object detail;
	@Override
	public String toString() {
		return "Result [result=" + result + ", detail=" + detail.toString() + "]";
	}
	
} 
public class ExceptionTest {
	public static void main(String[] args) {
		try {
			Result r = new Result();
			System.out.println(r);
		} catch (Exception e) {
			String s = e.getMessage();
			System.out.println(s.toString());
		}
	}
}
