package sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CollectionsSortTS {

	public static void main(String[] args) {
		int arr[] = new int[] {1,3,2,4};
		int arr0[] = {1,3,2,4};
		List<Integer> as = Arrays.asList(1,3,2,4);
		List<Integer> list = new ArrayList<Integer>(as);
		Collections.sort(list);
		System.out.println(list);
		
		
		String[] s = new String[] {"1a", "b2"};
		List ss = Arrays.asList(s);
		System.out.println(ss);
		System.out.println(ss.size());
	}

}
