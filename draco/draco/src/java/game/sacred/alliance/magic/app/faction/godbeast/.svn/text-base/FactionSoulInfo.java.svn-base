package sacred.alliance.magic.app.faction.godbeast;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class FactionSoulInfo implements KeySupport<Integer>{
	private int id;//神兽ID
	private String name;//神兽名称
	//Map<等级,属性对像>
	private Map<Byte,FactionSoulAttri> attriMap = new LinkedHashMap<Byte,FactionSoulAttri>();
	
	/**获取对应等级的属性对像*/
	public FactionSoulAttri getFactionSoulAttri(byte level){
		if(attriMap.size() == 0){
			return null;
		}
		return attriMap.get(level);
	}
	
	/**得到满足等级的列表*/
	public FactionSoulAttri getSoulAttriByFactionLevel(){
		if(attriMap.size() == 0){
			return null;
		}
		return this.getFactionSoulAttri((byte) 1);
	}
	
	/**初始化神兽属性*/
	public void initAttriMap(FactionSoulAttri obj){
		if(null == obj){
			return ;
		}
		attriMap.put((byte) obj.getLevel(), obj);
	}

	@Override
	public Integer getKey() {
		return this.id;
	}
}
