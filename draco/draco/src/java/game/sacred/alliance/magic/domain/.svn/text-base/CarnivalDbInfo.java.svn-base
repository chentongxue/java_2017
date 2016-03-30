package sacred.alliance.magic.domain;

import sacred.alliance.magic.base.SaveDbStateType;
import lombok.Data;

public @Data class CarnivalDbInfo{
	private int id;
	private int activeId;
	private String targetId;//角色ID 门派ID
	private String name;//角色NAME 门派NAME
	private byte campId;//阵营ID
	private byte career;
	private int targetValue;//数值
	private int subTargetValue;//数值
	
	private SaveDbStateType saveDbStateType = SaveDbStateType.Initialize;//持久化状态（决定是否更新库）
	
	public CarnivalDbInfo(int activeId, String targetId, String name, byte campId, byte career){
		this.activeId = activeId;
		this.targetId = targetId;
		this.name = name;
		this.campId = campId;
		this.career = career;
	}
	
	public CarnivalDbInfo(){
		
	}
}
