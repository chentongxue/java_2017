package a_effective.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.math.BigInteger;
//166
/**
 * 比较报酬的策略是，永远不要导出两个具有相同数目的重载方法.
 * 如果使用可变参数，保守的做法是根本不需要重载它
 */
public class CollectionClassifier2 {
	public static String classify(Collection<?> s){
		return s instanceof Set ? "set":
			   s instanceof List? "List":
				   "unkown collection";
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
set
List
unkown collection
*/
