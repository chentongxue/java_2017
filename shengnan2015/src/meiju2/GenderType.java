package meiju2;

import java.util.HashMap;
import java.util.Map;

public enum GenderType {  
	UNKNOWN((byte)0),
	MALE((byte)1),
	FEMALE((byte)2), 
	;
	
	private byte value;
	
	private static final Map<String ,GenderType> stringToEnum = new HashMap<String, GenderType>();
	static{
		for(GenderType t:values())
			stringToEnum.put(t.toString(), t);
	}
	public static GenderType fromString(String symbol){
		return stringToEnum.get(symbol);
	}
	GenderType(byte type) {
		this.value = type; 
	} 

    public int getValue() {
		return value;
	}

    public String toString(){
    	return this.name()+"--";
    }
	public static GenderType valueOf(byte value) { 
		GenderType userType = null; 
		switch (value) { 
		case 1: 
			userType = MALE;
            break;
		case 2: 
			userType = FEMALE; 
			break;
		default:
			userType = UNKNOWN;
            break;
		} 
		return userType;
	}
	public static GenderType valueOf2(byte value){
		for(GenderType v : values()){
			if(value == v.getValue()){
				return v;
			}
		}
		return null;
	}
    public static void main(String[] args) throws InterruptedException
	{
    	GenderType t = GenderType.valueOf("MALE");//可以得到
    	System.out.println(t);
    	GenderType t2 = GenderType.fromString("FEMALE--");//可以得到
    	System.out.println(t2);
	}
	
}



