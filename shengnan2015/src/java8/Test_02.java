package java8;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *	Lambda表达式
 */
public class Test_02 {

	public static void main(String[] args) {
		List<String> names = Arrays.asList("william", "with", "lishengnan");
/*		001
 * 		Collections.sort(names, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return b.compareTo(a);
			}
		});*/
		
/*		002
		Collections.sort(names, (String a, String b)->{
			return b.compareTo(a);
		});*/
		
		
		/**
		 * 003
		 */
		Collections.sort(names, (String a, String b)-> b.compareTo(a));
		/**
		 * 004 Java编译器可以自动推导出参数类型，所以你可以不用再写一次类型
		 */
		Collections.sort(names, (a, b)-> b.compareTo(a));
		System.out.println(names);
	}
}
