package comparebale.jingdianlizi;

import java.util.Arrays;
import java.util.Comparator;

public class Test {

	/**
	 * ±È½ÏÆ÷
	 */
	public static void main(String[] args) {
		String [] stringArray = {"hello","my","darling"};
		Arrays.sort(stringArray, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.length() - o2.length();
			}
		});
		System.out.println(Arrays.toString(stringArray));
	}

}
