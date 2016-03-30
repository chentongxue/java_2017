package sacred.alliance.magic.base;

public enum FrequencyType {
	
	FREQUENCY_TYPE_NONE((byte)0,"无限制"),
	FREQUENCY_TYPE_DAY((byte)1,"天"),
	FREQUENCY_TYPE_WEEK((byte)2,"周"),
	FREQUENCY_TYPE_MONTH((byte)3,"月"),
	FREQUENCY_TYPE_FOREVER((byte)4,"永久"),
	FREQUENCY_TYPE_COPY((byte)5,"每次副本"),
	;
	
	private final byte type;
	private final String name;
	
	FrequencyType(byte type,String name){
		this.type = type;
		this.name = name;
	}
	
	public final byte getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public static FrequencyType get(byte type){
		for(FrequencyType frequencyType : FrequencyType.values()){
			if(frequencyType.getType() == type){
				return frequencyType;
			}
		}
		return null;
	}
 }
