package set;

import java.util.Set;

import com.google.common.collect.Sets;

public class SetTest {
	public static void main(String args[]){
		System.out.println("hola ");
		Set<String> set = Sets.newHashSet();
		checkMapDuplicate(set,"a");
		checkMapDuplicate(set,"a");
		checkMapDuplicate(set,"b");
		checkMapDuplicate(set,"c");
		checkMapDuplicate(set,"d");
		checkMapDuplicate(set,"d");
		System.out.println(set);
	}
	private static void checkMapDuplicate(Set<String> set, String s){
		if(set.contains(s)){
			System.err.println(s);
		}
		set.add(s);
	}

}
