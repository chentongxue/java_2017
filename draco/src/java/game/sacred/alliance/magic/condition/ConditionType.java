package sacred.alliance.magic.condition;

public enum ConditionType {
	ROLE_LEVEL(0,false),
	RANK_RANKING(1,false),
	TITLE_ISHAVE(2,true),
	ROLE_ARENA_3V3_LEVEL(3,false),
	UNION_LEVEL(4,false),
	ROLE_VIP_LEVEL(5,false),
	UNION_lEAVE_TIME(6,false),
	;
	
	private final int type;
	private final boolean refresh ;
	
	ConditionType(int type,boolean refresh){
		this.type = type;
		this.refresh = refresh ;
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
	
	
	public boolean isRefresh() {
		return refresh;
	}

	public CondLogic createConditionAtrri() {
		switch(this) {
		case ROLE_LEVEL:
			return new CondAttriRoleLevel();
		case RANK_RANKING:
			return new CondAttriRankRanking();
		case TITLE_ISHAVE:
			return new CondTitleIsHave();
		case UNION_LEVEL:
			return new CondAttriUnionLevel();
		case UNION_lEAVE_TIME:
			return new CondAttriUnionJoinTime();
		case ROLE_VIP_LEVEL:
			return new CondAttriVipLevel();
		case ROLE_ARENA_3V3_LEVEL :
			return new CondAttriArena3v3Level() ;
		default:
			return null;
		}
	}
}
