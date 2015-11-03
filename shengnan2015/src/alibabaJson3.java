import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import a_serial.data.Person;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class alibabaJson3 {
	public static void main(String args[]){
		
		Person p = new Person("a", 123);
		
		
		JSONObject jo = new JSONObject();
		jo.put(p.getName(), p.getHeight());
		int a = jo.getIntValue("a");
		System.out.println(a);
	}
	
}
