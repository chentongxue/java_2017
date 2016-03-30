package sacred.alliance.magic.base;

public enum SummonType {
	
	SUMMON_FACTION((byte)1,"门派召唤"),
	SUMMON_ROLE((byte)2,"个人召唤"),
	//SUMMON_CAMP_WAR((byte)3,"阵营战召唤")
	;
	
	private final byte type;
	private final String name;
	
	SummonType(byte type,String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public static SummonType get(byte type){
		for(SummonType summonType : SummonType.values()){
			if(summonType.getType() == type){
				return summonType;
			}
		}
		return null;
	}
 }
