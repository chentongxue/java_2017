package listMapSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Lists;
/**
 * 对map里的东西排序
 * @author mofun030601
 *
 */
public class MapTestSort {

	public static void main(String[] args) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a",1);
		map.put("b",2);
		map.put("c",3);
		System.out.println(map);
		
		ArrayList list = Lists.newArrayList(map.values());
		System.out.println(list);
		Collections.sort(list);
		System.out.println(list);
		System.out.println(map);
	}
}
