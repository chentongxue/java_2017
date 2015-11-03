package mapvalue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Maps;
/**
 * 不能传入 key = null
 * @author mofun030601
 *
 */
public class MapTestNullValye {
	public static void main(String[] args) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, "aaa");
		map.put(2, "bbb");
		map.put(3, null);
//		for (Student s : map.values()) {
//			s.setAge(100);
//		}
		System.out.println(map);
		  for (Map.Entry<Integer, String> entry : map.entrySet()) {
		  }

		System.out.println(map.get(null));
		
		//-----------
//		Map<Integer, String> map2 = Maps.newConcurrentMap() ;
//		map2.get(null);//err
		
		Map<Integer, String> map3 = new ConcurrentHashMap<>();
		map3.put(1, "aaa");
		map3.put(2, "bbb");
		map3.put(3, null);
		System.out.println(map3);
//		map3.get(null);
	}
}
