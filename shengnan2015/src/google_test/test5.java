package google_test;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap.Builder;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;

public class test5 {
	public static void main(String[] args) {
		test();
		test1();
//		test2();
	}

	private static void test() {
		int[] numbers = { 1, 2, 3, 4, 5 };
		String ss = Joiner.on("\n").join(Ints.asList(numbers));
		System.out.println(ss);
	}
	private static void test1() {
		String[] numbers = { "a1", "a2", "a3", "a4", "a5" };
		String ss = Joiner.on("\n").join(numbers);
		System.out.println(ss);
	}
//	private static void test2() {
//		int[] numbers = { 1, 2, 3, 4, 5 };
//		String ss = Joiner.on("\n").join(Ints.asList(numbers));
//		System.out.println(ss);
//	}
}
