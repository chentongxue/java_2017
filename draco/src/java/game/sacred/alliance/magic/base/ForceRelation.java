package sacred.alliance.magic.base;

public enum ForceRelation {
	//势力关系
	neutral((byte)0,"中立"),
	friend((byte)1,"友好"),
	enemy((byte)2,"敌对"),
	;
	
	ForceRelation(byte type,String name){
		this.type = type ;
		this.name = name ;
	}
	private final byte type ;
	private final String name ;
	public byte getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	
	public static ForceRelation getByType(byte type){
		switch (type) {
		case 0:
			return neutral;
		case 1:
			return friend ;
		case 2:
			return enemy ;
		}
		return neutral ;
	}
	
}
