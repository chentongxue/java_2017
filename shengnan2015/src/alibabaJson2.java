import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import a_serial.data.Person;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class alibabaJson2 {
	public static void main(String args[]){
		
		Person p = new Person("a", 123);
		
		
		JSONObject jo = new JSONObject();
		jo.put(p.getName(), p.getHeight());
		
		JSONArray ja = new JSONArray();
		ja.add(jo);
		jo = new JSONObject();
		jo.put("A", "2008");
		jo.put("B", "2013");
		ja.add(jo);
		for (int i = 0; i < ja.size(); i++) {
			System.out.println("AAA"+ja.get(i));
		}
		System.out.println(ja.toJSONString());
		System.out.println(ja.toString());
	}
	
}
