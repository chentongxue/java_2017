package sacred.alliance.magic.base;

public enum FactionFuncStatus {
	
	Not_Open((byte)0,"未开启"),
	Opened((byte)1,"已开启"),
	Underway((byte)2,"进行中"),
	
	;
	
	private final byte type;
	private final String name;
	
	FactionFuncStatus(byte type,String name){
		this.type = type;
		this.name = name;
	}
	public final byte getType(){
		return type;
	}
	public String getName(){
		return name;
	}
	
	public static FactionFuncStatus get(byte type){
		for(FactionFuncStatus func : FactionFuncStatus.values()){
			if(func.getType() == type){
				return func;
			}
		}
		return null;
	}

 }
