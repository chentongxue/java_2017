package remote_call;

import java.util.HashMap;

public class Service {
	private static final  HashMap<String, Integer> map = new HashMap<String, Integer>();
	static{
		map.put("a",1);
	}
	public Service(){
	}
	public Integer getTeam(String key){
		return map.get(key);
	}
	public static void main(String args[]){
		Service s = new Service();
		System.out.println(s.getTeam("a"));
	}
}
