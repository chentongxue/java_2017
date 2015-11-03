package json;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TeamReqMemberItemVO{
	public int humanId;		
	public String name;					//类型
	public int level;					//当前完成次数
	public TeamReqMemberItemVO(JSONObject jo) {
		this.humanId = jo.getIntValue("humanId");
		this.name = jo.getString("name");
		this.level = jo.getIntValue("level");
	}
	
	public TeamReqMemberItemVO() {
	}
	
	public static List<TeamReqMemberItemVO> jsonToList(String json) {
		List<TeamReqMemberItemVO> result = new ArrayList<TeamReqMemberItemVO>();
		if(json == null || "".equals(json) || "{}".equals(json)){
			return result;
		}
		JSONArray ja = JSON.parseArray(json);
		for (int i = 0; i < ja.size(); i++) {
			TeamReqMemberItemVO vo = new TeamReqMemberItemVO(ja.getJSONObject(i));
			result.add(vo);
		}
		
		return result;
	}
	public static String listToJson(List<TeamReqMemberItemVO> questList){
		JSONArray ja = new JSONArray();
		for (TeamReqMemberItemVO vo : questList) {
			JSONObject jo = new JSONObject();
			jo.put("humanId", vo.humanId);
			jo.put("name", vo.name);
			jo.put("level", vo.level);
			ja.add(jo);
		}
		return ja.toJSONString();
	}
	
}
