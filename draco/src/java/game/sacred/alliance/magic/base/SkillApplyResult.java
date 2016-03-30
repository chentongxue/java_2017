package sacred.alliance.magic.base;

public enum SkillApplyResult {

	HAS_NOT_SKILL(0,"无此技能"),
	SUCCESS(1,""),
	CD_NOT_ENOUGH(2,"CD时间未到"),
	WRONG_TARGET(3,"错误的目标对象"),
	MP_NOT_ENOUGH(4,"MP值不足"),
	DISTANCE_TOO_LONG(5,"距离太远"),
	MISS(6,"未击中目标"),
	DISTANCE_TOO_SHORT(7,"距离太近"),
	HP_NOT_ENOUGH(8,"HP值不足"),
	ERROR(9,"当前状态无法使用"),
	CURRENT_MAP_CANOT_USE(11,"当前地图不允许使用此技能"),
	SYSTEM_TRIGGER(12,"系统触发技能"),
	;
	
	int type;
	
	String result;
	
	SkillApplyResult(int type, String result){
		this.type = type;
		this.result = result;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
