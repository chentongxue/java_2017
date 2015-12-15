import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;


public class alibabaJson {
	public static void main(String args[]){
		JSONArray questIds = toJSONArray("[1,2]");
		System.out.println(questIds.contains(1));
		System.out.println(Arrays.toString(questIds.toArray()));
		System.out.println(questIds.size());
//		System.out.println(questIds.toArray(a));
	}
	public static JSONArray toJSONArray(String str) {
		return JSON.parseArray(str);
	}
	
	public static List<Integer> strToIntList(String str) {
		if(str == null  || str.isEmpty()) return new ArrayList<Integer>();
		
		List<Integer> l = new ArrayList<Integer>();
		String[] o = str.split(",");
		for (String s : o) {
			if(s.isEmpty()) continue;
			l.add(Integer.parseInt(s));
		}
		
		return l;
	}
}
