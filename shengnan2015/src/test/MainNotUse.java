package test;

import java.util.Arrays;

/**
 */
public class MainNotUse {

	public static void main(String[] args) {
		try {
			int a = 1;
			int b = 0;
			System.out.println(a/b);
		} catch (Exception e) {
			System.out.println(Arrays.toString(e.getStackTrace()));
		}
	}
}
