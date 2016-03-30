package mapvalue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Maps;
/**
 * hashMap的key和value可以为null
 * ConcurrentHashMap的key和value都不可以为null
 * 不能传入 key = null
 *
 */
public class MapTestNullValye {
	public static void main(String[] args) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, "aaa");
		map.put(2, "bbb");
		map.put(null, null);
//		for (Student s : map.values()) {
//			s.setAge(100);
//		}
		System.out.println(map+"");
		  for (Map.Entry<Integer, String> entry : map.entrySet()) {
		  }

		System.out.println(map.get(null));
		
		//-----------
//		Map<Integer, String> map2 = Maps.newConcurrentMap() ;
//		map2.get(null);//err
		
		Map<Integer, String> map3 = new ConcurrentHashMap<>();
		map3.put(1, "aaa");
		map3.put(2, "bbb");
		map3.put(null, "ccc");
		System.out.println(map3+"");
//		map3.get(null);
	}
}
