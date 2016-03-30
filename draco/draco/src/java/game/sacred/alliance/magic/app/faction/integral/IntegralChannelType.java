package sacred.alliance.magic.app.faction.integral;

public enum IntegralChannelType {

	USE_GOODS(0),
	QUEST_AWARD(1),
	EXCHANGE(2),
    EXCHANGE_ROLLBACK(3),
    WARLORDS(4),
    DEGREE(5),
    
	;
	
	private final int type ;
	
	IntegralChannelType(int type){
		this.type = type ;
	}

	public int getType() {
		return type;
	}
	
}
