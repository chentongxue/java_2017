package sacred.alliance.magic.condition;

public enum ConditionType {
	ROLE_LEVEL(1),
	RANK_RANKING(2),
	TITLE_ISHAVE(3),
	FACTION_LEVEL(4),
	ROLE_VIP_LEVEL(5),
	;
	private final int type;
	
	ConditionType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public static ConditionType get(int type){
		for(ConditionType v : values()){
			if(type == v.getType()){
				return v;
			}
		}
		return null;
	}
	
	public CondLogic createConditionAtrri() {
		switch(this) {
		case ROLE_LEVEL:
			return new CondAttriRoleLevel();
		case RANK_RANKING:
			return new CondAttriRankRanking();
		case TITLE_ISHAVE:
			return new CondTitleIsHave();
		case FACTION_LEVEL:
			return new CondAttriFactionLevel();
		case ROLE_VIP_LEVEL:
			return new CondAttriVipLevel();
		default:
				return null;
		}
	}
}
