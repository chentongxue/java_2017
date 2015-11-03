package listMapSet;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
public class ConcurrenthashMapTest {

	private ConcurrentHashMap<Integer,Integer>  map = new ConcurrentHashMap<Integer,Integer>();
	
	public static void main(String[] args) {
		new ConcurrenthashMapTest().add();
		

	}
	public void add(){
		map.put(0, 5);
		map.put(1, 5);
		map.put(2, 5);
		map.put(3, 5);
		map.put(4, 5);
		map.put(5, 5);
		map.put(6, 5);
		map.put(7, 5);
		map.put(8, 5);
		map.put(9, 5);
		map.put(10, 5);
		map.put(11, 5);
		System.out.println(map);
		addRewardsPointsMap(0,-6);
		addRewardsPointsMap(12,-6);
		System.out.println(map);
	}
	//Ìí¼Ó»ý·Ö
	public ConcurrentHashMap<Integer,Integer> addRewardsPointsMap(Integer  roleId,Integer rewardsPoints){
		if (!map.containsKey(roleId))
		       map.put(roleId, rewardsPoints);
		   else
		       map.put(roleId, rewardsPoints+map.get(roleId));
		return map;
	}
}
