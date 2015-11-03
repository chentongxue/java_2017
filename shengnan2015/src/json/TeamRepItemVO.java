package json;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TeamRepItemVO {
	public int repId;		
	public List<TeamReqMemberItemVO> members;
	public TeamRepItemVO(JSONObject jo) {
		this.repId = jo.getIntValue("repId");
		String memberStr = jo.getString("members");
		this.members = TeamReqMemberItemVO.jsonToList(memberStr);
	}
	public static void main(String args[]){
		System.out.println("hello");
		
		TeamReqMemberItemVO  vo = new TeamReqMemberItemVO();
		vo.humanId = 1;
		vo.level = 10;
		vo.name = "a";
		TeamReqMemberItemVO  vo2 = new TeamReqMemberItemVO();
		vo2.humanId = 2;
		vo2.level = 20;
		vo2.name = "b";
		List<TeamReqMemberItemVO> list = new ArrayList<TeamReqMemberItemVO>();
		
		list.add(vo);
		list.add(vo2);
		
		TeamRepItemVO vv = new TeamRepItemVO();
		vv.repId = 101;
		vv.members = list;
		String ss = TeamReqMemberItemVO.listToJson(list);
		JSONObject jo = new JSONObject();
		jo.put("repId",vv.repId);
		jo.put("members", ss);
		System.out.println("ja字符串="+jo.toString());
		
		TeamRepItemVO v = new TeamRepItemVO(jo);
		System.out.println(v.toString());
	}
	
	
	
	public TeamRepItemVO() {
	}
	
	public static List<TeamRepItemVO> jsonToList(String json) {
		List<TeamRepItemVO> result = new ArrayList<TeamRepItemVO>();
		if(json == null || "".equals(json) || "{}".equals(json)){
			return result;
		}
		JSONArray ja = JSON.parseArray(json);
		for (int i = 0; i < ja.size(); i++) {
			TeamRepItemVO vo = new TeamRepItemVO(ja.getJSONObject(i));
			result.add(vo);
		}
		
		return result;
	}
	
	
}
