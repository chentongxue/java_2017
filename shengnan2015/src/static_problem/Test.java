package static_problem;

import java.util.Arrays;

public class Test {
	public static void main(String args[]) throws ClassNotFoundException{
		Class.forName("static_problem.TestStatic");
//		TestStatic.hello();
		System.out.println(Arrays.toString(args));
	}
}
