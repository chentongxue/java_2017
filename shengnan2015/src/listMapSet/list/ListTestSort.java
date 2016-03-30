package listMapSet.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;

public class ListTestSort {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(5);
		list.add(4);
		list.add(2);
		list.add(2);
		list.add(3);
		Collections.sort(list);
		TreeSet<Integer> set = new TreeSet<Integer>();
		set.add(1);
		set.add(5);
		set.add(4);
		set.add(2);
		set.add(2);
		set.add(3);
	
		System.out.println(list);
		System.err.println(set);
		for (Integer a : set) {
			System.err.println(a);
		}
	}

}
