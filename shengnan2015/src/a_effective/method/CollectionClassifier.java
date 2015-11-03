package a_effective.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.math.BigInteger;
//165
/**
 * 对于重载，选择是静态的。对于覆盖，选择是动态的
 */
public class CollectionClassifier {
	public static String classify(Set<?> s){
		return "set";
	}
	public static String classify(List <?> s){
		return "list";
	}
	public static String classify(Collection<?> s){
		return "unkown collection";
	}
	public static void main(String args[]){
		Collection<?>[] collections = {
				new HashSet<String>(),
				new ArrayList<BigInteger>(),
				new HashMap<String, String>().values()};
		for(Collection<?> c : collections){
			System.out.println(classify(c));
		}
	}
}
/*
unkown collection
unkown collection
unkown collection
*/
