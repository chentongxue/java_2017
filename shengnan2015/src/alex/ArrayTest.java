package alex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayTest {
	public static void main(String args[]){
		List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		
		Integer [] arr = list.toArray(new Integer[list.size()]);
		int [] arr1 = list.toArray(new int[list.size()]);
		Arrays.binarySearch(a, key)
		System.out.println(Arrays.toString(arr));
		System.out.println(Arrays.toString(arr1));
	}
}
