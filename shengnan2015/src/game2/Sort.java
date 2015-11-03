package game2;

import java.util.Arrays;

public class Sort {
	public static void main(String args[]) {
		System.out.println("HELEO");
//		byte arr[] = { 7, 0, 6, 0, 5, 3, 2, 0 };
		byte arr[][] = {{ 8, 7, 6, 5, 4, 3, 2, 1 },{ 8, 7, 6, 5, 4, 3, 2, 1 },{ 8, 7, 6, 5, 4, 3, 2, 1 }};
		System.out.println("[1  2  3  4  5  6  7  8]");
		byte bakarr[][] = null;
		bakarr = arr;
		System.out.println(Arrays.toString(arr));
		
		System.out.println(Arrays.toString(bakarr));
		System.out.println(bakarr.length);
	}

}
