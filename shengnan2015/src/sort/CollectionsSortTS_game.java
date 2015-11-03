package sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class CollectionsSortTS_game {

	public static void main(String[] args) {
		List<Integer> as = Arrays.asList(1,3,2,4);
		List<Integer> list = new ArrayList<Integer>(as);
		Collections.sort(list);
		System.out.println(list);
		
		JSONArray jr = JSON.parseArray("[]");
		jr.add(2);
		jr.add(4);
		String js = jr.toJSONString();
		System.out.println("json×Ö·û´®Îª---  " + js);
		jr = JSON.parseArray(js);
//		for(Integer i:list){
//			if(jr.contains(i)){
//				i = 1;
//			}else{
//				i = 0;
//			}
//		}
		for(int i = 0; i < list.size() ;i++){
			if(jr.contains(list.get(i))){
				list.set(i, 1);
			}else{
				list.set(i, 0);
			}
		}
		System.out.println(list);
	}

}
