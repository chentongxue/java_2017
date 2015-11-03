package google_test;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class test2 {
	public static void main(String[] args) {
		Multimap<String, String> map = ArrayListMultimap.create();  
		  
		 System.err.println(isEmpty(map));
		  // Adding some key/value  
		  map.put("Fruits", "Bannana");  
		  map.put("Fruits", "Apple");  
		  map.put("Fruits", "Pear");  
		  map.put("Vegetables", "Carrot");  
		  System.err.println(isEmpty(map));
		  // Getting the size  
		  int size = map.size();  
		  System.out.println(size);  // 4  
		  
		   
		  Collection<String> fruits = map.get("Fruits");  
		  System.out.println(fruits); // [Bannana, Apple, Pear]  
		  
		  Collection<String> vegetables = map.get("Vegetables");  
		  System.out.println(vegetables); // [Carrot]  
		  
		  // 循环输出  
		  for(String value : map.values()) {  
		   System.out.println(value);  
		  }  
		  
		  // 移走某个值  
		  map.remove("Fruits","Pear");  
		  System.out.println(map.get("Fruits")); // [Bannana, Pear]  
		  
		  //移走某个KEY的所有对应value  
		  map.removeAll("Fruits");  
		  System.out.println(map.get("Fruits")); // [] (Empty Collection!) 
		  
		  System.out.println("-----------");
	}
	public static boolean isEmpty(Map<?, ?> map) {
		if (map == null) {
			return true;
		} else if (map.keySet().size() == 0) {
			return true;
		} else {
			return false;
		}
	}
	public static boolean isEmpty(Multimap<?, ?> map) {
		return isEmpty(map.asMap());
	}
}
