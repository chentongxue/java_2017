package sacred.alliance.magic.base;

public enum FactionIntegralLogType {
	
	All((byte)-1,"全部"),
	Consume((byte)0,"消耗"),
	Income((byte)1,"收入"),
	
	;
	
	private final byte type;
	private final String name;
	
	FactionIntegralLogType(byte type,String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public static FactionIntegralLogType get(byte type){
		for(FactionIntegralLogType power : FactionIntegralLogType.values()){
			if(power.getType() == type){
				return power;
			}
		}
		return null;
	}

 }
