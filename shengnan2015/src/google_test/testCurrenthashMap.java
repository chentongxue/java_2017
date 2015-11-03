package google_test;

import java.util.Map;

import com.google.common.collect.Maps;

public class testCurrenthashMap {
	public static void main(String args[]){
		
		Map<String,Byte> map = Maps.newConcurrentMap();
		System.out.println(map.remove("a"));
		
	}
	

}
